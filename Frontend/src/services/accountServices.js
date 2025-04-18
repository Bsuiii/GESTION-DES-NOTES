const API_BASE_URL = "http://localhost:8080";

// Handle API responses
const handleResponse = async (response) => {
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || "Request failed");
  }
  return response.json();
};

// 🔹 Create account (with token from localStorage)
const createAccount = async (accountData) => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_accounts/accounts/add`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
    body: JSON.stringify(accountData),
  });

  return handleResponse(response);
};

// 🔹 Fetch all accounts (with token from localStorage)
const fetchAccounts = async () => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_accounts/accounts`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
  });

  return handleResponse(response);
};

// 🔹 Update account (with token from localStorage)
const updateAccount = async (id, accountData) => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_accounts/accounts/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
    body: JSON.stringify(accountData),
  });

  return handleResponse(response);
};

// 🔹 Delete account (with token from localStorage)
const deleteAccount = async (id) => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_accounts/accounts/${id}`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
  });

  return handleResponse(response);
};

// Export all service functions
export default {
  createAccount,
  fetchAccounts,
  updateAccount,
  deleteAccount,
};