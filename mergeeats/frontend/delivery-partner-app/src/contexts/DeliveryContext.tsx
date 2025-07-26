import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { deliveryAPI } from '../services/api';

interface DeliveryItem {
  id: string;
  name: string;
  quantity: number;
}

interface Delivery {
  id: string;
  orderId: string;
  customerName: string;
  customerPhone: string;
  restaurantName: string;
  restaurantAddress: string;
  deliveryAddress: string;
  orderItems: DeliveryItem[];
  totalAmount: number;
  status: 'PENDING' | 'ASSIGNED' | 'PICKED_UP' | 'ON_THE_WAY' | 'DELIVERED' | 'CANCELLED';
  assignedAt: string;
  updatedAt: string;
  estimatedDeliveryTime?: string;
  actualDeliveryTime?: string;
  earnings: number;
}

interface DeliveryStats {
  totalDeliveries: number;
  completedDeliveries: number;
  totalEarnings: number;
  averageRating: number;
  onTimeDeliveries: number;
}

interface DeliveryContextType {
  activeDeliveries: Delivery[];
  completedDeliveries: Delivery[];
  deliveryRequests: Delivery[];
  deliveryStats: DeliveryStats | null;
  loading: boolean;
  error: string | null;
  fetchActiveDeliveries: () => Promise<void>;
  fetchCompletedDeliveries: () => Promise<void>;
  fetchDeliveryRequests: () => Promise<void>;
  acceptDelivery: (deliveryId: string) => Promise<void>;
  rejectDelivery: (deliveryId: string, reason?: string) => Promise<void>;
  updateDeliveryStatus: (deliveryId: string, status: string) => Promise<void>;
  updateLocation: (latitude: number, longitude: number) => Promise<void>;
  fetchDeliveryStats: () => Promise<void>;
  clearError: () => void;
}

const DeliveryContext = createContext<DeliveryContextType | undefined>(undefined);

export const useDelivery = () => {
  const context = useContext(DeliveryContext);
  if (context === undefined) {
    throw new Error('useDelivery must be used within a DeliveryProvider');
  }
  return context;
};

interface DeliveryProviderProps {
  children: ReactNode;
}

export const DeliveryProvider: React.FC<DeliveryProviderProps> = ({ children }) => {
  const [activeDeliveries, setActiveDeliveries] = useState<Delivery[]>([]);
  const [completedDeliveries, setCompletedDeliveries] = useState<Delivery[]>([]);
  const [deliveryRequests, setDeliveryRequests] = useState<Delivery[]>([]);
  const [deliveryStats, setDeliveryStats] = useState<DeliveryStats | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // WebSocket connection for real-time delivery updates
  useEffect(() => {
    let ws: WebSocket | null = null;

    const connectWebSocket = () => {
      const token = localStorage.getItem('authToken');
      if (!token) return;

      ws = new WebSocket(`ws://localhost:8085/ws/deliveries?token=${token}`);

      ws.onopen = () => {
        console.log('WebSocket connected for delivery updates');
      };

      ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.type === 'DELIVERY_UPDATE') {
            updateDeliveryInList(data.delivery);
          } else if (data.type === 'NEW_DELIVERY_REQUEST') {
            addNewDeliveryRequest(data.delivery);
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

  const updateDeliveryInList = (updatedDelivery: Delivery) => {
    setActiveDeliveries(prevDeliveries =>
      prevDeliveries.map(delivery =>
        delivery.id === updatedDelivery.id ? updatedDelivery : delivery
      )
    );

    setCompletedDeliveries(prevDeliveries =>
      prevDeliveries.map(delivery =>
        delivery.id === updatedDelivery.id ? updatedDelivery : delivery
      )
    );

    setDeliveryRequests(prevRequests =>
      prevRequests.filter(request => request.id !== updatedDelivery.id)
    );
  };

  const addNewDeliveryRequest = (newDelivery: Delivery) => {
    setDeliveryRequests(prevRequests => [newDelivery, ...prevRequests]);
  };

  const fetchActiveDeliveries = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await deliveryAPI.getActiveDeliveries();
      setActiveDeliveries(response.data);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch active deliveries';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const fetchCompletedDeliveries = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await deliveryAPI.getCompletedDeliveries();
      setCompletedDeliveries(response.data);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch completed deliveries';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const fetchDeliveryRequests = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await deliveryAPI.getDeliveryRequests();
      setDeliveryRequests(response.data);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch delivery requests';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const acceptDelivery = async (deliveryId: string) => {
    try {
      setLoading(true);
      setError(null);

      await deliveryAPI.acceptDelivery(deliveryId);

      // Move delivery from requests to active deliveries
      const acceptedDelivery = deliveryRequests.find(d => d.id === deliveryId);
      if (acceptedDelivery) {
        setDeliveryRequests(prev => prev.filter(d => d.id !== deliveryId));
        setActiveDeliveries(prev => [acceptedDelivery, ...prev]);
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to accept delivery';
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const rejectDelivery = async (deliveryId: string, reason?: string) => {
    try {
      setLoading(true);
      setError(null);

      await deliveryAPI.rejectDelivery(deliveryId, reason);

      // Remove delivery from requests
      setDeliveryRequests(prev => prev.filter(d => d.id !== deliveryId));
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to reject delivery';
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const updateDeliveryStatus = async (deliveryId: string, status: string) => {
    try {
      setLoading(true);
      setError(null);

      await deliveryAPI.updateDeliveryStatus(deliveryId, status);

      // Update delivery status in local state
      const updateDelivery = (deliveries: Delivery[]) =>
        deliveries.map(delivery =>
          delivery.id === deliveryId ? { ...delivery, status: status as any } : delivery
        );

      setActiveDeliveries(updateDelivery);
      setCompletedDeliveries(updateDelivery);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to update delivery status';
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const updateLocation = async (latitude: number, longitude: number) => {
    try {
      await deliveryAPI.updateLocation(latitude, longitude);
    } catch (error: any) {
      console.error('Failed to update location:', error);
    }
  };

  const fetchDeliveryStats = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await deliveryAPI.getEarningsStats();
      setDeliveryStats(response.data);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch delivery stats';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const clearError = () => {
    setError(null);
  };

  const value: DeliveryContextType = {
    activeDeliveries,
    completedDeliveries,
    deliveryRequests,
    deliveryStats,
    loading,
    error,
    fetchActiveDeliveries,
    fetchCompletedDeliveries,
    fetchDeliveryRequests,
    acceptDelivery,
    rejectDelivery,
    updateDeliveryStatus,
    updateLocation,
    fetchDeliveryStats,
    clearError,
  };

  return (
    <DeliveryContext.Provider value={value}>
      {children}
    </DeliveryContext.Provider>
  );
}; 