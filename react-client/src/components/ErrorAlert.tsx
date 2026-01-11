import React from 'react';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';
import { AlertCircle, Lock, RefreshCw } from 'lucide-react';
import { useAuth } from '@/contexts/AuthContext';

interface ErrorAlertProps {
  title?: string;
  message: string;
  status?: number;
  onRetry?: () => void;
}

const ErrorAlert: React.FC<ErrorAlertProps> = ({ title, message, status, onRetry }) => {
  const { login } = useAuth();

  const getIcon = () => {
    if (status === 401 || status === 403) {
      return <Lock className="h-4 w-4" />;
    }
    return <AlertCircle className="h-4 w-4" />;
  };

  const getTitle = () => {
    if (title) return title;
    if (status === 401) return 'Authentification requise';
    if (status === 403) return 'Accès refusé';
    return 'Erreur';
  };

  return (
    <Alert variant="destructive" className="my-4">
      {getIcon()}
      <AlertTitle>{getTitle()}</AlertTitle>
      <AlertDescription className="mt-2">
        <p>{message}</p>
        <div className="mt-4 flex gap-2">
          {status === 401 && (
            <Button variant="outline" size="sm" onClick={login}>
              Se connecter
            </Button>
          )}
          {onRetry && (
            <Button variant="outline" size="sm" onClick={onRetry}>
              <RefreshCw className="mr-2 h-4 w-4" />
              Réessayer
            </Button>
          )}
        </div>
      </AlertDescription>
    </Alert>
  );
};

export default ErrorAlert;
