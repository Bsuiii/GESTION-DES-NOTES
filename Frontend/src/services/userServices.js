const API_BASE_URL = 'http://localhost:8080';

// Helper function to handle responses
const handleResponse = async (response) => {
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Request failed');
  }
  return response.json();
};

// Fetch all users
export const fetchUsers = async () => {
  const token = localStorage.getItem('token'); 
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_users/users`, {
    headers: {
      'Authorization': token 
    }
  });
  return handleResponse(response);
};

// Fetch user by ID
export const fetchUserById = async (id) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_users/users/${id}`, {
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });
  return handleResponse(response);
};

// Create a new user
export const createUser = async (user) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_users/users`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token // Include the token in the headers
    },
    body: JSON.stringify(user)
  });
  return handleResponse(response);
};

// Update user
export const updateUser = async (id, userDetails) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_users/users/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token // Include the token in the headers
    },
    body: JSON.stringify(userDetails)
  });
  return handleResponse(response);
};

// Delete user
export const deleteUser = async (id) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_users/users/${id}`, {
    method: 'DELETE',
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });
  if (!response.ok) {
    throw new Error('Failed to delete user');
  }
  return; // No need to parse JSON if the backend doesn't return anything
};

// Login user
export const loginUser = async (credentials) => {
  const response = await fetch(`${API_BASE_URL}/auth/login`, { // Updated endpoint to match your backend
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(credentials)
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || 'Login failed');
  }

  const data = await response.json();
  const token = response.headers.get('Authorization'); // Get token from headers

  // Store the token and user details in localStorage
  localStorage.setItem('token', token);
  localStorage.setItem('userDetails', JSON.stringify(data));

  return data; // Return user details for further use
};