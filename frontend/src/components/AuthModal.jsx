import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';

export default function AuthModal() {
  const { authModalOpen, authModalInitialTab, closeAuthModal, login, register } = useAuth();
  const [tab, setTab] = useState(authModalInitialTab || 'login');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // Login form state
  const [loginEmail, setLoginEmail] = useState('');
  const [loginPassword, setLoginPassword] = useState('');

  // Register form state
  const [regFullName, setRegFullName] = useState('');
  const [regEmail, setRegEmail] = useState('');
  const [regPassword, setRegPassword] = useState('');
  const [regPhone, setRegPhone] = useState('');
  const [regRole, setRegRole] = useState('ROLE_BUYER');

  const resetFormFields = () => {
    setLoginEmail('');
    setLoginPassword('');
    setRegFullName('');
    setRegEmail('');
    setRegPassword('');
    setRegPhone('');
    setRegRole('ROLE_BUYER');
    setError('');
  };

  // Every time modal opens or requested tab changes, wipe inputs completely
  useEffect(() => {
    if (authModalOpen) {
      setTab(authModalInitialTab || 'login');
      resetFormFields();
    }
  }, [authModalInitialTab, authModalOpen]);

  if (!authModalOpen) return null;

  const handleClose = () => {
    resetFormFields();
    closeAuthModal();
  };

  const handleSwitchTab = (newTab) => {
    setTab(newTab);
    resetFormFields();
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      await login(loginEmail, loginPassword);
      handleClose();
    } catch (err) {
      setError(
        err.response?.data?.message || 'Invalid email or password. Please check your credentials.'
      );
    } finally {
      setSubmitting(false);
    }
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      await register({
        fullName: regFullName,
        email: regEmail,
        password: regPassword,
        phone: regPhone,
        role: regRole,
      });
      handleClose();
    } catch (err) {
      setError(
        err.response?.data?.message || 'Registration failed. Please verify your details.'
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-backdrop" onClick={handleClose}>
      <div className="modal-card" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close-btn" onClick={handleClose} aria-label="Close modal">
          ×
        </button>

        <div className="modal-tabs">
          <button
            type="button"
            className={`modal-tab ${tab === 'login' ? 'active' : ''}`}
            onClick={() => handleSwitchTab('login')}
          >
            Sign In
          </button>
          <button
            type="button"
            className={`modal-tab ${tab === 'register' ? 'active' : ''}`}
            onClick={() => handleSwitchTab('register')}
          >
            Create Account
          </button>
        </div>

        {error && <div className="modal-error-banner">{error}</div>}

        {tab === 'login' ? (
          <form onSubmit={handleLoginSubmit} className="modal-form" autoComplete="off">
            <div className="form-group">
              <label htmlFor="login-email">Email Address</label>
              <input
                id="login-email"
                type="email"
                required
                placeholder="you@example.com"
                value={loginEmail}
                onChange={(e) => setLoginEmail(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label htmlFor="login-password">Password</label>
              <input
                id="login-password"
                type="password"
                required
                placeholder="••••••••"
                value={loginPassword}
                onChange={(e) => setLoginPassword(e.target.value)}
              />
            </div>

            <button type="submit" className="primary-button full-width" disabled={submitting}>
              {submitting ? 'Signing In…' : 'Sign In'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleRegisterSubmit} className="modal-form" autoComplete="off">
            <div className="form-group">
              <label>Account Type</label>
              <div className="role-selector">
                <label className={`role-option ${regRole === 'ROLE_BUYER' ? 'selected' : ''}`}>
                  <input
                    type="radio"
                    name="role"
                    value="ROLE_BUYER"
                    checked={regRole === 'ROLE_BUYER'}
                    onChange={() => setRegRole('ROLE_BUYER')}
                  />
                  <div>
                    <span className="role-title">🔍 Property Seeker</span>
                    <span className="role-desc">Looking to rent or browse properties</span>
                  </div>
                </label>

                <label className={`role-option ${regRole === 'ROLE_OWNER' ? 'selected' : ''}`}>
                  <input
                    type="radio"
                    name="role"
                    value="ROLE_OWNER"
                    checked={regRole === 'ROLE_OWNER'}
                    onChange={() => setRegRole('ROLE_OWNER')}
                  />
                  <div>
                    <span className="role-title">🏢 Property Owner</span>
                    <span className="role-desc">Register & manage rental listings</span>
                  </div>
                </label>
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="reg-fullname">Full Name</label>
              <input
                id="reg-fullname"
                type="text"
                required
                placeholder="e.g. Priya Sharma"
                value={regFullName}
                onChange={(e) => setRegFullName(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label htmlFor="reg-email">Email Address</label>
              <input
                id="reg-email"
                type="email"
                required
                placeholder="you@example.com"
                value={regEmail}
                onChange={(e) => setRegEmail(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label htmlFor="reg-phone">Phone Number (Optional)</label>
              <input
                id="reg-phone"
                type="tel"
                placeholder="+91 98765 43210"
                value={regPhone}
                onChange={(e) => setRegPhone(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label htmlFor="reg-password">Password (at least 6 characters)</label>
              <input
                id="reg-password"
                type="password"
                required
                minLength={6}
                placeholder="Create a strong password"
                value={regPassword}
                onChange={(e) => setRegPassword(e.target.value)}
              />
            </div>

            <button type="submit" className="primary-button full-width" disabled={submitting}>
              {submitting ? 'Creating Account…' : 'Create Account'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}
