import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Package, Calendar, User } from 'lucide-react';
import type { Order, OrderStatus } from '@/types';

interface OrderCardProps {
  order: Order;
  showUserId?: boolean;
}

const statusConfig: Record<OrderStatus, { label: string; variant: 'default' | 'secondary' | 'destructive' | 'outline' }> = {
  PENDING: { label: 'En attente', variant: 'secondary' },
  SHIPPED: { label: 'Expédiée', variant: 'default' },
  DELIVERED: { label: 'Livrée', variant: 'outline' },
  CANCELLED: { label: 'Annulée', variant: 'destructive' },
};

const OrderCard: React.FC<OrderCardProps> = ({ order, showUserId = false }) => {
  const status = order.status || 'PENDING';
  const statusInfo = statusConfig[status];
  const formattedDate = order.date 
    ? new Date(order.date).toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: 'long',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      })
    : 'Date non disponible';

  return (
    <Card className="overflow-hidden transition-all hover:shadow-lg">
      <CardHeader className="border-b border-border bg-muted/30 pb-4">
        <div className="flex items-center justify-between">
          <CardTitle className="text-lg">
            Commande #{order.id}
          </CardTitle>
          <Badge variant={statusInfo.variant}>
            {statusInfo.label}
          </Badge>
        </div>
        <div className="mt-2 flex flex-wrap items-center gap-4 text-sm text-muted-foreground">
          <span className="flex items-center gap-1">
            <Calendar className="h-4 w-4" />
            {formattedDate}
          </span>
          {showUserId && order.userId && (
            <span className="flex items-center gap-1">
              <User className="h-4 w-4" />
              {order.userId}
            </span>
          )}
        </div>
      </CardHeader>
      
      <CardContent className="pt-4">
        <div className="space-y-3">
          <h4 className="font-medium">Articles ({order.orderLines?.length || 0})</h4>
          <div className="space-y-2">
            {order.orderLines?.map((line, index) => (
              <div
                key={line.id || index}
                className="flex items-center justify-between rounded-lg bg-muted/50 p-3"
              >
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10">
                    <Package className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <p className="font-medium">
                      {line.product?.name || `Produit #${line.productId}`}
                    </p>
                    <p className="text-sm text-muted-foreground">
                      Qté: {line.quantity} × {line.unitPrice?.toFixed(2) || '0.00'} €
                    </p>
                  </div>
                </div>
                <p className="font-semibold">
                  {(line.lineTotal || line.quantity * (line.unitPrice || 0)).toFixed(2)} €
                </p>
              </div>
            ))}
          </div>
          <div className="flex items-center justify-between border-t border-border pt-3">
            <span className="text-lg font-semibold">Total</span>
            <span className="text-xl font-bold text-primary">
              {order.totalAmount?.toFixed(2) || '0.00'} €
            </span>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default OrderCard;
