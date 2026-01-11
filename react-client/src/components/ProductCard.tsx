import React from 'react';
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Edit, Trash2, ShoppingCart, Package } from 'lucide-react';
import type { Product } from '@/types';

interface ProductCardProps {
  product: Product;
  isAdmin: boolean;
  onEdit?: (product: Product) => void;
  onDelete?: (id: number) => void;
  onAddToCart?: (product: Product) => void;
}

const ProductCard: React.FC<ProductCardProps> = ({
  product,
  isAdmin,
  onEdit,
  onDelete,
  onAddToCart,
}) => {
  const isOutOfStock = product.quantity <= 0;

  return (
    <Card className="group flex h-full flex-col overflow-hidden transition-all hover:shadow-lg">
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between gap-2">
          <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
            <Package className="h-6 w-6 text-primary" />
          </div>
          <Badge variant={isOutOfStock ? 'destructive' : 'secondary'}>
            {isOutOfStock ? 'Rupture' : `${product.quantity} en stock`}
          </Badge>
        </div>
        <CardTitle className="mt-3 line-clamp-1 text-lg">{product.name}</CardTitle>
      </CardHeader>
      
      <CardContent className="flex-1">
        <p className="line-clamp-2 text-sm text-muted-foreground">
          {product.description || 'Aucune description disponible'}
        </p>
        <p className="mt-4 text-2xl font-bold text-primary">
          {product.price.toFixed(2)} €
        </p>
      </CardContent>

      <CardFooter className="flex gap-2 border-t border-border bg-muted/30 pt-4">
        {isAdmin ? (
          <>
            <Button
              variant="outline"
              size="sm"
              className="flex-1"
              onClick={() => onEdit?.(product)}
            >
              <Edit className="mr-2 h-4 w-4" />
              Modifier
            </Button>
            <Button
              variant="destructive"
              size="sm"
              onClick={() => product.id && onDelete?.(product.id)}
            >
              <Trash2 className="h-4 w-4" />
            </Button>
          </>
        ) : (
          <Button
            className="w-full"
            disabled={isOutOfStock}
            onClick={() => onAddToCart?.(product)}
          >
            <ShoppingCart className="mr-2 h-4 w-4" />
            Ajouter au panier
          </Button>
        )}
      </CardFooter>
    </Card>
  );
};

export default ProductCard;
