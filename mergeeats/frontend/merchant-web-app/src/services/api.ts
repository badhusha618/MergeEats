import axios from 'axios';

// API Gateway base URL
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials: { email: string; password: string }) =>
    api.post('/auth/merchant/login', credentials),
  
  register: (merchantData: { name: string; email: string; password: string; phone: string; restaurantName: string }) =>
    api.post('/auth/merchant/register', merchantData),
  
  logout: () => api.post('/auth/logout'),
  
  getProfile: () => api.get('/auth/merchant/profile'),
  
  updateProfile: (profileData: any) => api.put('/auth/merchant/profile', profileData),
};

// Restaurant API
export const restaurantAPI = {
  getMyRestaurant: () => api.get('/restaurants/my-restaurant'),
  
  updateRestaurant: (restaurantData: any) => api.put('/restaurants/my-restaurant', restaurantData),
  
  getMenu: () => api.get('/restaurants/my-restaurant/menu'),
  
  addMenuItem: (menuItem: any) => api.post('/restaurants/my-restaurant/menu', menuItem),
  
  updateMenuItem: (itemId: string, menuItem: any) => api.put(`/restaurants/my-restaurant/menu/${itemId}`, menuItem),
  
  deleteMenuItem: (itemId: string) => api.delete(`/restaurants/my-restaurant/menu/${itemId}`),
  
  updateMenuItemAvailability: (itemId: string, isAvailable: boolean) =>
    api.put(`/restaurants/my-restaurant/menu/${itemId}/availability`, { isAvailable }),
};

// Order API
export const orderAPI = {
  getMyOrders: (params?: { status?: string; page?: number; size?: number }) =>
    api.get('/orders/merchant', { params }),
  
  getOrderById: (id: string) => api.get(`/orders/merchant/${id}`),
  
  updateOrderStatus: (id: string, status: string) => api.put(`/orders/merchant/${id}/status`, { status }),
  
  getOrderStats: () => api.get('/orders/merchant/stats'),
  
  getRecentOrders: () => api.get('/orders/merchant/recent'),
};

// Analytics API
export const analyticsAPI = {
  getSalesData: (period: string) => api.get(`/analytics/sales?period=${period}`),
  
  getOrderStats: (period: string) => api.get(`/analytics/orders?period=${period}`),
  
  getPopularItems: () => api.get('/analytics/popular-items'),
  
  getCustomerStats: () => api.get('/analytics/customers'),
  
  getRevenueData: (period: string) => api.get(`/analytics/revenue?period=${period}`),
};

// Notification API
export const notificationAPI = {
  getNotifications: () => api.get('/notifications/merchant'),
  
  markAsRead: (id: string) => api.put(`/notifications/${id}/read`),
  
  markAllAsRead: () => api.put('/notifications/merchant/read-all'),
  
  deleteNotification: (id: string) => api.delete(`/notifications/${id}`),
};

export default api; 