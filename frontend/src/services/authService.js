import { apiClient } from './api';

export const authService = {
  async register(data) {
    const response = await apiClient.post('/api/auth/register', data);
    return response.data;
  },

  async login(email, password) {
    const response = await apiClient.post('/api/auth/login', { email, password });
    return response.data;
  },

  async getCurrentUser() {
    const response = await apiClient.get('/api/auth/me');
    return response.data;
  },
};
