import { Route, Routes, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import AuthModal from './components/AuthModal';
import HomePage from './pages/HomePage';
import PropertyListingsPage from './pages/PropertyListingsPage';
import PropertyDetailPage from './pages/PropertyDetailPage';
import OwnerDashboardPage from './pages/OwnerDashboardPage';
import SeekerInquiriesPage from './pages/SeekerInquiriesPage';
import AboutPage from './pages/AboutPage';

export default function App() {
  return (
    <AuthProvider>
      <div className="app-root">
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
  );
}
