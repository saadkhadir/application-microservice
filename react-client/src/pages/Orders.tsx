import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { orderApi, ApiError } from '@/services/api';
import OrderCard from '@/components/OrderCard';
import ErrorAlert from '@/components/ErrorAlert';
import LoadingSpinner from '@/components/LoadingSpinner';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { ShoppingBag, Users } from 'lucide-react';
import type { Order } from '@/types';

const Orders: React.FC = () => {
  const { isAuthenticated, isAdmin, login } = useAuth();
  const [myOrders, setMyOrders] = useState<Order[]>([]);
  const [allOrders, setAllOrders] = useState<Order[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<{ message: string; status?: number } | null>(null);
  const [activeTab, setActiveTab] = useState('my-orders');

  const fetchOrders = async () => {
    if (!isAuthenticated) {
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError(null);
    
    try {
      // Fetch my orders
      const myOrdersData = await orderApi.getMyOrders();
      setMyOrders(myOrdersData);

      // If admin, also fetch all orders
      if (isAdmin) {
        const allOrdersData = await orderApi.getAll();
        setAllOrders(allOrdersData);
      }
    } catch (err) {
      if (err instanceof ApiError) {
        setError({ message: err.message, status: err.status });
      } else {
        setError({ message: 'Erreur lors du chargement des commandes' });
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [isAuthenticated, isAdmin]);

  if (!isAuthenticated) {
    return (
      <div className="py-12 text-center">
        <ShoppingBag className="mx-auto mb-4 h-16 w-16 text-muted-foreground/50" />
        <h2 className="mb-2 text-xl font-semibold">Connexion requise</h2>
        <p className="mb-4 text-muted-foreground">
          Veuillez vous connecter pour voir vos commandes
        </p>
        <button
          onClick={login}
          className="rounded-lg bg-primary px-4 py-2 font-medium text-primary-foreground hover:bg-primary/90"
        >
          Se connecter
        </button>
      </div>
    );
  }

  if (isLoading) {
    return <LoadingSpinner message="Chargement des commandes..." />;
  }

  if (error) {
    return <ErrorAlert message={error.message} status={error.status} onRetry={fetchOrders} />;
  }

  return (
    <div>
      <div className="mb-8">
        <h1 className="font-serif text-3xl font-bold text-foreground">Commandes</h1>
        <p className="mt-1 text-muted-foreground">
          Consultez et gérez vos commandes
        </p>
      </div>

      {isAdmin ? (
        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <TabsList className="mb-6">
            <TabsTrigger value="my-orders" className="flex items-center gap-2">
              <ShoppingBag className="h-4 w-4" />
              Mes commandes ({myOrders.length})
            </TabsTrigger>
            <TabsTrigger value="all-orders" className="flex items-center gap-2">
              <Users className="h-4 w-4" />
              Toutes les commandes ({allOrders.length})
            </TabsTrigger>
          </TabsList>

          <TabsContent value="my-orders">
            {myOrders.length === 0 ? (
              <div className="py-12 text-center">
                <ShoppingBag className="mx-auto mb-4 h-16 w-16 text-muted-foreground/50" />
                <p className="text-lg text-muted-foreground">
                  Vous n'avez pas encore de commandes
                </p>
              </div>
            ) : (
              <div className="grid gap-6 lg:grid-cols-2">
                {myOrders.map((order) => (
                  <OrderCard key={order.id} order={order} />
                ))}
              </div>
            )}
          </TabsContent>

          <TabsContent value="all-orders">
            {allOrders.length === 0 ? (
              <div className="py-12 text-center">
                <ShoppingBag className="mx-auto mb-4 h-16 w-16 text-muted-foreground/50" />
                <p className="text-lg text-muted-foreground">
                  Aucune commande dans le système
                </p>
              </div>
            ) : (
              <div className="grid gap-6 lg:grid-cols-2">
                {allOrders.map((order) => (
                  <OrderCard key={order.id} order={order} showUserId />
                ))}
              </div>
            )}
          </TabsContent>
        </Tabs>
      ) : (
        <>
          {myOrders.length === 0 ? (
            <div className="py-12 text-center">
              <ShoppingBag className="mx-auto mb-4 h-16 w-16 text-muted-foreground/50" />
              <p className="text-lg text-muted-foreground">
                Vous n'avez pas encore de commandes
              </p>
              <p className="mt-2 text-sm text-muted-foreground">
                Parcourez notre catalogue pour passer votre première commande
              </p>
            </div>
          ) : (
            <div className="grid gap-6 lg:grid-cols-2">
              {myOrders.map((order) => (
                <OrderCard key={order.id} order={order} />
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default Orders;
