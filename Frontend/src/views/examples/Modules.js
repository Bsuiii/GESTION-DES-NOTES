import React, { useState, useEffect } from "react";
import {
  Card,
  CardHeader,
  CardBody,
  Table,
  Button,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Form,
  FormGroup,
  Label,
  Input,
  Container,
  Row,
  Col,
  Alert
} from "reactstrap";
import Header from "components/Headers/Header.js";
import { 
  fetchModules, 
  createModule, 
  deleteModule, 
  fetchNiveaux, 
  fetchFilieres 
} from "services/pedagogicalService";

const Modules = () => {
  // State management
  const [modules, setModules] = useState([]);
  const [niveaux, setNiveaux] = useState([]);
  const [filieres, setFilieres] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  // Form state with all required fields
  const [formData, setFormData] = useState({
    titre: "",
    code: "",
    description: "",
    niveau_id: "",
    filiere_id: "",
    semester: "AUTOMNE" // Default value based on ENUM
  });

  // Fetch initial data
  useEffect(() => {
    const loadData = async () => {
      try {
        setIsLoading(true);
        const [modulesData, niveauxData, filieresData] = await Promise.all([
          fetchModules(),
          fetchNiveaux(),
          fetchFilieres()
        ]);
        setModules(modulesData);
        setNiveaux(niveauxData);
        setFilieres(filieresData);
      } catch (error) {
        setError("Failed to load data: " + error.message);
      } finally {
        setIsLoading(false);
      }
    };
    loadData();
  }, []);

  // Modal toggle
  const toggleModal = () => {
    setModalOpen(!modalOpen);
    setError(null);
    setSuccess(null);
    if (!modalOpen) {
      setFormData({
        titre: "",
        code: "",
        description: "",
        niveau_id: "",
        filiere_id: "",
        semester: "AUTOMNE"
      });
    }
  };

  // Handle input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  // Handle form submission
  const handleSubmit = async () => {
    // Validate all required fields
    const requiredFields = ['titre', 'code', 'niveau_id', 'filiere_id', 'semester'];
    const missingFields = requiredFields.filter(field => !formData[field]);
  
    if (missingFields.length > 0) {
      setError(`Champs obligatoires manquants: ${missingFields.join(', ')}`);
      return;
    }
  
    try {
      // Create payload with all required fields
      const modulePayload = {
        titre: formData.titre,
        code: formData.code,
        description: formData.description,
        niveau_id: formData.niveau_id,
        filiere_id: formData.filiere_id,
        semester: formData.semester // This must be either 'AUTOMNE' or 'PRINTEMPS'
      };
  
      const created = await createModule(modulePayload);
      setModules([...modules, created]);
      setSuccess("Module créé avec succès!");
      
      // Reset form and close modal after 1.5 seconds
      setTimeout(() => {
        setFormData({
          titre: "",
          code: "",
          description: "",
          niveau_id: "",
          filiere_id: "",
          semester: "AUTOMNE" // Reset to default
        });
        setModalOpen(false);
        setSuccess(null);
      }, 1500);
  
    } catch (error) {
      const errorMessage = error.response?.data?.message || 
                          error.message || 
                          "Erreur lors de la création du module";
      setError(errorMessage);
    }
  };

  // Handle module deletion
  const handleDelete = async (id) => {
    if (window.confirm("Êtes-vous sûr de vouloir supprimer ce module?")) {
      try {
        await deleteModule(id);
        setModules(modules.filter(m => m.id !== id));
        setSuccess("Module supprimé avec succès");
        setTimeout(() => setSuccess(null), 3000);
      } catch (error) {
        setError("Erreur lors de la suppression: " + error.message);
      }
    }
  };

  // Loading state
  if (isLoading) {
    return (
      <>
        <Header />
        <Container className="mt--7" fluid>
          <Row>
            <Col>
              <Card className="shadow">
                <CardBody>
                  <div className="text-center">
                    <div className="spinner-border text-primary" role="status">
                      <span className="sr-only">Loading...</span>
                    </div>
                  </div>
                </CardBody>
              </Card>
            </Col>
          </Row>
        </Container>
      </>
    );
  }

  return (
    <>
      <Header />
      <Container className="mt--7" fluid>
        {error && <Alert color="danger">{error}</Alert>}
        {success && <Alert color="success">{success}</Alert>}
        
        <Row>
          <Col>
            <Card className="shadow">
              <CardHeader className="border-0">
                <h3 className="mb-0">Gestion des Modules</h3>
              </CardHeader>
              <CardBody>
                <div className="d-flex justify-content-end mb-3">
                  <Button color="success" onClick={toggleModal}>
                    <i className="ni ni-fat-add mr-1"></i> Ajouter un Module
                  </Button>
                </div>
                
                <Table className="align-items-center table-flush" responsive>
                  <thead className="thead-light">
                    <tr>
                      <th>Titre</th>
                      <th>Code</th>
                      <th>Description</th>
                      <th>Niveau</th>
                      <th>Filière</th>
                      <th>Semestre</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {modules.map((module) => (
                      <tr key={module.id}>
                        <td>{module.titre}</td>
                        <td>{module.code}</td>
                        <td>{module.description}</td>
                        <td>{niveaux.find(n => n.id === module.niveau_id)?.niveau || module.niveau_id}</td>
                        <td>{filieres.find(f => f.id === module.filiere_id)?.alias || module.filiere_id}</td>
                        <td>{module.semester}</td>
                        <td>
                          <Button color="primary" size="sm" className="mr-2">
                            <i className="ni ni-settings"></i> Modifier
                          </Button>
                          <Button 
                            color="danger" 
                            size="sm"
                            onClick={() => handleDelete(module.id)}
                          >
                            <i className="ni ni-fat-remove"></i> Supprimer
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </CardBody>
            </Card>
          </Col>
        </Row>

        {/* Create/Edit Module Modal */}
        <Modal isOpen={modalOpen} toggle={toggleModal} size="lg">
          <ModalHeader toggle={toggleModal}>
            <i className="ni ni-book-bookmark mr-2"></i>
            {formData.id ? "Modifier Module" : "Ajouter un Module"}
          </ModalHeader>
          <ModalBody>
            <Form>
              <Row>
                <Col md="6">
                  <FormGroup>
                    <Label>Titre*</Label>
                    <Input
                      type="text"
                      name="titre"
                      value={formData.titre}
                      onChange={handleInputChange}
                      placeholder="Titre du module"
                      required
                    />
                  </FormGroup>
                </Col>
                <Col md="6">
                  <FormGroup>
                    <Label>Code*</Label>
                    <Input
                      type="text"
                      name="code"
                      value={formData.code}
                      onChange={handleInputChange}
                      placeholder="Code du module"
                      required
                    />
                  </FormGroup>
                </Col>
              </Row>

              <FormGroup>
                <Label>Description</Label>
                <Input
                  type="textarea"
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  placeholder="Description du module"
                  rows="3"
                />
              </FormGroup>

              <Row>
                <Col md="6">
                  <FormGroup>
                    <Label>Niveau*</Label>
                    <Input
                      type="select"
                      name="niveau_id"
                      value={formData.niveau_id}
                      onChange={handleInputChange}
                      required
                    >
                      <option value="">Sélectionner un niveau</option>
                      {niveaux.map((niveau) => (
                        <option key={niveau.id} value={niveau.id}>
                          {niveau.niveau} ({niveau.alias})
                        </option>
                      ))}
                    </Input>
                  </FormGroup>
                </Col>
                <Col md="6">
                  <FormGroup>
                    <Label>Filière*</Label>
                    <Input
                      type="select"
                      name="filiere_id"
                      value={formData.filiere_id}
                      onChange={handleInputChange}
                      required
                    >
                      <option value="">Sélectionner une filière</option>
                      {filieres.map((filiere) => (
                        <option key={filiere.id} value={filiere.id}>
                          {filiere.alias} - {filiere.intitule}
                        </option>
                      ))}
                    </Input>
                  </FormGroup>
                </Col>
              </Row>

              <FormGroup>
                <Label>Semestre*</Label>
                <Input
                  type="select"
                  name="semester"
                  value={formData.semester}
                  onChange={handleInputChange}
                  required
                >
                  <option value="AUTOMNE">Automne</option>
                  <option value="PRINTEMPS">Printemps</option>
                </Input>
              </FormGroup>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={toggleModal}>
              <i className="ni ni-fat-remove mr-1"></i> Annuler
            </Button>
            <Button color="primary" onClick={handleSubmit}>
              <i className="ni ni-check-bold mr-1"></i> Sauvegarder
            </Button>
          </ModalFooter>
        </Modal>
      </Container>
    </>
  );
};

export default Modules;