import { createContext, useContext, useEffect, useState } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('homelens_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('homelens_token'));
  const [loading, setLoading] = useState(true);
  const [authModalOpen, setAuthModalOpen] = useState(false);
  const [authModalInitialTab, setAuthModalInitialTab] = useState('login');

  useEffect(() => {
    const verifyUser = async () => {
      if (token) {
        try {
          const profile = await authService.getCurrentUser();
          setUser((prev) => ({ ...prev, ...profile }));
          localStorage.setItem('homelens_user', JSON.stringify({ ...user, ...profile }));
        } catch (err) {
          logout();
        }
      }
      setLoading(false);
    };

    verifyUser();
  }, [token]);

  const login = async (email, password) => {
    const authData = await authService.login(email, password);
    setToken(authData.token);
    setUser({
      id: authData.id,
      email: authData.email,
      fullName: authData.fullName,
      role: authData.role,
    });
    localStorage.setItem('homelens_token', authData.token);
    localStorage.setItem(
      'homelens_user',
      JSON.stringify({
        id: authData.id,
        email: authData.email,
        fullName: authData.fullName,
        role: authData.role,
      })
    );
    return authData;
  };

  const register = async (formData) => {
    const authData = await authService.register(formData);
    setToken(authData.token);
    setUser({
      id: authData.id,
      email: authData.email,
      fullName: authData.fullName,
      role: authData.role,
    });
    localStorage.setItem('homelens_token', authData.token);
    localStorage.setItem(
      'homelens_user',
      JSON.stringify({
        id: authData.id,
        email: authData.email,
        fullName: authData.fullName,
        role: authData.role,
      })
    );
    return authData;
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('homelens_token');
    localStorage.removeItem('homelens_user');
  };

  const openAuthModal = (tab = 'login') => {
    setAuthModalInitialTab(tab);
    setAuthModalOpen(true);
  };

  const closeAuthModal = () => {
    setAuthModalOpen(false);
  };

  const isOwner = user?.role === 'ROLE_OWNER' || user?.role === 'ROLE_AGENT' || user?.role === 'ROLE_ADMIN';
  const isAdmin = user?.role === 'ROLE_ADMIN';

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token,
        isOwner,
        isAdmin,
        loading,
        login,
        register,
        logout,
        authModalOpen,
        authModalInitialTab,
        openAuthModal,
        closeAuthModal,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
