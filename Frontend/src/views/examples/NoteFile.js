import {
    Card,
    CardHeader,
    Container,
    Row,
    Form,
    FormGroup,
    Input,
    Button,
    Alert,
  } from "reactstrap";
  import Header from "components/Headers/Header.js";
  import { useState, useEffect } from "react";
  import { fetchModules } from "services/pedagogicalService"; // Import fetchModules
  import { exportModuleExcel } from "services/documentsService"; // Import exportModuleExcel
  
  const Tables = () => {
    const [modules, setModules] = useState([]); // State to store modules
    const [selectedModule, setSelectedModule] = useState(null); // Selected module
    const [selectedSession, setSelectedSession] = useState("NORMAL"); // Selected session (default: NORMAL)
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);
  
    // Fetch all modules on component mount
    useEffect(() => {
      const loadModules = async () => {
        try {
          const modulesData = await fetchModules(); // Fetch all modules
          setModules(modulesData);
        } catch (error) {
          setError("Failed to fetch modules. Please try again.");
        }
      };
      loadModules();
    }, []);
  
    // Handle module selection
    const handleModuleChange = (e) => {
      const moduleId = e.target.value;
      setSelectedModule(moduleId);
    };
  
    // Handle session selection
    const handleSessionChange = (e) => {
      const session = e.target.value;
      setSelectedSession(session);
    };
  
    // Handle export button click
    const handleExport = async () => {
      if (!selectedModule || !selectedSession) {
        setError("Please select a module and a session first.");
        return;
      }
  
      try {
        // Export the module details to Excel
        const blob = await exportModuleExcel(selectedModule, selectedSession);
  
        // Create a download link for the Excel file
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = `module_${selectedModule}_${selectedSession}.xlsx`; // Set the filename for the download
        document.body.appendChild(a);
        a.click();
        a.remove();
  
        // Clean up the URL object
        window.URL.revokeObjectURL(url);
  
        setSuccess("Excel file exported successfully.");
      } catch (error) {
        setError("An error occurred while exporting the file.");
      }
    };
  
    // Automatically dismiss messages after 10 seconds
    useEffect(() => {
      const timer = setTimeout(() => {
        setError(null);
        setSuccess(null);
      }, 10000); // 10 seconds
  
      // Clear the timer when the component unmounts or when error/success changes
      return () => clearTimeout(timer);
    }, [error, success]);
  
    return (
      <>
        <Header />
        <Container className="mt--7" fluid>
          <Row>
            <div className="col">
              <Card className="shadow">
                <CardHeader className="border-0">
                  <h3 className="mb-0">Gestion des modules</h3>
                </CardHeader>
                {/* Module Selection Dropdown */}
                <FormGroup className="p-4">
                  <Input
                    type="select"
                    value={selectedModule || ""}
                    onChange={handleModuleChange}
                  >
                    <option value="">Sélectionner un module</option>
                    {modules.map((module) => (
                      <option key={module.id} value={module.id}>
                        {module.titre} {/* Display module titre */}
                      </option>
                    ))}
                  </Input>
                </FormGroup>
                {/* Session Selection Dropdown */}
                <FormGroup className="p-4">
                  <Input
                    type="select"
                    value={selectedSession}
                    onChange={handleSessionChange}
                  >
                    <option value="NORMAL">Session Normale</option>
                    <option value="RATTRAPAGE">Session de Rattrapage</option>
                  </Input>
                </FormGroup>
                {/* Export Button */}
                <FormGroup className="p-4">
                  <Button
                    color="primary"
                    onClick={handleExport}
                    disabled={!selectedModule || !selectedSession} // Disable if no module or session is selected
                  >
                    Exporter en Excel
                  </Button>
                </FormGroup>
                {/* Error Alert with close button */}
                {error && (
                  <Alert color="danger" toggle={() => setError(null)}>
                    {error}
                  </Alert>
                )}
                {/* Success Alert with close button */}
                {success && (
                  <Alert color="success" toggle={() => setSuccess(null)}>
                    {success}
                  </Alert>
                )}
              </Card>
            </div>
          </Row>
        </Container>
      </>
    );
  };
  
  export default Tables;