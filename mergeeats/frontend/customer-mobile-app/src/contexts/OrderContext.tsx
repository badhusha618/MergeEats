import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { orderAPI } from '../services/api';

interface OrderItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
}

interface Order {
  id: string;
  customerId: string;
  restaurantId: string;
  restaurantName: string;
  items: OrderItem[];
  totalAmount: number;
  status: 'PENDING' | 'CONFIRMED' | 'PREPARING' | 'READY_FOR_PICKUP' | 'ON_THE_WAY' | 'DELIVERED' | 'CANCELLED';
  deliveryAddress: string;
  customerName: string;
  customerPhone: string;
  createdAt: string;
  updatedAt: string;
  estimatedDeliveryTime?: string;
  deliveryPartnerId?: string;
  deliveryPartnerName?: string;
}

interface OrderContextType {
  orders: Order[];
  activeOrder: Order | null;
  loading: boolean;
  error: string | null;
  createOrder: (orderData: any) => Promise<Order>;
  fetchOrders: () => Promise<void>;
  getOrderById: (id: string) => Promise<Order>;
  cancelOrder: (id: string) => Promise<void>;
  trackOrder: (id: string) => Promise<any>;
  clearError: () => void;
}

const OrderContext = createContext<OrderContextType | undefined>(undefined);

export const useOrder = () => {
  const context = useContext(OrderContext);
  if (context === undefined) {
    throw new Error('useOrder must be used within an OrderProvider');
  }
  return context;
};

interface OrderProviderProps {
  children: ReactNode;
}

export const OrderProvider: React.FC<OrderProviderProps> = ({ children }) => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [activeOrder, setActiveOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // WebSocket connection for real-time updates
  useEffect(() => {
    let ws: WebSocket | null = null;

    const connectWebSocket = () => {
      const token = localStorage.getItem('authToken');
      if (!token) return;

      ws = new WebSocket(`ws://localhost:8082/ws/orders?token=${token}`);

      ws.onopen = () => {
        console.log('WebSocket connected for order updates');
      };

      ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.type === 'ORDER_UPDATE') {
            updateOrderInList(data.order);
          }
        } catch (error) {
          console.error('Failed to parse WebSocket message:', error);
        }
      };

      ws.onerror = (error) => {
        console.error('WebSocket error:', error);
      };

      ws.onclose = () => {
        console.log('WebSocket disconnected');
        // Reconnect after 5 seconds
        setTimeout(connectWebSocket, 5000);
      };
    };

    connectWebSocket();

    return () => {
      if (ws) {
        ws.close();
      }
    };
  }, []);

  const updateOrderInList = (updatedOrder: Order) => {
    setOrders(prevOrders =>
      prevOrders.map(order =>
        order.id === updatedOrder.id ? updatedOrder : order
      )
    );

    if (activeOrder?.id === updatedOrder.id) {
      setActiveOrder(updatedOrder);
    }
  };

  const createOrder = async (orderData: any): Promise<Order> => {
    try {
      setLoading(true);
      setError(null);

      const response = await orderAPI.create(orderData);
      const newOrder = response.data;

      setOrders(prevOrders => [newOrder, ...prevOrders]);
      setActiveOrder(newOrder);

      return newOrder;
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to create order';
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await orderAPI.getAll();
      const fetchedOrders = response.data;

      setOrders(fetchedOrders);
      
      // Set active order if there's a pending/active order
      const active = fetchedOrders.find(order => 
        ['PENDING', 'CONFIRMED', 'PREPARING', 'READY_FOR_PICKUP', 'ON_THE_WAY'].includes(order.status)
      );
      setActiveOrder(active || null);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch orders';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const getOrderById = async (id: string): Promise<Order> => {
    try {
      setLoading(true);
      setError(null);

      const response = await orderAPI.getById(id);
      const order = response.data;

      // Update order in list
      updateOrderInList(order);

      return order;
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch order';
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const cancelOrder = async (id: string) => {
    try {
      setLoading(true);
      setError(null);

      await orderAPI.cancel(id);

      // Update order status in local state
      setOrders(prevOrders =>
        prevOrders.map(order =>
          order.id === id ? { ...order, status: 'CANCELLED' } : order
        )
      );

      if (activeOrder?.id === id) {
        setActiveOrder(prev => prev ? { ...prev, status: 'CANCELLED' } : null);
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to cancel order';
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const trackOrder = async (id: string) => {
    try {
      const response = await orderAPI.track(id);
      return response.data;
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to track order';
      setError(errorMessage);
      throw error;
    }
  };

  const clearError = () => {
    setError(null);
  };

  const value: OrderContextType = {
    orders,
    activeOrder,
    loading,
    error,
    createOrder,
    fetchOrders,
    getOrderById,
    cancelOrder,
    trackOrder,
    clearError,
  };

  return (
    <OrderContext.Provider value={value}>
      {children}
    </OrderContext.Provider>
  );
}; 