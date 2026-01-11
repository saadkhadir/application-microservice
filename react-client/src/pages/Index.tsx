import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Package, ShoppingCart, Shield, ArrowRight, LogIn } from 'lucide-react';
import LoadingSpinner from '@/components/LoadingSpinner';

const Index: React.FC = () => {
  const { isAuthenticated, isLoading, isAdmin, username, login } = useAuth();

  if (isLoading) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <LoadingSpinner size="lg" message="Initialisation de l'authentification..." />
      </div>
    );
  }

  return (
    <div className="flex flex-col items-center">
      {/* Hero Section */}
      <section className="w-full py-12 text-center md:py-20">
        <div className="mx-auto max-w-3xl">
          <div className="mb-6 flex justify-center">
            <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-primary shadow-lg">
              <Package className="h-8 w-8 text-primary-foreground" />
            </div>
          </div>
          <h1 className="font-serif text-4xl font-bold tracking-tight text-foreground md:text-5xl lg:text-6xl">
            Gestion des Produits & Commandes
          </h1>
          <p className="mx-auto mt-6 max-w-2xl text-lg text-muted-foreground md:text-xl">
            Plateforme complète pour gérer votre catalogue de produits et suivre vos commandes en temps réel.
          </p>
          
          {!isAuthenticated ? (
            <div className="mt-8 flex flex-col items-center gap-4 sm:flex-row sm:justify-center">
              <Button size="lg" onClick={login}>
                <LogIn className="mr-2 h-5 w-5" />
                Se connecter avec Keycloak
              </Button>
              <Button variant="outline" size="lg" asChild>
                <Link to="/products">
                  Voir le catalogue
                  <ArrowRight className="ml-2 h-5 w-5" />
                </Link>
              </Button>
            </div>
          ) : (
            <div className="mt-8">
              <p className="mb-4 text-lg">
                Bienvenue, <span className="font-semibold text-primary">{username}</span>
                {isAdmin && (
                  <span className="ml-2 inline-flex items-center gap-1 rounded-full bg-primary/10 px-3 py-1 text-sm font-medium text-primary">
                    <Shield className="h-4 w-4" />
                    Administrateur
                  </span>
                )}
              </p>
              <div className="flex flex-col items-center gap-4 sm:flex-row sm:justify-center">
                <Button size="lg" asChild>
                  <Link to="/products">
                    <Package className="mr-2 h-5 w-5" />
                    Voir les produits
                  </Link>
                </Button>
                <Button variant="outline" size="lg" asChild>
                  <Link to="/orders">
                    <ShoppingCart className="mr-2 h-5 w-5" />
                    Mes commandes
                  </Link>
                </Button>
              </div>
            </div>
          )}
        </div>
      </section>

      {/* Features Section */}
      <section className="w-full py-12">
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          <Card className="transition-all hover:shadow-lg">
            <CardHeader>
              <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                <Package className="h-6 w-6 text-primary" />
              </div>
              <CardTitle>Catalogue Produits</CardTitle>
              <CardDescription>
                Parcourez notre catalogue complet de produits avec recherche et filtres
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button variant="ghost" asChild className="w-full justify-start p-0 text-primary hover:text-primary">
                <Link to="/products" className="flex items-center">
                  Explorer le catalogue
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Link>
              </Button>
            </CardContent>
          </Card>

          <Card className="transition-all hover:shadow-lg">
            <CardHeader>
              <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                <ShoppingCart className="h-6 w-6 text-primary" />
              </div>
              <CardTitle>Gestion des Commandes</CardTitle>
              <CardDescription>
                Créez et suivez vos commandes en temps réel avec suivi des statuts
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Button variant="ghost" asChild className="w-full justify-start p-0 text-primary hover:text-primary">
                <Link to="/orders" className="flex items-center">
                  Voir les commandes
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Link>
              </Button>
            </CardContent>
          </Card>

          <Card className="transition-all hover:shadow-lg md:col-span-2 lg:col-span-1">
            <CardHeader>
              <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
                <Shield className="h-6 w-6 text-primary" />
              </div>
              <CardTitle>Sécurité Avancée</CardTitle>
              <CardDescription>
                Authentification OAuth2/OIDC avec Keycloak et gestion des rôles
              </CardDescription>
            </CardHeader>
            <CardContent>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li className="flex items-center gap-2">
                  <span className="h-1.5 w-1.5 rounded-full bg-primary" />
                  Authentification SSO
                </li>
                <li className="flex items-center gap-2">
                  <span className="h-1.5 w-1.5 rounded-full bg-primary" />
                  Tokens JWT sécurisés
                </li>
                <li className="flex items-center gap-2">
                  <span className="h-1.5 w-1.5 rounded-full bg-primary" />
                  Contrôle d'accès basé sur les rôles
                </li>
              </ul>
            </CardContent>
          </Card>
        </div>
      </section>
    </div>
  );
};

export default Index;
