import React, { useState, useEffect } from 'react';
import { useAuth } from '@/contexts/AuthContext';
import { productApi, ApiError } from '@/services/api';
import ProductCard from '@/components/ProductCard';
import ProductForm from '@/components/ProductForm';
import Cart, { CartItem } from '@/components/Cart';
import ErrorAlert from '@/components/ErrorAlert';
import LoadingSpinner from '@/components/LoadingSpinner';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { Plus, Search, ShoppingCart } from 'lucide-react';
import { useToast } from '@/hooks/use-toast';
import { orderApi } from '@/services/api';
import type { Product, Order, OrderLine } from '@/types';

const Products: React.FC = () => {
  const { isAuthenticated, isAdmin, login } = useAuth();
  const { toast } = useToast();
  const [products, setProducts] = useState<Product[]>([]);
  const [filteredProducts, setFilteredProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<{ message: string; status?: number } | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  
  // Form state
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Delete state
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [productToDelete, setProductToDelete] = useState<number | null>(null);

  // Cart state
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [isCheckingOut, setIsCheckingOut] = useState(false);

  const fetchProducts = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await productApi.getAll();
      setProducts(data);
      setFilteredProducts(data);
    } catch (err) {
      if (err instanceof ApiError) {
        setError({ message: err.message, status: err.status });
      } else {
        setError({ message: 'Erreur lors du chargement des produits' });
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  useEffect(() => {
    const filtered = products.filter(
      (product) =>
        product.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        product.description?.toLowerCase().includes(searchQuery.toLowerCase())
    );
    setFilteredProducts(filtered);
  }, [searchQuery, products]);

  const handleAddProduct = () => {
    setEditingProduct(null);
    setIsFormOpen(true);
  };

  const handleEditProduct = (product: Product) => {
    setEditingProduct(product);
    setIsFormOpen(true);
  };

  const handleDeleteClick = (id: number) => {
    setProductToDelete(id);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (productToDelete === null) return;
    
    try {
      await productApi.delete(productToDelete);
      toast({ title: 'Succès', description: 'Produit supprimé avec succès' });
      fetchProducts();
    } catch (err) {
      if (err instanceof ApiError) {
        toast({ title: 'Erreur', description: err.message, variant: 'destructive' });
      }
    } finally {
      setDeleteDialogOpen(false);
      setProductToDelete(null);
    }
  };

  const handleFormSubmit = async (productData: Omit<Product, 'id'>) => {
    setIsSubmitting(true);
    try {
      if (editingProduct?.id) {
        await productApi.update(editingProduct.id, { ...productData, id: editingProduct.id });
        toast({ title: 'Succès', description: 'Produit modifié avec succès' });
      } else {
        await productApi.create(productData);
        toast({ title: 'Succès', description: 'Produit créé avec succès' });
      }
      setIsFormOpen(false);
      fetchProducts();
    } catch (err) {
      if (err instanceof ApiError) {
        toast({ title: 'Erreur', description: err.message, variant: 'destructive' });
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  // Cart functions
  const handleAddToCart = (product: Product) => {
    if (!isAuthenticated) {
      toast({
        title: 'Connexion requise',
        description: 'Veuillez vous connecter pour ajouter des produits au panier',
        variant: 'destructive',
      });
      login();
      return;
    }

    setCartItems((prev) => {
      const existing = prev.find((item) => item.product.id === product.id);
      if (existing) {
        return prev.map((item) =>
          item.product.id === product.id
            ? { ...item, quantity: Math.min(item.quantity + 1, product.quantity) }
            : item
        );
      }
      return [...prev, { product, quantity: 1 }];
    });
    toast({ title: 'Ajouté au panier', description: product.name });
  };

  const handleUpdateCartQuantity = (productId: number, quantity: number) => {
    if (quantity < 1) return;
    setCartItems((prev) =>
      prev.map((item) =>
        item.product.id === productId ? { ...item, quantity } : item
      )
    );
  };

  const handleRemoveFromCart = (productId: number) => {
    setCartItems((prev) => prev.filter((item) => item.product.id !== productId));
  };

  const handleCheckout = async () => {
    setIsCheckingOut(true);
    try {
      const orderLines: OrderLine[] = cartItems.map((item) => ({
        productId: item.product.id!,
        quantity: item.quantity,
        unitPrice: item.product.price,
      }));

      const order: Order = {
        orderLines,
      };

      await orderApi.create(order);
      toast({ title: 'Succès', description: 'Commande créée avec succès!' });
      setCartItems([]);
      setIsCartOpen(false);
      fetchProducts();
    } catch (err) {
      if (err instanceof ApiError) {
        toast({ title: 'Erreur', description: err.message, variant: 'destructive' });
      }
    } finally {
      setIsCheckingOut(false);
    }
  };

  const cartItemCount = cartItems.reduce((sum, item) => sum + item.quantity, 0);

  if (isLoading) {
    return <LoadingSpinner message="Chargement des produits..." />;
  }

  if (error) {
    return <ErrorAlert message={error.message} status={error.status} onRetry={fetchProducts} />;
  }

  return (
    <div>
      {/* Header */}
      <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="font-serif text-3xl font-bold text-foreground">Catalogue Produits</h1>
          <p className="mt-1 text-muted-foreground">
            {filteredProducts.length} produit(s) disponible(s)
          </p>
        </div>
        <div className="flex items-center gap-3">
          {!isAdmin && isAuthenticated && (
            <Button
              variant="outline"
              className="relative"
              onClick={() => setIsCartOpen(true)}
            >
              <ShoppingCart className="mr-2 h-4 w-4" />
              Panier
              {cartItemCount > 0 && (
                <span className="absolute -right-2 -top-2 flex h-5 w-5 items-center justify-center rounded-full bg-primary text-xs font-medium text-primary-foreground">
                  {cartItemCount}
                </span>
              )}
            </Button>
          )}
          {isAdmin && (
            <Button onClick={handleAddProduct}>
              <Plus className="mr-2 h-4 w-4" />
              Nouveau produit
            </Button>
          )}
        </div>
      </div>

      {/* Search */}
      <div className="mb-6">
        <div className="relative max-w-md">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Rechercher un produit..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="pl-10"
          />
        </div>
      </div>

      {/* Products Grid */}
      {filteredProducts.length === 0 ? (
        <div className="py-12 text-center">
          <p className="text-lg text-muted-foreground">Aucun produit trouvé</p>
        </div>
      ) : (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {filteredProducts.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              isAdmin={isAdmin}
              onEdit={handleEditProduct}
              onDelete={handleDeleteClick}
              onAddToCart={handleAddToCart}
            />
          ))}
        </div>
      )}

      {/* Product Form Dialog */}
      <ProductForm
        open={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSubmit={handleFormSubmit}
        product={editingProduct}
        isLoading={isSubmitting}
      />

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmer la suppression</AlertDialogTitle>
            <AlertDialogDescription>
              Êtes-vous sûr de vouloir supprimer ce produit ? Cette action est irréversible.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Annuler</AlertDialogCancel>
            <AlertDialogAction onClick={handleDeleteConfirm}>
              Supprimer
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {/* Cart */}
      <Cart
        open={isCartOpen}
        onClose={() => setIsCartOpen(false)}
        items={cartItems}
        onUpdateQuantity={handleUpdateCartQuantity}
        onRemove={handleRemoveFromCart}
        onCheckout={handleCheckout}
        isCheckingOut={isCheckingOut}
      />
    </div>
  );
};

export default Products;
