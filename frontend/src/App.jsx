import React, { useEffect } from 'react';
import { Route, Routes, Navigate, useNavigate, useLocation } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import AuthModal from './components/AuthModal';
import HomePage from './pages/HomePage';
import PropertyListingsPage from './pages/PropertyListingsPage';
import PropertyDetailPage from './pages/PropertyDetailPage';
import OwnerDashboardPage from './pages/OwnerDashboardPage';
import SeekerInquiriesPage from './pages/SeekerInquiriesPage';
import AboutPage from './pages/AboutPage';

/**
 * Handles manual page refresh / browser reload:
 * In accordance with requirements, any manual browser refresh resets to Home page.
 */
function RefreshToHomeHandler() {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    try {
      const navEntries = performance.getEntriesByType('navigation');
      const isReload = navEntries.length > 0
        ? navEntries[0].type === 'reload'
        : (window.performance && window.performance.navigation && window.performance.navigation.type === 1);

      if (isReload && location.pathname !== '/') {
        navigate('/', { replace: true });
      }
    } catch (err) {
      // Fallback silently if performance API unavailable
    }
  }, []);

  return null;
}

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    console.error("HomeLens caught error:", error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div style={{ minHeight: '80vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center', padding: '2rem' }}>
          <h2 style={{ fontSize: '2rem', marginBottom: '1rem', color: '#38bdf8' }}>🏠 HomeLens</h2>
          <p style={{ color: '#94a3b8', marginBottom: '1.5rem', maxWidth: '480px' }}>
            We encountered a temporary viewing issue. Click below to return to the Home page.
          </p>
          <a
            href="/"
            style={{
              display: 'inline-flex',
              padding: '0.75rem 1.5rem',
              background: '#38bdf8',
              color: '#07141d',
              borderRadius: '10px',
              textDecoration: 'none',
              fontWeight: 700,
            }}
          >
            Return to Home Page
          </a>
        </div>
      );
    }
    return this.props.children;
  }
}

export default function App() {
  return (
    <ErrorBoundary>
      <AuthProvider>
        <div className="app-root">
          <RefreshToHomeHandler />
          <Navbar />
          <AuthModal />
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/properties" element={<PropertyListingsPage />} />
            <Route path="/properties/:id" element={<PropertyDetailPage />} />
            <Route path="/owner/properties" element={<OwnerDashboardPage />} />
            <Route path="/seeker/inquiries" element={<SeekerInquiriesPage />} />
            <Route path="/about" element={<AboutPage />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </div>
      </AuthProvider>
    </ErrorBoundary>
  );
}
