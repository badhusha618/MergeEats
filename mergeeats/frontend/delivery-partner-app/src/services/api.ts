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
    api.post('/auth/delivery/login', credentials),
  
  register: (partnerData: { name: string; email: string; password: string; phone: string; vehicleType: string; vehicleNumber: string }) =>
    api.post('/auth/delivery/register', partnerData),
  
  logout: () => api.post('/auth/logout'),
  
  getProfile: () => api.get('/auth/delivery/profile'),
  
  updateProfile: (profileData: any) => api.put('/auth/delivery/profile', profileData),
};

// Delivery API
export const deliveryAPI = {
  getActiveDeliveries: () => api.get('/deliveries/active'),
  
  getCompletedDeliveries: (params?: { page?: number; size?: number }) =>
    api.get('/deliveries/completed', { params }),
  
  getDeliveryById: (id: string) => api.get(`/deliveries/${id}`),
  
  updateDeliveryStatus: (id: string, status: string) => api.put(`/deliveries/${id}/status`, { status }),
  
  acceptDelivery: (id: string) => api.post(`/deliveries/${id}/accept`),
  
  rejectDelivery: (id: string, reason?: string) => api.post(`/deliveries/${id}/reject`, { reason }),
  
  updateLocation: (latitude: number, longitude: number) => 
    api.post('/deliveries/location', { latitude, longitude }),
  
  getDeliveryRequests: () => api.get('/deliveries/requests'),
  
  getEarnings: (period: string) => api.get(`/deliveries/earnings?period=${period}`),
  
  getEarningsStats: () => api.get('/deliveries/earnings/stats'),
};

// Order API
export const orderAPI = {
  getOrderDetails: (orderId: string) => api.get(`/orders/${orderId}`),
  
  updateOrderStatus: (orderId: string, status: string) => 
    api.put(`/orders/${orderId}/status`, { status }),
};

// Notification API
export const notificationAPI = {
  getNotifications: () => api.get('/notifications/delivery'),
  
  markAsRead: (id: string) => api.put(`/notifications/${id}/read`),
  
  markAllAsRead: () => api.put('/notifications/delivery/read-all'),
  
  deleteNotification: (id: string) => api.delete(`/notifications/${id}`),
};

export default api; 