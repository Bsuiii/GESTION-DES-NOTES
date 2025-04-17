const API_BASE_URL = "http://localhost:8080";

// Handle API responses
const handleResponse = async (response) => {
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || "Request failed");
  }
  return response.json();
};

// 🔹 Upload Excel file
const uploadExcelFile = async (file) => {
  const token = localStorage.getItem("token");
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${API_BASE_URL}/api/admin_notes/excel/upload`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: formData,
  });

  return handleResponse(response);
};

// 🔹 Fetch all etudiants
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

  return handleResponse(response);
};

// 🔹 Create a new etudiant
const createEtudiant = async (etudiantData) => {
  const token = localStorage.getItem("token");
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/etudiants`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(etudiantData),
  });

  return handleResponse(response);
};

// 🔹 Update multiple etudiants
const updateEtudiants = async (etudiantsData) => {
  const token = localStorage.getItem("token");
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/etudiants/change_info`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(etudiantsData),
  });

  return handleResponse(response);
};

export default {
  uploadExcelFile,
  fetchEtudiants,
  createEtudiant,
  updateEtudiants,
};