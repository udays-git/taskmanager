import api from './api';

const AuthService = {
  login: (credentials) => {
    return api.post('/auth/login', {
      name: credentials.name,
      email: credentials.email
    });
  },

  register: (userData) => api.post('/auth/register', userData),

  logout: () => {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  },

  getCurrentUser: () => {
    try {
      const raw = localStorage.getItem('user');
      return raw ? JSON.parse(raw) : null;
    } catch (e) {
      return null;
    }
  },

  persistLoginResponse: (res) => {
    if (!res || !res.data) return;
    const data = res.data;
    if (data.token) {
      localStorage.setItem('token', data.token);
    }
    const user = data.user ?? data;
    try {
      localStorage.setItem('user', JSON.stringify(user));
    } catch (e) {
    }
  }
};

export default AuthService;
