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
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
} from "reactstrap";
import Header from "components/Headers/Header.js";
import { useState, useEffect } from "react";
import excelService from "services/excelService"; // Updated service

const Tables = () => {
  const [students, setStudents] = useState([]);
  const [file, setFile] = useState(null); // Store the uploaded file
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [searchTerm, setSearchTerm] = useState(""); // For searching students
  const [editModalOpen, setEditModalOpen] = useState(false); // For edit modal
  const [selectedStudent, setSelectedStudent] = useState(null); // Selected student for editing
  const [modificationHistory, setModificationHistory] = useState([]); // History of modifications

  // Automatically dismiss messages after 10 seconds
  useEffect(() => {
    const timer = setTimeout(() => {
      setError(null);
      setSuccess(null);
    }, 10000); // 10 seconds

    // Clear the timer when the component unmounts or when error/success changes
    return () => clearTimeout(timer);
  }, [error, success]);

  // Handle file selection
  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (!selectedFile) {
      setError("Veuillez sélectionner un fichier.");
      return;
    }
    setFile(selectedFile); // Store the file
    setError(null);
  };

  // Handle file upload when the user clicks "Submit"
  const handleSubmit = async () => {
    if (!file) {
      setError("Veuillez sélectionner un fichier.");
      return;
    }

    try {
      // Fetch data from the Excel file
      const result = await excelService.uploadExcelFile(file);
      console.log("Raw data from Excel:", result); // Debugging log

      // Check if the result is an array and has data
      if (!Array.isArray(result) || result.length === 0) {
        setError("Le fichier Excel est vide ou mal formaté.");
        return;
      }

      // Map the result to include only the required columns
      const formattedStudents = result.map((student) => ({
        id: student.id,
        cne: student.cne,
        nom: student.nom,
        prenom: student.prenom,
        niveau_id: student.niveau_id,
      }));

      console.log("Formatted students:", formattedStudents); // Debugging log

      // Update the state with the formatted students
      setStudents(formattedStudents);
      setError(null);
      setSuccess("Fichier téléchargé et validé avec succès.");
    } catch (error) {
      console.error("Error during file upload:", error);
      setError("Erreur lors du téléchargement du fichier.");
      setStudents([]);
    }
  };

  // Handle student data modification
  const handleEditStudent = (student) => {
    setSelectedStudent(student);
    setEditModalOpen(true);
  };

  // Save modifications
  const handleSaveChanges = async () => {
    if (!selectedStudent) return;

    // Update the student in the state
    const updatedStudents = students.map((student) =>
      student.id === selectedStudent.id ? selectedStudent : student
    );
    setStudents(updatedStudents);

    // Log the modification
    const modificationLog = {
      userId: "currentUser", // Replace with actual user ID
      date: new Date().toISOString(),
      studentId: selectedStudent.id,
      changes: selectedStudent,
    };
    setModificationHistory([...modificationHistory, modificationLog]);

    // Save changes to Excel (you need to implement this in excelService)
    try {
      await excelService.updateExcelFile(updatedStudents);
      setSuccess("Modifications enregistrées avec succès.");
    } catch (error) {
      setError("Erreur lors de la sauvegarde des modifications.");
    }

    setEditModalOpen(false);
  };

  // Handle search
  const filteredStudents = students.filter(
    (student) =>
      student.cne.includes(searchTerm) ||
      student.nom.includes(searchTerm) ||
      student.prenom.includes(searchTerm)
  );

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
              <Form className="p-4">
                <FormGroup>
                  <Input
                    type="file"
                    accept=".xlsx, .xls"
                    onChange={handleFileChange}
                  />
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
                <Button color="primary" onClick={handleSubmit}>
                  Submit
                </Button>
              </Form>
              <FormGroup className="p-4">
                <Input
                  type="text"
                  placeholder="Rechercher un étudiant (CNE, Nom, Prénom)"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </FormGroup>
              <Table className="align-items-center table-flush" responsive>
                <thead className="thead-light">
                  <tr>
                    <th scope="col">ID ETUDIANT</th>
                    <th scope="col">CNE</th>
                    <th scope="col">NOM</th>
                    <th scope="col">PRENOM</th>
                    <th scope="col">ID NIVEAU ACTUEL</th>
                    <th scope="col">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredStudents.map((student, index) => (
                    <tr key={index}>
                      <td>{student.id}</td>
                      <td>{student.cne}</td>
                      <td>{student.nom}</td>
                      <td>{student.prenom}</td>
                      <td>{student.niveau_id}</td>
                      <td>
                        <Button
                          color="primary"
                          size="sm"
                          onClick={() => handleEditStudent(student)}
                        >
                          Modifier
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card>
          </div>
        </Row>
      </Container>

      {/* Edit Modal */}
      <Modal isOpen={editModalOpen} toggle={() => setEditModalOpen(false)}>
        <ModalHeader toggle={() => setEditModalOpen(false)}>
          Modifier les informations de l'étudiant
        </ModalHeader>
        <ModalBody>
          <Form>
            <FormGroup>
              <label>CNE</label>
              <Input
                type="text"
                value={selectedStudent?.cne || ""}
                onChange={(e) =>
                  setSelectedStudent({
                    ...selectedStudent,
                    cne: e.target.value,
                  })
                }
              />
            </FormGroup>
            <FormGroup>
              <label>Nom</label>
              <Input
                type="text"
                value={selectedStudent?.nom || ""}
                onChange={(e) =>
                  setSelectedStudent({
                    ...selectedStudent,
                    nom: e.target.value,
                  })
                }
              />
            </FormGroup>
            <FormGroup>
              <label>Prénom</label>
              <Input
                type="text"
                value={selectedStudent?.prenom || ""}
                onChange={(e) =>
                  setSelectedStudent({
                    ...selectedStudent,
                    prenom: e.target.value,
                  })
                }
              />
            </FormGroup>
            <FormGroup>
              <label>Niveau</label>
              <Input
                type="text"
                value={selectedStudent?.niveau_id || ""}
                onChange={(e) =>
                  setSelectedStudent({
                    ...selectedStudent,
                    niveau_id: e.target.value,
                  })
                }
              />
            </FormGroup>
          </Form>
        </ModalBody>
        <ModalFooter>
          <Button color="primary" onClick={handleSaveChanges}>
            Enregistrer
          </Button>
          <Button color="secondary" onClick={() => setEditModalOpen(false)}>
            Annuler
          </Button>
        </ModalFooter>
      </Modal>
    </>
  );
};

export default Tables;