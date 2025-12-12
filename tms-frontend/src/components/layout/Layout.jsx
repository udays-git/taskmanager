import React from 'react';
import Navbar from './Navbar';
import '../../styles/app.css';

function Layout({ children }) {
  return (
    <div className="layout">
      <Navbar />
      <main className="layout-main">
        {children}
      </main>
      <footer className="footer">
        <div className="container">
          <p>Task Management System © 2024</p>
        </div>
      </footer>
    </div>
  );
}

export default Layout;