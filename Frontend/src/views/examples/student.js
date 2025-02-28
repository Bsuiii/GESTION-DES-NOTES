
import {
    Badge,
    Card,
    CardHeader,
    CardFooter,
    DropdownMenu,
    DropdownItem,
    UncontrolledDropdown,
    DropdownToggle,
    Media,
    Pagination,
    PaginationItem,
    PaginationLink,
    Progress,
    Table,
    Container,
    Row,
    UncontrolledTooltip,
    Form,
    FormGroup,
    Input,
    Button,
    Alert,
} from "reactstrap";
import Header from "components/Headers/Header.js";
import { useState } from "react";
import * as XLSX from "xlsx";
  
const Tables = () => {
    const [students, setStudents] = useState([]);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    const handleFileUpload = (e) => {
      const file = e.target.files[0];
      const reader = new FileReader();
  
      reader.onload = (event) => {
        const data = new Uint8Array(event.target.result);
        const workbook = XLSX.read(data, { type: "array" });
        const sheetName = workbook.SheetNames[0];
        const worksheet = workbook.Sheets[sheetName];
        const json = XLSX.utils.sheet_to_json(worksheet);
  
        if (
          !json[0] ||
          !json[0]["ID ETUDIANT"] ||
          !json[0]["CNE"] ||
          !json[0]["NOM"] ||
          !json[0]["PRENOM"] ||
          !json[0]["ID NIVEAU ACTUEL"]
        ) {
          setError("Le fichier Excel ne correspond pas au format attendu.");
          setStudents([]);
          return;
        }
  
        // i will check i f students exist in data base
        setStudents(json);
        setError(null);
        setSuccess("Fichier téléchargé et validé avec succès.");
      };
  
      reader.readAsArrayBuffer(file);
    };
  
    const handleInscription = () => {
      setSuccess("Inscription des étudiants effectuée avec succès.");
    };
  
    const handleReinscription = () => {
      setSuccess("Réinscription des étudiants effectuée avec succès.");
    };
  
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
                      onChange={handleFileUpload}
                    />
                  </FormGroup>
                  {error && <Alert color="danger">{error}</Alert>}
                  {success && <Alert color="success">{success}</Alert>}
                  <Button color="primary" onClick={handleInscription}>
                    Inscription
                  </Button>{" "}
                  <Button color="success" onClick={handleReinscription}>
                    Réinscription
                  </Button>
                </Form>
                <Table className="align-items-center table-flush" responsive>
                  <thead className="thead-light">
                    <tr>
                      <th scope="col">ID ETUDIANT</th>
                      <th scope="col">CNE</th>
                      <th scope="col">NOM</th>
                      <th scope="col">PRENOM</th>
                      <th scope="col">ID NIVEAU ACTUEL</th>
                    </tr>
                  </thead>
                  <tbody>
                    {students.map((student, index) => (
                      <tr key={index}>
                        <td>{student["ID ETUDIANT"]}</td>
                        <td>{student["CNE"]}</td>
                        <td>{student["NOM"]}</td>
                        <td>{student["PRENOM"]}</td>
                        <td>{student["ID NIVEAU ACTUEL"]}</td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </Card>
            </div>
          </Row>
        </Container>
      </>
    );
  };
  
  export default Tables;