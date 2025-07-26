import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import axios from 'axios';

interface DeliveryPartner {
  id: string;
  email: string;
  name: string;
  phone: string;
  vehicleType: string;
  vehicleNumber: string;
  isAvailable: boolean;
  currentLocation?: {
    latitude: number;
    longitude: number;
  };
}

interface AuthContextType {
  deliveryPartner: DeliveryPartner | null;
  token: string | null;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  loading: boolean;
  error: string | null;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [deliveryPartner, setDeliveryPartner] = useState<DeliveryPartner | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('delivery_token'));
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
      delete axios.defaults.headers.common['Authorization'];
    }
  }, [token]);

  useEffect(() => {
    const checkAuth = async () => {
      if (token) {
        try {
          const response = await axios.get('http://localhost:8080/api/delivery-partners/profile');
          setDeliveryPartner(response.data);
        } catch (error) {
          localStorage.removeItem('delivery_token');
          setToken(null);
        }
      }
      setLoading(false);
    };

    checkAuth();
  }, [token]);

  const login = async (email: string, password: string) => {
    try {
      setError(null);
      const response = await axios.post('http://localhost:8080/api/delivery-partners/login', {
        email,
        password,
      });

      const { token: newToken, deliveryPartner: partnerData } = response.data;
      setToken(newToken);
      setDeliveryPartner(partnerData);
      localStorage.setItem('delivery_token', newToken);
    } catch (error: any) {
      setError(error.response?.data?.message || 'Login failed');
      throw error;
    }
  };

  const logout = () => {
    setDeliveryPartner(null);
    setToken(null);
    localStorage.removeItem('delivery_token');
    delete axios.defaults.headers.common['Authorization'];
  };

  const value = {
    deliveryPartner,
    token,
    login,
    logout,
    loading,
    error,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}; 