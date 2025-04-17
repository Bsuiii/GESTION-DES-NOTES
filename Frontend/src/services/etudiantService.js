const API_BASE_URL = "http://localhost:8080";

// Handle API responses
const handleResponse = async (response) => {
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || "Request failed");
  }
  return response.json();
};

// 🔹 Get all etudiants
const fetchEtudiants = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("No token found. Please log in.");
    }
  
    const response = await fetch(`${API_BASE_URL}/api/admin_sp/etudiants`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
  
    if (!response.ok) {
      throw new Error("Failed to fetch students");
    }
  
    return response.json();
  };

// 🔹 Create a new etudiant
const createEtudiant = async (etudiantData) => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/etudiants`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
    body: JSON.stringify(etudiantData),
  });

  return handleResponse(response);
};

// 🔹 Update an existing etudiant
const updateEtudiant = async (id, etudiantData) => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/etudiants/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
    body: JSON.stringify(etudiantData),
  });

  return handleResponse(response);
};

// 🔹 Update multiple etudiants
const updateEtudiants = async (etudiantsData) => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/etudiants/change_info`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
    body: JSON.stringify(etudiantsData),
  });

  return handleResponse(response);
};

// 🔹 Delete an etudiant
const deleteEtudiant = async (id) => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/etudiants/${id}`, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
      Authorization: token, // Include the token in the headers
    },
  });

  return handleResponse(response);
};

export default {
  fetchEtudiants,
  createEtudiant,
  updateEtudiant,
  updateEtudiants,
  deleteEtudiant,
};