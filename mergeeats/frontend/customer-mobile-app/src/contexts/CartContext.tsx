import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { restaurantAPI } from '../services/api';

interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
  imageUrl?: string;
  isVegetarian: boolean;
  isAvailable: boolean;
}

interface CartItem {
  id: string;
  menuItem: MenuItem;
  quantity: number;
  restaurantId: string;
  restaurantName: string;
}

interface CartContextType {
  items: CartItem[];
  addItem: (menuItem: MenuItem, restaurantId: string, restaurantName: string) => void;
  removeItem: (itemId: string) => void;
  updateQuantity: (itemId: string, quantity: number) => void;
  clearCart: () => void;
  getTotal: () => number;
  getItemCount: () => number;
  getRestaurantId: () => string | null;
  canAddFromRestaurant: (restaurantId: string) => boolean;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const useCart = () => {
  const context = useContext(CartContext);
  if (context === undefined) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};

interface CartProviderProps {
  children: ReactNode;
}

export const CartProvider: React.FC<CartProviderProps> = ({ children }) => {
  const [items, setItems] = useState<CartItem[]>([]);

  // Load cart from localStorage on mount
  useEffect(() => {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      try {
        setItems(JSON.parse(savedCart));
      } catch (error) {
        console.error('Failed to load cart from localStorage:', error);
        localStorage.removeItem('cart');
      }
    }
  }, []);

  // Save cart to localStorage whenever items change
  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(items));
  }, [items]);

  const addItem = (menuItem: MenuItem, restaurantId: string, restaurantName: string) => {
    setItems(prevItems => {
      // Check if we can add from this restaurant
      const currentRestaurantId = getRestaurantId();
      if (currentRestaurantId && currentRestaurantId !== restaurantId) {
        // Clear cart if adding from different restaurant
        return [{
          id: `${menuItem.id}-${Date.now()}`,
          menuItem,
          quantity: 1,
          restaurantId,
          restaurantName,
        }];
      }

      // Check if item already exists
      const existingItem = prevItems.find(item => item.menuItem.id === menuItem.id);
      if (existingItem) {
        return prevItems.map(item =>
          item.id === existingItem.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      }

      // Add new item
      return [...prevItems, {
        id: `${menuItem.id}-${Date.now()}`,
        menuItem,
        quantity: 1,
        restaurantId,
        restaurantName,
      }];
    });
  };

  const removeItem = (itemId: string) => {
    setItems(prevItems => prevItems.filter(item => item.id !== itemId));
  };

  const updateQuantity = (itemId: string, quantity: number) => {
    if (quantity <= 0) {
      removeItem(itemId);
      return;
    }

    setItems(prevItems =>
      prevItems.map(item =>
        item.id === itemId ? { ...item, quantity } : item
      )
    );
  };

  const clearCart = () => {
    setItems([]);
  };

  const getTotal = () => {
    return items.reduce((total, item) => total + (item.menuItem.price * item.quantity), 0);
  };

  const getItemCount = () => {
    return items.reduce((count, item) => count + item.quantity, 0);
  };

  const getRestaurantId = () => {
    return items.length > 0 ? items[0].restaurantId : null;
  };

  const canAddFromRestaurant = (restaurantId: string) => {
    const currentRestaurantId = getRestaurantId();
    return !currentRestaurantId || currentRestaurantId === restaurantId;
  };

  const value: CartContextType = {
    items,
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
    getTotal,
    getItemCount,
    getRestaurantId,
    canAddFromRestaurant,
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
}; 