import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const productAPI = {
  /**
   * Fetch all products
   */
  getAll: () => api.get('/products'),

  /**
   * Fetch a single product by ID
   */
  getById: (id) => api.get(`/products/${id}`),

  /**
   * Create a new product
   */
  create: (data) => api.post('/products', data),

  /**
   * Update a product
   */
  update: (id, data) => api.put(`/products/${id}`, data),

  /**
   * Delete a product
   */
  delete: (id) => api.delete(`/products/${id}`),
  checkout: (items) => api.post('/checkout', items, { headers: { 'X-User-Id': 'demo-user' } }),
  pay: (id, result) => api.post(`/orders/${id}/payment`, { result, idempotencyKey: `demo-${id}` }, { headers: { 'X-User-Id': 'demo-user' } }),
  orders: () => api.get('/orders', { headers: { 'X-User-Id': 'demo-user' } }),
  cancel: (id) => api.post(`/orders/${id}/cancel`, {}, { headers: { 'X-User-Id': 'demo-user' } }),
};

export default api;
