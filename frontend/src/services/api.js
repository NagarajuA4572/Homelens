import axios from 'axios';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor to attach JWT token to all requests if present in localStorage
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('homelens_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor to handle 401 Unauthorized responses
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // If token is invalid or expired, clear it
      const currentToken = localStorage.getItem('homelens_token');
      if (currentToken && !error.config.url.includes('/api/auth/login')) {
        localStorage.removeItem('homelens_token');
        localStorage.removeItem('homelens_user');
      }
    }
    return Promise.reject(error);
  }
);
