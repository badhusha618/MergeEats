import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import axios from 'axios';

interface Merchant {
  id: string;
  email: string;
  name: string;
  phone: string;
  restaurantId: string;
  restaurantName: string;
}

interface AuthContextType {
  merchant: Merchant | null;
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
  const [merchant, setMerchant] = useState<Merchant | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('merchant_token'));
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
          const response = await axios.get('http://localhost:8080/api/merchants/profile');
          setMerchant(response.data);
        } catch (error) {
          localStorage.removeItem('merchant_token');
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
      const response = await axios.post('http://localhost:8080/api/merchants/login', {
        email,
        password,
      });

      const { token: newToken, merchant: merchantData } = response.data;
      setToken(newToken);
      setMerchant(merchantData);
      localStorage.setItem('merchant_token', newToken);
    } catch (error: any) {
      setError(error.response?.data?.message || 'Login failed');
      throw error;
    }
  };

  const logout = () => {
    setMerchant(null);
    setToken(null);
    localStorage.removeItem('merchant_token');
    delete axios.defaults.headers.common['Authorization'];
  };

  const value = {
    merchant,
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