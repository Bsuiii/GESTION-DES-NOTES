const API_BASE_URL = "http://localhost:8080";

// Handle API responses
const handleResponse = async (response) => {
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || "Request failed");
  }
  return response.json();
};

// 🔹 Get all inscriptions
const fetchInscriptions = async () => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/etudiant_inscriptions`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
  });

  return handleResponse(response);
};

// 🔹 Create a new inscription
const createInscription = async (inscriptionData) => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/etudiant_inscriptions`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
    body: JSON.stringify(inscriptionData),
  });

  return handleResponse(response);
};

// 🔹 Update an existing inscription
const updateInscription = async (id, inscriptionData) => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/etudiant_inscriptions/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
    body: JSON.stringify(inscriptionData),
  });

  return handleResponse(response);
};

// 🔹 Delete an inscription
const deleteInscription = async (id) => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/etudiant_inscriptions/${id}`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
  });

  return handleResponse(response);
};

export default {
  fetchInscriptions,
  createInscription,
  updateInscription,
  deleteInscription,
};