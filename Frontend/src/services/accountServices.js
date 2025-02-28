const API_BASE_URL = "http://localhost:8080";

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

  const response = await fetch(`${API_BASE_URL}/api/admin_users/accounts/add`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
    body: JSON.stringify(accountData),
  });

  return handleResponse(response);
};

export default createAccount;
