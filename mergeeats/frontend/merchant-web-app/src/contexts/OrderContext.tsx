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
  customerName: string;
  customerPhone: string;
  items: OrderItem[];
  totalAmount: number;
  status: 'PENDING' | 'CONFIRMED' | 'PREPARING' | 'READY_FOR_PICKUP' | 'ON_THE_WAY' | 'DELIVERED' | 'CANCELLED';
  deliveryAddress: string;
  specialInstructions?: string;
  createdAt: string;
  updatedAt: string;
  estimatedDeliveryTime?: string;
  deliveryPartnerId?: string;
  deliveryPartnerName?: string;
}

interface OrderStats {
  totalOrders: number;
  pendingOrders: number;
  completedOrders: number;
  cancelledOrders: number;
  totalRevenue: number;
  averageOrderValue: number;
}

interface OrderContextType {
  orders: Order[];
  pendingOrders: Order[];
  completedOrders: Order[];
  orderStats: OrderStats | null;
  loading: boolean;
  error: string | null;
  fetchOrders: () => Promise<void>;
  getOrderById: (id: string) => Promise<Order>;
  updateOrderStatus: (id: string, status: string) => Promise<void>;
  fetchOrderStats: () => Promise<void>;
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
  const [orderStats, setOrderStats] = useState<OrderStats | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // WebSocket connection for real-time order updates
  useEffect(() => {
    let ws: WebSocket | null = null;

    const connectWebSocket = () => {
      const token = localStorage.getItem('authToken');
      if (!token) return;

      ws = new WebSocket(`ws://localhost:8082/ws/orders/merchant?token=${token}`);

      ws.onopen = () => {
        console.log('WebSocket connected for merchant order updates');
      };

      ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.type === 'ORDER_UPDATE') {
            updateOrderInList(data.order);
          } else if (data.type === 'NEW_ORDER') {
            addNewOrder(data.order);
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
  };

  const addNewOrder = (newOrder: Order) => {
    setOrders(prevOrders => [newOrder, ...prevOrders]);
  };

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await orderAPI.getMyOrders();
      const fetchedOrders = response.data;

      setOrders(fetchedOrders);
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

      const response = await orderAPI.getOrderById(id);
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

  const updateOrderStatus = async (id: string, status: string) => {
    try {
      setLoading(true);
      setError(null);

      await orderAPI.updateOrderStatus(id, status);

      // Update order status in local state
      setOrders(prevOrders =>
        prevOrders.map(order =>
          order.id === id ? { ...order, status: status as any } : order
        )
      );
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to update order status';
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const fetchOrderStats = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await orderAPI.getOrderStats();
      setOrderStats(response.data);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch order stats';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const clearError = () => {
    setError(null);
  };

  // Computed properties
  const pendingOrders = orders.filter(order => 
    ['PENDING', 'CONFIRMED', 'PREPARING', 'READY_FOR_PICKUP', 'ON_THE_WAY'].includes(order.status)
  );

  const completedOrders = orders.filter(order => 
    ['DELIVERED', 'CANCELLED'].includes(order.status)
  );

  const value: OrderContextType = {
    orders,
    pendingOrders,
    completedOrders,
    orderStats,
    loading,
    error,
    fetchOrders,
    getOrderById,
    updateOrderStatus,
    fetchOrderStats,
    clearError,
  };

  return (
    <OrderContext.Provider value={value}>
      {children}
    </OrderContext.Provider>
  );
}; 