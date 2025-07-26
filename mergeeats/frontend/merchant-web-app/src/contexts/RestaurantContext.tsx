import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { restaurantAPI } from '../services/api';

interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
  category: string;
  isVegetarian: boolean;
  isAvailable: boolean;
  imageUrl?: string;
}

interface Restaurant {
  id: string;
  name: string;
  description: string;
  cuisine: string;
  phone: string;
  email: string;
  address: {
    street: string;
    city: string;
    state: string;
    zipCode: string;
  };
  isOpen: boolean;
  openingHours: {
    monday: string;
    tuesday: string;
    wednesday: string;
    thursday: string;
    friday: string;
    saturday: string;
    sunday: string;
  };
  rating: number;
  totalOrders: number;
}

interface RestaurantContextType {
  restaurant: Restaurant | null;
  menuItems: MenuItem[];
  loading: boolean;
  error: string | null;
  fetchRestaurant: () => Promise<void>;
  updateRestaurant: (restaurantData: any) => Promise<void>;
  addMenuItem: (menuItem: any) => Promise<void>;
  updateMenuItem: (itemId: string, menuItem: any) => Promise<void>;
  deleteMenuItem: (itemId: string) => Promise<void>;
  updateMenuItemAvailability: (itemId: string, isAvailable: boolean) => Promise<void>;
  clearError: () => void;
}

const RestaurantContext = createContext<RestaurantContextType | undefined>(undefined);

export const useRestaurant = () => {
  const context = useContext(RestaurantContext);
  if (context === undefined) {
    throw new Error('useRestaurant must be used within a RestaurantProvider');
  }
  return context;
};

interface RestaurantProviderProps {
  children: ReactNode;
}

export const RestaurantProvider: React.FC<RestaurantProviderProps> = ({ children }) => {
  const [restaurant, setRestaurant] = useState<Restaurant | null>(null);
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchRestaurant = async () => {
    try {
      setLoading(true);
      setError(null);

      const [restaurantResponse, menuResponse] = await Promise.all([
        restaurantAPI.getMyRestaurant(),
        restaurantAPI.getMenu()
      ]);

      setRestaurant(restaurantResponse.data);
      setMenuItems(menuResponse.data);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch restaurant data';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const updateRestaurant = async (restaurantData: any) => {
    try {
      setLoading(true);
      setError(null);

      const response = await restaurantAPI.updateRestaurant(restaurantData);
      setRestaurant(response.data);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to update restaurant';
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const addMenuItem = async (menuItem: any) => {
    try {
      setLoading(true);
      setError(null);

      const response = await restaurantAPI.addMenuItem(menuItem);
      const newItem = response.data;
      
      setMenuItems(prevItems => [...prevItems, newItem]);
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to add menu item';
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const updateMenuItem = async (itemId: string, menuItem: any) => {
    try {
      setLoading(true);
      setError(null);

      const response = await restaurantAPI.updateMenuItem(itemId, menuItem);
      const updatedItem = response.data;
      
      setMenuItems(prevItems =>
        prevItems.map(item =>
          item.id === itemId ? updatedItem : item
        )
      );
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to update menu item';
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const deleteMenuItem = async (itemId: string) => {
    try {
      setLoading(true);
      setError(null);

      await restaurantAPI.deleteMenuItem(itemId);
      
      setMenuItems(prevItems =>
        prevItems.filter(item => item.id !== itemId)
      );
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to delete menu item';
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const updateMenuItemAvailability = async (itemId: string, isAvailable: boolean) => {
    try {
      setLoading(true);
      setError(null);

      await restaurantAPI.updateMenuItemAvailability(itemId, isAvailable);
      
      setMenuItems(prevItems =>
        prevItems.map(item =>
          item.id === itemId ? { ...item, isAvailable } : item
        )
      );
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to update item availability';
      setError(errorMessage);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  const clearError = () => {
    setError(null);
  };

  const value: RestaurantContextType = {
    restaurant,
    menuItems,
    loading,
    error,
    fetchRestaurant,
    updateRestaurant,
    addMenuItem,
    updateMenuItem,
    deleteMenuItem,
    updateMenuItemAvailability,
    clearError,
  };

  return (
    <RestaurantContext.Provider value={value}>
      {children}
    </RestaurantContext.Provider>
  );
}; 