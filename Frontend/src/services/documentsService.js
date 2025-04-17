const API_BASE_URL = 'http://localhost:8080';

export const downloadNiveauZip = async (niveauId) => {
  const token = localStorage.getItem("token"); // Get the token from localStorage
  if (!token) {
    throw new Error("No token found. Please log in.");
  }

  const response = await fetch(`${API_BASE_URL}/api/zip/export/niveau/${niveauId}`, {
    headers: {
      Authorization: token, // Include the token in the headers
    },
  });
  if (!response.ok) {
    throw new Error("Failed to download ZIP file.");
  }

  // Convert the response to a blob
  const blob = await response.blob();
  return blob;
};

export const exportModuleExcel = async (moduleId, session) => {
    const token = localStorage.getItem("token"); // Get the token from localStorage
    if (!token) {
      throw new Error("No token found. Please log in.");
    }
  
    try {
      const response = await fetch(
        `${API_BASE_URL}/api/admin_notes/excel/modules/${moduleId}/details/export?session=${session}`,
        {
          headers: {
            Authorization: token, // Include the token in the headers
          },
        }
      );
  
      if (!response.ok) {
        const errorData = await response.json(); // Parse error response if available
        throw new Error(errorData.message || "Failed to export module details.");
      }
  
      // Convert the response to a blob
      const blob = await response.blob();
      return blob;
    } catch (error) {
      console.error("Error exporting module details:", error);
      throw new Error(error.message || "An error occurred while exporting the file.");
    }
  };