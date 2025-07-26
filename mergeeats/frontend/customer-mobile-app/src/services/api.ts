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
    api.post('/auth/login', credentials),
  
  register: (userData: { name: string; email: string; password: string; phone: string }) =>
    api.post('/auth/register', userData),
  
  logout: () => api.post('/auth/logout'),
  
  getProfile: () => api.get('/auth/profile'),
  
  updateProfile: (profileData: any) => api.put('/auth/profile', profileData),
};

// Restaurant API
export const restaurantAPI = {
  getAll: (params?: { search?: string; cuisine?: string; rating?: number }) =>
    api.get('/restaurants', { params }),
  
  getById: (id: string) => api.get(`/restaurants/${id}`),
  
  getMenu: (restaurantId: string) => api.get(`/restaurants/${restaurantId}/menu`),
  
  search: (query: string) => api.get('/restaurants/search', { params: { q: query } }),
};

// Order API
export const orderAPI = {
  create: (orderData: any) => api.post('/orders', orderData),
  
  getAll: () => api.get('/orders'),
  
  getById: (id: string) => api.get(`/orders/${id}`),
  
  updateStatus: (id: string, status: string) => api.put(`/orders/${id}/status`, { status }),
  
  cancel: (id: string) => api.post(`/orders/${id}/cancel`),
  
  track: (id: string) => api.get(`/orders/${id}/track`),
};

// Payment API
export const paymentAPI = {
  createPayment: (paymentData: any) => api.post('/payments', paymentData),
  
  getPaymentMethods: () => api.get('/payments/methods'),
  
  processPayment: (paymentId: string, paymentDetails: any) =>
    api.post(`/payments/${paymentId}/process`, paymentDetails),
};

// Address API
export const addressAPI = {
  getAll: () => api.get('/addresses'),
  
  create: (addressData: any) => api.post('/addresses', addressData),
  
  update: (id: string, addressData: any) => api.put(`/addresses/${id}`, addressData),
  
  delete: (id: string) => api.delete(`/addresses/${id}`),
  
  setDefault: (id: string) => api.post(`/addresses/${id}/default`),
};

// Notification API
export const notificationAPI = {
  getAll: () => api.get('/notifications'),
  
  markAsRead: (id: string) => api.put(`/notifications/${id}/read`),
  
  markAllAsRead: () => api.put('/notifications/read-all'),
  
  delete: (id: string) => api.delete(`/notifications/${id}`),
};

export default api; 