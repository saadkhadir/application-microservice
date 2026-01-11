import keycloak from '@/lib/keycloak';
import type { Product, Order, OrderLine } from '@/types';

const API_BASE_URL = 'http://localhost:8888';

class ApiError extends Error {
  status: number;
  
  constructor(message: string, status: number) {
    super(message);
    this.status = status;
    this.name = 'ApiError';
  }
}

const getAuthHeaders = async (): Promise<Headers> => {
  const headers = new Headers({
    'Content-Type': 'application/json',
  });

  if (keycloak.authenticated) {
    try {
      await keycloak.updateToken(30);
      headers.append('Authorization', `Bearer ${keycloak.token}`);
    } catch (error) {
      console.error('Failed to refresh token:', error);
      throw new ApiError('Session expirée. Veuillez vous reconnecter.', 401);
    }
  }

  return headers;
};

const handleResponse = async <T>(response: Response): Promise<T> => {
  if (!response.ok) {
    if (response.status === 401) {
      throw new ApiError('Non autorisé. Veuillez vous connecter.', 401);
    }
    if (response.status === 403) {
      throw new ApiError('Accès refusé. Vous n\'avez pas les permissions nécessaires.', 403);
    }
    const errorText = await response.text();
    throw new ApiError(errorText || `Erreur ${response.status}`, response.status);
  }

  const text = await response.text();
  if (!text) return {} as T;
  
  try {
    return JSON.parse(text);
  } catch {
    return text as unknown as T;
  }
};

// Product API
export const productApi = {
  getAll: async (): Promise<Product[]> => {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE_URL}/product-service/products`, { headers });
    return handleResponse<Product[]>(response);
  },

  getById: async (id: number): Promise<Product> => {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE_URL}/product-service/products/${id}`, { headers });
    return handleResponse<Product>(response);
  },

  create: async (product: Omit<Product, 'id'>): Promise<void> => {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE_URL}/product-service/products`, {
      method: 'POST',
      headers,
      body: JSON.stringify(product),
    });
    return handleResponse<void>(response);
  },

  update: async (id: number, product: Product): Promise<void> => {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE_URL}/product-service/products/${id}`, {
      method: 'PUT',
      headers,
      body: JSON.stringify(product),
    });
    return handleResponse<void>(response);
  },

  delete: async (id: number): Promise<void> => {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE_URL}/product-service/products/${id}`, {
      method: 'DELETE',
      headers,
    });
    return handleResponse<void>(response);
  },
};

// Order API
export const orderApi = {
  getAll: async (): Promise<Order[]> => {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE_URL}/order-service/orders`, { headers });
    return handleResponse<Order[]>(response);
  },

  getMyOrders: async (): Promise<Order[]> => {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE_URL}/order-service/orders/myOrders`, { headers });
    return handleResponse<Order[]>(response);
  },

  getById: async (id: number): Promise<Order> => {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE_URL}/order-service/orders/${id}`, { headers });
    return handleResponse<Order>(response);
  },

  create: async (order: Order): Promise<string> => {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE_URL}/order-service/orders`, {
      method: 'POST',
      headers,
      body: JSON.stringify(order),
    });
    return handleResponse<string>(response);
  },

  updateStatus: async (orderId: number, status: string): Promise<string> => {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE_URL}/order-service/orders/${orderId}/status?status=${status}`, {
      method: 'PATCH',
      headers,
    });
    return handleResponse<string>(response);
  },

  addOrderLine: async (orderId: number, orderLine: OrderLine): Promise<string> => {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE_URL}/order-service/orders/${orderId}/add-order-line`, {
      method: 'PATCH',
      headers,
      body: JSON.stringify(orderLine),
    });
    return handleResponse<string>(response);
  },

  delete: async (id: number): Promise<void> => {
    const headers = await getAuthHeaders();
    const response = await fetch(`${API_BASE_URL}/order-service/orders/${id}`, {
      method: 'DELETE',
      headers,
    });
    return handleResponse<void>(response);
  },
};

export { ApiError };
