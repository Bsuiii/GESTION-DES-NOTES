import {
  Card,
  CardHeader,
  Table,
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
import { fetchNiveaux } from "services/pedagogicalService"; // Import the fetchNiveaux service
import { downloadNiveauZip } from "services/documentsService"; // Import the zipService

const Tables = () => {
  const [niveaux, setNiveaux] = useState([]); // State to store niveaux
  const [selectedNiveau, setSelectedNiveau] = useState(null); // Selected niveau
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Fetch all niveaux on component mount
  useEffect(() => {
    const loadNiveaux = async () => {
      try {
        const niveauxData = await fetchNiveaux();
        setNiveaux(niveauxData);
      } catch (error) {
        setError("Failed to fetch niveaux. Please try again.");
      }
    };
    loadNiveaux();
  }, []);

  // Handle niveau selection
  const handleNiveauChange = (e) => {
    const niveauId = e.target.value;
    setSelectedNiveau(niveauId);
  };

  // Handle download button click
  const handleDownload = async () => {
    if (!selectedNiveau) {
      setError("Please select a niveau first.");
      return;
    }

    try {
      // Download the ZIP file for the selected niveau
      const blob = await downloadNiveauZip(selectedNiveau);

      // Create a download link for the ZIP file
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = `niveau_${selectedNiveau}_files.zip`; // Set the filename for the download
      document.body.appendChild(a);
      a.click();
      a.remove();

      // Clean up the URL object
      window.URL.revokeObjectURL(url);

      setSuccess("ZIP file downloaded successfully.");
    } catch (error) {
      setError("An error occurred while downloading the file.");
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
                <h3 className="mb-0">Gestion des étudiants</h3>
              </CardHeader>
              {/* Niveau Selection Dropdown */}
              <FormGroup className="p-4">
                <Input
                  type="select"
                  value={selectedNiveau || ""}
                  onChange={handleNiveauChange}
                >
                  <option value="">Sélectionner un niveau</option>
                  {niveaux.map((niveau) => (
                    <option key={niveau.id} value={niveau.id}>
                      {niveau.niveau} ({niveau.alias}) {/* Display niveau and alias */}
                    </option>
                  ))}
                </Input>
              </FormGroup>
              {/* Download Button */}
              <FormGroup className="p-4">
                <Button
                  color="primary"
                  onClick={handleDownload}
                  disabled={!selectedNiveau} // Disable the button if no niveau is selected
                >
                  Télécharger le fichier
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