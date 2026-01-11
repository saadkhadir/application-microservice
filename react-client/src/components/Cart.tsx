import React from 'react';
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from '@/components/ui/sheet';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Minus, Plus, Trash2, ShoppingBag } from 'lucide-react';
import type { Product } from '@/types';

export interface CartItem {
  product: Product;
  quantity: number;
}

interface CartProps {
  open: boolean;
  onClose: () => void;
  items: CartItem[];
  onUpdateQuantity: (productId: number, quantity: number) => void;
  onRemove: (productId: number) => void;
  onCheckout: () => void;
  isCheckingOut?: boolean;
}

const Cart: React.FC<CartProps> = ({
  open,
  onClose,
  items,
  onUpdateQuantity,
  onRemove,
  onCheckout,
  isCheckingOut,
}) => {
  const total = items.reduce(
    (sum, item) => sum + item.product.price * item.quantity,
    0
  );

  return (
    <Sheet open={open} onOpenChange={onClose}>
      <SheetContent className="flex w-full flex-col sm:max-w-lg">
        <SheetHeader>
          <SheetTitle className="flex items-center gap-2">
            <ShoppingBag className="h-5 w-5" />
            Panier
          </SheetTitle>
          <SheetDescription>
            {items.length === 0
              ? 'Votre panier est vide'
              : `${items.length} article(s) dans votre panier`}
          </SheetDescription>
        </SheetHeader>

        {items.length > 0 ? (
          <>
            <ScrollArea className="flex-1 py-4">
              <div className="space-y-4">
                {items.map((item) => (
                  <div
                    key={item.product.id}
                    className="flex items-center gap-4 rounded-lg border border-border bg-card p-4"
                  >
                    <div className="flex-1">
                      <h4 className="font-medium">{item.product.name}</h4>
                      <p className="text-sm text-muted-foreground">
                        {item.product.price.toFixed(2)} € / unité
                      </p>
                    </div>

                    <div className="flex items-center gap-2">
                      <Button
                        variant="outline"
                        size="icon"
                        className="h-8 w-8"
                        onClick={() =>
                          onUpdateQuantity(item.product.id!, item.quantity - 1)
                        }
                        disabled={item.quantity <= 1}
                      >
                        <Minus className="h-4 w-4" />
                      </Button>
                      <span className="w-8 text-center font-medium">
                        {item.quantity}
                      </span>
                      <Button
                        variant="outline"
                        size="icon"
                        className="h-8 w-8"
                        onClick={() =>
                          onUpdateQuantity(item.product.id!, item.quantity + 1)
                        }
                        disabled={item.quantity >= item.product.quantity}
                      >
                        <Plus className="h-4 w-4" />
                      </Button>
                    </div>

                    <div className="text-right">
                      <p className="font-semibold">
                        {(item.product.price * item.quantity).toFixed(2)} €
                      </p>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="mt-1 h-auto p-0 text-destructive hover:text-destructive"
                        onClick={() => onRemove(item.product.id!)}
                      >
                        <Trash2 className="mr-1 h-3 w-3" />
                        Retirer
                      </Button>
                    </div>
                  </div>
                ))}
              </div>
            </ScrollArea>

            <SheetFooter className="flex-col gap-4 border-t border-border pt-4">
              <div className="flex w-full items-center justify-between">
                <span className="text-lg font-semibold">Total</span>
                <span className="text-2xl font-bold text-primary">
                  {total.toFixed(2)} €
                </span>
              </div>
              <Button
                className="w-full"
                size="lg"
                onClick={onCheckout}
                disabled={isCheckingOut}
              >
                {isCheckingOut ? 'Traitement...' : 'Passer la commande'}
              </Button>
            </SheetFooter>
          </>
        ) : (
          <div className="flex flex-1 flex-col items-center justify-center text-center">
            <ShoppingBag className="mb-4 h-16 w-16 text-muted-foreground/50" />
            <p className="text-muted-foreground">
              Ajoutez des produits pour commencer
            </p>
          </div>
        )}
      </SheetContent>
    </Sheet>
  );
};

export default Cart;
