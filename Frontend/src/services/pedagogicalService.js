const API_BASE_URL = "http://localhost:8080";

// Helper function to handle responses
const handleResponse = async (response) => {
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.message || "Request failed");
  }
  return response.json();
};

// 🔹 Fetch all filieres
export const fetchFilieres = async () => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/filieres`, {
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });
  return handleResponse(response);
};

// 🔹 Fetch filiere by ID
export const fetchFiliereById = async (id) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/filieres/${id}`, {
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });
  return handleResponse(response);
};

// 🔹 Create a new filiere
export const createFiliere = async (filiereData) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/filieres`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      'Authorization': token // Include the token in the headers
    },
    body: JSON.stringify(filiereData),
  });
  return handleResponse(response);
};

// 🔹 Update an existing filiere
export const updateFiliere = async (id, filiereData) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/filieres/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      'Authorization': token // Include the token in the headers
    },
    body: JSON.stringify(filiereData),
  });
  return handleResponse(response);
};

// 🔹 Delete a filiere
export const deleteFiliere = async (id) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/filieres/${id}`, {
    method: "DELETE",
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });

  if (!response.ok) {
    throw new Error("Failed to delete filiere");
  }
};

// 🔹 Fetch all modules
export const fetchModules = async () => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/modules`, {
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });
  return handleResponse(response);
};

// 🔹 Fetch module by ID
export const fetchModuleById = async (id) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/modules/${id}`, {
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });
  return handleResponse(response);
};

// 🔹 Create a new module
export const createModule = async (moduleData) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/modules`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      'Authorization': token // Include the token in the headers
    },
    body: JSON.stringify(moduleData),
  });
  return handleResponse(response);
};

// 🔹 Update an existing module
export const updateModule = async (id, moduleData) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/modules/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      'Authorization': token // Include the token in the headers
    },
    body: JSON.stringify(moduleData),
  });
  return handleResponse(response);
};

// 🔹 Delete a module
export const deleteModule = async (id) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/modules/${id}`, {
    method: "DELETE",
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });

  if (!response.ok) {
    throw new Error("Failed to delete module");
  }
};

// 🔹 Fetch all elements
export const fetchElements = async () => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/elements`, {
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });
  return handleResponse(response);
};

// 🔹 Fetch element by ID
export const fetchElementById = async (id) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/elements/${id}`, {
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });
  return handleResponse(response);
};

// 🔹 Create a new element
export const createElement = async (elementData) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/elements`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      'Authorization': token // Include the token in the headers
    },
    body: JSON.stringify(elementData),
  });
  return handleResponse(response);
};

// 🔹 Update an existing element
export const updateElement = async (id, elementData) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/elements/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      'Authorization': token // Include the token in the headers
    },
    body: JSON.stringify(elementData),
  });
  return handleResponse(response);
};

// 🔹 Delete an element
export const deleteElement = async (id) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/elements/${id}`, {
    method: "DELETE",
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });

  if (!response.ok) {
    throw new Error("Failed to delete element");
  }
};

// 🔹 Fetch all niveaux
export const fetchNiveaux = async () => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/niveaux`, {
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });
  return handleResponse(response);
};

// 🔹 Fetch niveau by ID
export const fetchNiveauById = async (id) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/niveaux/${id}`, {
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });
  return handleResponse(response);
};

// 🔹 Create a new niveau
export const createNiveau = async (niveauData) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/niveaux`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      'Authorization': token // Include the token in the headers
    },
    body: JSON.stringify(niveauData),
  });
  return handleResponse(response);
};

// 🔹 Update an existing niveau
export const updateNiveau = async (id, niveauData) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/niveaux/${id}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      'Authorization': token // Include the token in the headers
    },
    body: JSON.stringify(niveauData),
  });
  return handleResponse(response);
};

// 🔹 Delete a niveau
export const deleteNiveau = async (id) => {
  const token = localStorage.getItem('token'); // Get the token from localStorage
  if (!token) {
    throw new Error('No token found. Please log in.');
  }

  const response = await fetch(`${API_BASE_URL}/api/admin_sp/niveaux/${id}`, {
    method: "DELETE",
    headers: {
      'Authorization': token // Include the token in the headers
    }
  });

  if (!response.ok) {
    throw new Error("Failed to delete niveau");
  }
};