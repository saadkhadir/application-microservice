import React, { createContext, useContext, useState, useEffect, useCallback, ReactNode } from 'react';
import keycloak from '@/lib/keycloak';
import type { UserRole } from '@/types';

interface AuthContextType {
  isAuthenticated: boolean;
  isLoading: boolean;
  token: string | null;
  username: string | null;
  roles: UserRole[];
  isAdmin: boolean;
  isClient: boolean;
  login: () => void;
  logout: () => void;
  getToken: () => Promise<string | null>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [token, setToken] = useState<string | null>(null);
  const [username, setUsername] = useState<string | null>(null);
  const [roles, setRoles] = useState<UserRole[]>([]);

  const extractRoles = useCallback((): UserRole[] => {
    const extractedRoles: UserRole[] = [];
    
    // Check realm roles
    const realmRoles = keycloak.realmAccess?.roles || [];
    if (realmRoles.includes('admin') || realmRoles.includes('ADMIN')) {
      extractedRoles.push('admin');
    }
    if (realmRoles.includes('client') || realmRoles.includes('CLIENT') || realmRoles.includes('user') || realmRoles.includes('USER')) {
      extractedRoles.push('client');
    }

    // Check resource roles
    const resourceRoles = keycloak.resourceAccess?.['microservices-client']?.roles || [];
    if (resourceRoles.includes('admin') || resourceRoles.includes('ADMIN')) {
      if (!extractedRoles.includes('admin')) extractedRoles.push('admin');
    }
    if (resourceRoles.includes('client') || resourceRoles.includes('CLIENT') || resourceRoles.includes('user') || resourceRoles.includes('USER')) {
      if (!extractedRoles.includes('client')) extractedRoles.push('client');
    }

    // Default to client if no roles found
    if (extractedRoles.length === 0) {
      extractedRoles.push('client');
    }

    return extractedRoles;
  }, []);

  useEffect(() => {
    const initKeycloak = async () => {
      try {
        const authenticated = await keycloak.init({
          onLoad: 'check-sso',
          silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
          pkceMethod: 'S256',
        });

        setIsAuthenticated(authenticated);
        
        if (authenticated) {
          setToken(keycloak.token || null);
          setUsername(keycloak.tokenParsed?.preferred_username || null);
          setRoles(extractRoles());
        }
      } catch (error) {
        console.error('Keycloak initialization failed:', error);
      } finally {
        setIsLoading(false);
      }
    };

    initKeycloak();

    // Token refresh
    const refreshInterval = setInterval(() => {
      if (keycloak.authenticated) {
        keycloak.updateToken(70).then((refreshed) => {
          if (refreshed) {
            setToken(keycloak.token || null);
          }
        }).catch(() => {
          console.error('Failed to refresh token');
        });
      }
    }, 60000);

    return () => clearInterval(refreshInterval);
  }, [extractRoles]);

  const login = useCallback(() => {
    keycloak.login();
  }, []);

  const logout = useCallback(() => {
    keycloak.logout({ redirectUri: window.location.origin });
  }, []);

  const getToken = useCallback(async (): Promise<string | null> => {
    if (!keycloak.authenticated) return null;
    
    try {
      await keycloak.updateToken(30);
      return keycloak.token || null;
    } catch {
      console.error('Failed to refresh token');
      return null;
    }
  }, []);

  const isAdmin = roles.includes('admin');
  const isClient = roles.includes('client');

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated,
        isLoading,
        token,
        username,
        roles,
        isAdmin,
        isClient,
        login,
        logout,
        getToken,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
