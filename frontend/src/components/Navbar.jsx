import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, isAuthenticated, isOwner, logout, openAuthModal } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="topbar">
      <div className="topbar-content">
        <Link to="/" className="brand">
          <span className="brand-icon">🏠</span>
          <span className="brand-text">HomeLens</span>
        </Link>

        <nav className="nav-links">
          <Link to="/">Home</Link>
          <Link to="/properties">Explore Rentals</Link>
          <Link to="/about">About</Link>
        </nav>

        <div className="nav-actions">
          {isAuthenticated ? (
            <div className="user-nav-group">
              {isOwner && (
                <Link to="/owner/properties" className="owner-dash-btn">
                  🏢 Owner Dashboard
                </Link>
              )}
              <div className="user-profile-badge">
                <span className="avatar-circle">
                  {user?.fullName ? user.fullName.charAt(0).toUpperCase() : 'U'}
                </span>
                <span className="user-name">{user?.fullName?.split(' ')[0]}</span>
                <span className="role-tag">
                  {user?.role === 'ROLE_OWNER'
                    ? 'Owner'
                    : user?.role === 'ROLE_AGENT'
                    ? 'Agent'
                    : user?.role === 'ROLE_ADMIN'
                    ? 'Admin'
                    : 'Seeker'}
                </span>
              </div>
              <button onClick={handleLogout} className="sign-out-btn" title="Sign out">
                Sign Out
              </button>
            </div>
          ) : (
            <div className="auth-buttons">
              <button onClick={() => openAuthModal('login')} className="sign-in-btn">
                Sign In
              </button>
              <button onClick={() => openAuthModal('register')} className="register-btn">
                Register
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
