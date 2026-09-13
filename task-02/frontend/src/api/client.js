import axios from 'axios';

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8082/api',
  headers: { 'Content-Type': 'application/json' },
});

export const healthAPI = {
  check: () => client.get('/health'),
};

const userHeaders = () => ({ headers: { 'X-User-Id': 'demo-user' } });
export const productAPI = { list: (params) => client.get('/products', { params }), get: (id) => client.get(`/products/${id}`) };
export const commerceAPI = {
  cart: () => client.get('/cart', userHeaders()),
  add: (productId, quantity = 1) => client.post('/cart/items', { productId, quantity }, userHeaders()),
  remove: (productId) => client.delete(`/cart/items/${productId}`, userHeaders()),
  checkout: () => client.post('/checkout', {}, userHeaders()),
  pay: (id, result) => client.post(`/orders/${id}/payment`, { result, idempotencyKey: `demo-${id}` }),
  orders: () => client.get('/orders', userHeaders()),
  order: (id) => client.get(`/orders/${id}`),
  cancel: (id) => client.post(`/orders/${id}/cancel`),
};

export default client;