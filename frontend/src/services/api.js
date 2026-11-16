import axios from 'axios';

const baseURL = (import.meta.env.VITE_API_URL || 'http://localhost:8080').replace(/\/+$/, '');

export const apiClient = axios.create({
  baseURL,
  // 75 seconds timeout to accommodate Render.com free tier cold starts (~45-60s)
  timeout: 75000,
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

// Response interceptor with automatic retry for cold starts and 401 handling
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const config = error.config;

    // Handle 401 Unauthorized responses
    if (error.response && error.response.status === 401) {
      const currentToken = localStorage.getItem('homelens_token');
      if (currentToken && !config?.url?.includes('/api/auth/login')) {
        localStorage.removeItem('homelens_token');
        localStorage.removeItem('homelens_user');
      }
      return Promise.reject(error);
    }

    // Auto-retry up to 2 times for Render.com cold starts (502, 503, 504, timeout, or network error)
    if (config && !config._isRetry) {
      const isNetworkOrColdStart =
        !error.response ||
        error.code === 'ECONNABORTED' ||
        [502, 503, 504].includes(error.response?.status);

      config._retryCount = config._retryCount || 0;
      if (isNetworkOrColdStart && config._retryCount < 2) {
        config._retryCount += 1;
        // Wait 2 seconds before retry to give backend time to spin up
        await new Promise((resolve) => setTimeout(resolve, 2000));
        return apiClient(config);
      }
    }

    return Promise.reject(error);
  }
);

/**
 * Robust image URL resolver supporting external cloud/Neon object storage and local uploads
 */
export function resolveImageUrl(rawUrl) {
  if (!rawUrl || typeof rawUrl !== 'string') return null;
  // Strip surrounding quotes and whitespace
  let clean = rawUrl.trim().replace(/^['"]+|['"]+$/g, '');
  if (!clean) return null;

  // Already a full HTTP or HTTPS or protocol-relative URL
  if (clean.startsWith('http://') || clean.startsWith('https://') || clean.startsWith('//')) {
    if (clean.startsWith('//')) {
      return `https:${clean}`;
    }
    // Upgrade http to https to avoid browser Mixed Content blockage on HTTPS deployed frontends
    if (clean.startsWith('http://') && typeof window !== 'undefined' && window.location.protocol === 'https:') {
      return clean.replace('http://', 'https://');
    }
    return clean;
  }

  // Relative path (e.g. /uploads/properties/... or uploads/properties/...)
  const path = clean.startsWith('/') ? clean : `/${clean}`;
  return `${baseURL}${path}`;
}

/**
 * Modern SVG placeholder fallback for properties without valid images or failed loads
 */
export const FALLBACK_PROPERTY_IMAGE =
  'data:image/svg+xml;charset=UTF-8,' +
  encodeURIComponent(
    `<svg xmlns="http://www.w3.org/2000/svg" width="800" height="500" viewBox="0 0 800 500">
      <defs>
        <linearGradient id="grad" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" style="stop-color:#0f172a;stop-opacity:1" />
          <stop offset="100%" style="stop-color:#1e293b;stop-opacity:1" />
        </linearGradient>
      </defs>
      <rect width="100%" height="100%" fill="url(#grad)" />
      <g fill="none" stroke="#38bdf8" stroke-width="6" stroke-linecap="round" stroke-linejoin="round" transform="translate(340, 160)">
        <path d="M10 70 L60 20 L110 70" />
        <rect x="25" y="70" width="70" height="70" />
        <rect x="45" y="100" width="30" height="40" />
      </g>
      <text x="50%" y="320" font-family="system-ui, sans-serif" font-size="22" font-weight="600" fill="#94a3b8" text-anchor="middle">
        HomeLens Property
      </text>
      <text x="50%" y="355" font-family="system-ui, sans-serif" font-size="14" fill="#64748b" text-anchor="middle">
        Photograph Preview
      </text>
    </svg>`
  );
