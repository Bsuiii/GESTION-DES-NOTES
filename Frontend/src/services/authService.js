const API_BASE_URL = 'http://localhost:8080';

const authService = {
  // Login function
  login: async (email, password) => {
    try {
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        throw new Error('Invalid email or password');
      }

      const data = await response.json();
      const token = response.headers.get('Authorization'); // Get token from headers

      // Store the token and user details in localStorage
      localStorage.setItem('token', token);
      localStorage.setItem('userDetails', JSON.stringify(data));

      return data; // Return user details for further use
    } catch (error) {
      console.error('Login failed:', error);
      throw error; // Throw error to handle it in the component
    }
  },

  // Logout function
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userDetails');
  },

  // Get the current user details from localStorage
  getCurrentUser: () => {
    const userDetails = localStorage.getItem('userDetails');
    return userDetails ? JSON.parse(userDetails) : null;
  },

  // Get the token from localStorage
  getToken: () => {
    return localStorage.getItem('token');
  },
};

export default authService;