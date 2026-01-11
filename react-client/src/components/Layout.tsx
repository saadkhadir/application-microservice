import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Package, ShoppingCart, LogOut, User, Shield, Menu, X } from 'lucide-react';
import { useState } from 'react';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { isAuthenticated, username, isAdmin, login, logout } = useAuth();
  const location = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navItems = [
    { path: '/products', label: 'Produits', icon: Package, visible: true },
    { path: '/orders', label: 'Commandes', icon: ShoppingCart, visible: isAuthenticated },
  ];

  const isActive = (path: string) => location.pathname === path;

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="sticky top-0 z-50 border-b border-border bg-card/95 backdrop-blur supports-[backdrop-filter]:bg-card/60">
        <div className="container mx-auto px-4">
          <div className="flex h-16 items-center justify-between">
            {/* Logo */}
            <Link to="/" className="flex items-center gap-2">
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary">
                <Package className="h-5 w-5 text-primary-foreground" />
              </div>
              <span className="hidden font-serif text-xl font-bold text-foreground sm:block">
                GestionPro
              </span>
            </Link>

            {/* Desktop Navigation */}
            <nav className="hidden items-center gap-1 md:flex">
              {navItems.filter(item => item.visible).map((item) => (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`flex items-center gap-2 rounded-lg px-4 py-2 text-sm font-medium transition-colors ${
                    isActive(item.path)
                      ? 'bg-primary text-primary-foreground'
                      : 'text-muted-foreground hover:bg-accent hover:text-accent-foreground'
                  }`}
                >
                  <item.icon className="h-4 w-4" />
                  {item.label}
                </Link>
              ))}
            </nav>

            {/* User Actions */}
            <div className="flex items-center gap-3">
              {isAuthenticated ? (
                <>
                  <div className="hidden items-center gap-2 sm:flex">
                    {isAdmin && (
                      <span className="flex items-center gap-1 rounded-full bg-primary/10 px-2 py-1 text-xs font-medium text-primary">
                        <Shield className="h-3 w-3" />
                        Admin
                      </span>
                    )}
                    <span className="flex items-center gap-1 text-sm text-muted-foreground">
                      <User className="h-4 w-4" />
                      {username}
                    </span>
                  </div>
                  <Button variant="outline" size="sm" onClick={logout}>
                    <LogOut className="mr-2 h-4 w-4" />
                    Déconnexion
                  </Button>
                </>
              ) : (
                <Button onClick={login}>
                  Connexion
                </Button>
              )}

              {/* Mobile Menu Toggle */}
              <Button
                variant="ghost"
                size="icon"
                className="md:hidden"
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              >
                {mobileMenuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
              </Button>
            </div>
          </div>

          {/* Mobile Navigation */}
          {mobileMenuOpen && (
            <nav className="border-t border-border py-4 md:hidden">
              {navItems.filter(item => item.visible).map((item) => (
                <Link
                  key={item.path}
                  to={item.path}
                  onClick={() => setMobileMenuOpen(false)}
                  className={`flex items-center gap-2 rounded-lg px-4 py-3 text-sm font-medium transition-colors ${
                    isActive(item.path)
                      ? 'bg-primary text-primary-foreground'
                      : 'text-muted-foreground hover:bg-accent'
                  }`}
                >
                  <item.icon className="h-4 w-4" />
                  {item.label}
                </Link>
              ))}
              {isAuthenticated && (
                <div className="mt-2 flex items-center gap-2 px-4 pt-2 text-sm text-muted-foreground sm:hidden">
                  <User className="h-4 w-4" />
                  {username}
                  {isAdmin && (
                    <span className="ml-2 rounded-full bg-primary/10 px-2 py-0.5 text-xs font-medium text-primary">
                      Admin
                    </span>
                  )}
                </div>
              )}
            </nav>
          )}
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-8">
        {children}
      </main>
    </div>
  );
};

export default Layout;
