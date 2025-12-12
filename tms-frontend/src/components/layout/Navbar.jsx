import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import '../../styles/app.css';

function Navbar() {
  const { user, logout } = useAuth();
  const location = useLocation();

  const handleLogout = () => {
    logout();
  };

  if (!user || location.pathname.includes('/auth')) {
    return null;
  }

  return (
    <nav className="navbar">
      <div className="container">
        <div className="navbar-content">
          <div className="navbar-brand">
            <Link to="/dashboard" className="navbar-logo">
              Task Manager
            </Link>
          </div>
          
          <div className="navbar-links">
            <Link 
              to="/dashboard" 
              className={`navbar-link ${location.pathname === '/dashboard' ? 'active' : ''}`}
            >
              Projects
            </Link>
            {user && (
              <div className="navbar-user">
                <span className="navbar-user-name">
                  {user.name || user.email}
                </span>
                <button 
                  className="btn btn-outline btn-sm"
                  onClick={handleLogout}
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;