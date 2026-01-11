export interface Product {
  id?: number;
  name: string;
  description: string;
  price: number;
  quantity: number;
}

export type OrderStatus = 'PENDING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface OrderLine {
  id?: number;
  productId: number;
  product?: Product;
  quantity: number;
  unitPrice: number;
  lineTotal?: number;
}

export interface Order {
  id?: number;
  date?: string;
  status?: OrderStatus;
  orderLines: OrderLine[];
  userId?: string;
  totalAmount?: number;
}

export type UserRole = 'admin' | 'client';
