import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import type { Product } from '@/types';

const productSchema = z.object({
  name: z.string().min(1, 'Le nom est requis'),
  description: z.string().min(1, 'La description est requise'),
  price: z.coerce.number().min(0.01, 'Le prix doit être supérieur à 0'),
  quantity: z.coerce.number().int().min(0, 'La quantité doit être positive'),
});

type ProductFormValues = z.infer<typeof productSchema>;

interface ProductFormProps {
  open: boolean;
  onClose: () => void;
  onSubmit: (product: Omit<Product, 'id'>) => Promise<void>;
  product?: Product | null;
  isLoading?: boolean;
}

const ProductForm: React.FC<ProductFormProps> = ({
  open,
  onClose,
  onSubmit,
  product,
  isLoading,
}) => {
  const form = useForm<ProductFormValues>({
    resolver: zodResolver(productSchema),
    defaultValues: {
      name: product?.name || '',
      description: product?.description || '',
      price: product?.price || 0,
      quantity: product?.quantity || 0,
    },
  });

  React.useEffect(() => {
    if (product) {
      form.reset({
        name: product.name,
        description: product.description,
        price: product.price,
        quantity: product.quantity,
      });
    } else {
      form.reset({
        name: '',
        description: '',
        price: 0,
        quantity: 0,
      });
    }
  }, [product, form]);

  const handleSubmit = async (values: ProductFormValues) => {
    await onSubmit({
      name: values.name,
      description: values.description,
      price: values.price,
      quantity: values.quantity,
    });
  };

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>
            {product ? 'Modifier le produit' : 'Ajouter un produit'}
          </DialogTitle>
          <DialogDescription>
            {product
              ? 'Modifiez les informations du produit ci-dessous.'
              : 'Remplissez les informations pour créer un nouveau produit.'}
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nom du produit</FormLabel>
                  <FormControl>
                    <Input placeholder="Entrez le nom du produit" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Description</FormLabel>
                  <FormControl>
                    <Textarea
                      placeholder="Entrez la description du produit"
                      className="resize-none"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="price"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Prix (€)</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        step="0.01"
                        min="0"
                        placeholder="0.00"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="quantity"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Quantité</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        min="0"
                        placeholder="0"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <DialogFooter>
              <Button type="button" variant="outline" onClick={onClose}>
                Annuler
              </Button>
              <Button type="submit" disabled={isLoading}>
                {isLoading ? 'Chargement...' : product ? 'Modifier' : 'Créer'}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
};

export default ProductForm;
