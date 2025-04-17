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
  Dropdown,
  DropdownToggle,
  DropdownMenu,
  DropdownItem,
} from "reactstrap";
import Header from "components/Headers/Header.js"; // Ensure this path is correct
import {
  fetchFilieres,
  createFiliere,
  updateFiliere,
  deleteFiliere,
  fetchModules,
  createModule,
  deleteModule,
  fetchElements,
  createElement,
  deleteElement,
  fetchNiveaux, 
  createNiveau, 
  deleteNiveau
} from "services/pedagogicalService"; // Import your service functions

const PedagogicalStructure = () => {
  // State for modals
  const [modalCreateFiliere, setModalCreateFiliere] = useState(false);
  const [modalCreateModule, setModalCreateModule] = useState(false);
  const [modalCreateElement, setModalCreateElement] = useState(false);
  const [modalAssignModule, setModalAssignModule] = useState(false);
  const [modalCreateNiveau, setModalCreateNiveau] = useState(false); // New modal for niveaux
  const [modalAssignElement, setModalAssignElement] = useState(false);

  // State for data
  const [filieres, setFilieres] = useState([]);
  const [modules, setModules] = useState([]);
  const [elements, setElements] = useState([]);
  const [selectedFiliere, setSelectedFiliere] = useState(null);
  const [niveaux, setNiveaux] = useState([]); // New state for niveaux
  const [selectedModule, setSelectedModule] = useState(null);

  // State for new entries
  const [newFiliere, setNewFiliere] = useState({
    alias: "",
    intitule: "",
    anneeAccreditation: "",
    anneeFinAccreditation: "",
    coordonnateur_id: "",
  });
  const [newModule, setNewModule] = useState({
    titre: "",
    code: "",
    niveau :"",
    niveau_id: "",
    filiere_id: "",
  });
  const [newElement, setNewElement] = useState({
    titre: "",
    module_id: "",
  });
  const [newNiveau, setNewNiveau] = useState({
    alias: "",
    niveau: "",
    filiere_id: "",
    is_niveau_suivant: false,
  });

  // Fetch data on component mount
  useEffect(() => {
    const loadData = async () => {
      try {
        const filieresData = await fetchFilieres();
        const modulesData = await fetchModules();
        const elementsData = await fetchElements();
        const niveauxData = await fetchNiveaux(); // Fetch niveaux data
        setFilieres(filieresData);
        setModules(modulesData);
        setElements(elementsData);
        setNiveaux(niveauxData); // Set niveaux data
      } catch (error) {
        console.error("Failed to fetch data:", error);
      }
    };
    loadData();
  }, []);

  // Toggle modals
  const toggleCreateFiliere = () => setModalCreateFiliere(!modalCreateFiliere);
  const toggleCreateModule = () => setModalCreateModule(!modalCreateModule);
  const toggleCreateElement = () => setModalCreateElement(!modalCreateElement);
  const toggleAssignModule = () => setModalAssignModule(!modalAssignModule);
  const toggleAssignElement = () => setModalAssignElement(!modalAssignElement);
  const toggleCreateNiveau = () => setModalCreateNiveau(!modalCreateNiveau); 


  // Handle input changes
  const handleFiliereInputChange = (e) => {
    const { name, value } = e.target;
    setNewFiliere({ ...newFiliere, [name]: value });
  };
  const handleModuleInputChange = (e) => {
    const { name, value } = e.target;
    setNewModule({ ...newModule, [name]: value });
  };
  const handleElementInputChange = (e) => {
    const { name, value } = e.target;
    setNewElement({ ...newElement, [name]: value });
  };
  const handleNiveauInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setNewNiveau({
      ...newNiveau,
      [name]: type === "checkbox" ? checked : value,
    });
  };


  // Handle creating new entries
  const handleCreateFiliere = async () => {
    try {
      const createdFiliere = await createFiliere(newFiliere);
      setFilieres([...filieres, createdFiliere]);
      toggleCreateFiliere();
      setNewFiliere({
        alias: "",
        intitule: "",
        anneeAccreditation: "",
        anneeFinAccreditation: "",
        coordonnateur_id: "",
      });
    } catch (error) {
      console.error("Failed to create filière:", error);
    }
  };
  const handleCreateModule = async () => {
    try {
      const createdModule = await createModule(newModule);
      setModules([...modules, createdModule]);
      toggleCreateModule();
      setNewModule({
        titre: "",
        code: "",
        niveau:"",
        niveau_id: "",
        filiere_id: "",
      });
    } catch (error) {
      console.error("Failed to create module:", error);
    }
  };
  const handleCreateElement = async () => {
    try {
      const createdElement = await createElement(newElement);
      setElements([...elements, createdElement]);
      toggleCreateElement();
      setNewElement({
        titre: "",
        module_id: "",
      });
    } catch (error) {
      console.error("Failed to create element:", error);
    }
  };

  // Handle assigning modules to classes and elements to modules
  const handleAssignModule = async (filiereId, moduleId) => {
    try {
      // Logic to assign module to filiere
      console.log(`Module ${moduleId} assigned to Filiere ${filiereId}`);
      toggleAssignModule();
    } catch (error) {
      console.error("Failed to assign module:", error);
    }
  };
  const handleAssignElement = async (moduleId, elementId) => {
    try {
      // Logic to assign element to module
      console.log(`Element ${elementId} assigned to Module ${moduleId}`);
      toggleAssignElement();
    } catch (error) {
      console.error("Failed to assign element:", error);
    }
  };
  const [editingFiliere, setEditingFiliere] = useState(null);

// Open edit modal with current filière data
const handleEditFiliere = (filiere) => {
  setEditingFiliere(filiere);
  setModalCreateFiliere(true);
};

// Handle updating a filière
const handleUpdateFiliere = async () => {
  try {
    const updatedFiliere = await updateFiliere(editingFiliere.id, editingFiliere);
    setFilieres(filieres.map(f => (f.id === updatedFiliere.id ? updatedFiliere : f)));
    setModalCreateFiliere(false);
    setEditingFiliere(null);
  } catch (error) {
    console.error("Failed to update filière:", error);
  }
};

// Handle deleting a filière
const handleDeleteFiliere = async (id) => {
  try {
    await deleteFiliere(id);
    setFilieres(filieres.filter(f => f.id !== id));
  } catch (error) {
    console.error("Failed to delete filière:", error);
  }
};
const handleCreateNiveau = async () => {
  try {
    const createdNiveau = await createNiveau(newNiveau);
    setNiveaux([...niveaux, createdNiveau]);
    toggleCreateNiveau();
    setNewNiveau({
      alias: "",
      niveau: "",
      filiere_id: "",
      is_niveau_suivant: false,
    });
  } catch (error) {
    console.error("Failed to create niveau:", error);
  }
};

// Handle deleting a niveau
const handleDeleteNiveau = async (id) => {
  try {
    await deleteNiveau(id);
    setNiveaux(niveaux.filter((n) => n.id !== id));
  } catch (error) {
    console.error("Failed to delete niveau:", error);
  }
};

  return (
    <>
      <Header />
      <Container className="mt--7" fluid>
        <Row>
          <Col>
            <Card className="shadow">
              <CardHeader className="border-0">
                <h3 className="mb-0">Gestion de la Structure Pédagogique</h3>
              </CardHeader>
              <CardBody>
                {/* Filières Table */}
                <h4 className="mb-4">Filières</h4>
                <div className="d-flex justify-content-end mb-3">
                  <Button color="success" onClick={toggleCreateFiliere}>
                    Ajouter une Filière
                  </Button>
                </div>
                <Table className="align-items-center table-flush" responsive>
                  <thead className="thead-light">
                    <tr>
                      <th scope="col">Alias</th>
                      <th scope="col">Intitulé</th>
                      <th scope="col">Accréditation</th>
                      <th scope="col">Fin Accréditation</th>
                      <th scope="col">Coordonnateur</th>
                      <th scope="col">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filieres.map((filiere) => (
                      <tr key={filiere.id}>
                        <td>{filiere.alias}</td>
                        <td>{filiere.intitule}</td>
                        <td>{filiere.anneeAccreditation}</td>
                        <td>{filiere.anneeFinAccreditation}</td>
                        <td>{filiere.coordonnateur_nomComplet}</td>
                        <td>
                        <Button color="primary" size="sm" onClick={() => handleEditFiliere(filiere)}>
                          Modifier
                        </Button>{" "}
                        <Button color="danger" size="sm" onClick={() => handleDeleteFiliere(filiere.id)}>
                          Supprimer
                        </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
                <h4 className="mb-4 mt-5">Niveaux</h4>
                <div className="d-flex justify-content-end mb-3">
                  <Button color="success" onClick={toggleCreateNiveau}>
                    Ajouter un Niveau
                  </Button>
                </div>
                <Table className="align-items-center table-flush" responsive>
                  <thead className="thead-light">
                    <tr>
                      <th scope="col">Alias</th>
                      <th scope="col">Niveau</th>
                      <th scope="col">Filière</th>
                      <th scope="col">Niveau Suivant</th>
                      <th scope="col">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {niveaux.map((niveau) => (
                      <tr key={niveau.id}>
                        <td>{niveau.alias}</td>
                        <td>{niveau.niveau}</td>
                        <td>{niveau.filiere_id}</td>
                        <td>{niveau.is_niveau_suivant ? "Oui" : "Non"}</td>
                        <td>
                          <Button color="primary" size="sm">
                            Modifier
                          </Button>{" "}
                          <Button
                            color="danger"
                            size="sm"
                            onClick={() => handleDeleteNiveau(niveau.id)}
                          >
                            Supprimer
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
                {/* Modules Table */}
                <h4 className="mb-4 mt-5">Modules</h4>
                <div className="d-flex justify-content-end mb-3">
                  <Button color="success" onClick={toggleCreateModule}>
                    Ajouter un Module
                  </Button>
                </div>
                <Table className="align-items-center table-flush" responsive>
                  <thead className="thead-light">
                    <tr>
                      <th scope="col">Titre</th>
                      <th scope="col">Code</th>
                      <th scope="col">Niveau</th>
                      <th scope="col">Filière</th>
                      <th scope="col">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {modules.map((module) => (
                      <tr key={module.id}>
                        <td>{module.titre}</td>
                        <td>{module.code}</td>
                        <td>{module.niveau_alias}</td>
                        <td>{module.filiere_id}</td>
                        <td>
                          <Button color="primary" size="sm">
                            Modifier
                          </Button>{" "}
                          <Button color="danger" size="sm">
                            Supprimer
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </Table>

                {/* Elements Table */}
                <h4 className="mb-4 mt-5">Éléments</h4>
                <div className="d-flex justify-content-end mb-3">
                  <Button color="success" onClick={toggleCreateElement}>
                    Ajouter un Élément
                  </Button>
                </div>
                <Table className="align-items-center table-flush" responsive>
                  <thead className="thead-light">
                    <tr>
                      <th scope="col">Titre</th>
                      <th scope="col">Module</th>
                      <th scope="col">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {elements.map((element) => (
                      <tr key={element.id}>
                        <td>{element.titre}</td>
                        <td>{element.module_id}</td>
                        <td>
                          <Button color="primary" size="sm">
                            Modifier
                          </Button>{" "}
                          <Button color="danger" size="sm">
                            Supprimer
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

        {/* Create Filière Modal */}
        <Modal isOpen={modalCreateFiliere} toggle={toggleCreateFiliere}>
          <ModalHeader toggle={toggleCreateFiliere}>Ajouter une Filière</ModalHeader>
          <ModalBody>
            <Form>
              <FormGroup>
                <Label>Alias</Label>
                <Input
                  type="text"
                  name="alias"
                  value={newFiliere.alias}
                  onChange={handleFiliereInputChange}
                  placeholder="Alias"
                />
              </FormGroup>
              <FormGroup>
                <Label>Intitulé</Label>
                <Input
                  type="text"
                  name="intitule"
                  value={newFiliere.intitule}
                  onChange={handleFiliereInputChange}
                  placeholder="Intitulé"
                />
              </FormGroup>
              <FormGroup>
                <Label>Accréditation</Label>
                <Input
                  type="text"
                  name="anneeAccreditation"
                  value={newFiliere.anneeAccreditation}
                  onChange={handleFiliereInputChange}
                  placeholder="Accréditation"
                />
              </FormGroup>
              <FormGroup>
                <Label>Fin Accréditation</Label>
                <Input
                  type="text"
                  name="anneeFinAccreditation"
                  value={newFiliere.anneeFinAccreditation}
                  onChange={handleFiliereInputChange}
                  placeholder="Fin Accréditation"
                />
              </FormGroup>
              <FormGroup>
                <Label>Coordonnateur</Label>
                <Input
                  type="text"
                  name="coordonnateur"
                  value={newFiliere.coordonnateur}
                  onChange={handleFiliereInputChange}
                  placeholder="Coordonnateur"
                />
              </FormGroup>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={handleCreateFiliere}>
              Sauvegarder
            </Button>{" "}
            <Button color="secondary" onClick={toggleCreateFiliere}>
              Annuler
            </Button>
          </ModalFooter>
        </Modal>
        {/* Create Niveau Modal */}
        <Modal isOpen={modalCreateNiveau} toggle={toggleCreateNiveau}>
          <ModalHeader toggle={toggleCreateNiveau}>Ajouter un Niveau</ModalHeader>
          <ModalBody>
            <Form>
              <FormGroup>
                <Label>Alias</Label>
                <Input
                  type="text"
                  name="alias"
                  value={newNiveau.alias}
                  onChange={handleNiveauInputChange}
                  placeholder="Alias"
                />
              </FormGroup>
              <FormGroup>
                <Label>Niveau</Label>
                <Input
                  type="text"
                  name="niveau"
                  value={newNiveau.niveau}
                  onChange={handleNiveauInputChange}
                  placeholder="Niveau"
                />
              </FormGroup>
              <FormGroup>
                <Label>Filière</Label>
                <Input
                  type="select"
                  name="filiere_id"
                  value={newNiveau.filiere_id}
                  onChange={handleNiveauInputChange}
                >
                  <option value="">Sélectionner une filière</option>
                  {filieres.map((filiere) => (
                    <option key={filiere.id} value={filiere.id}>
                      {filiere.alias}
                    </option>
                  ))}
                </Input>
              </FormGroup>
              <FormGroup check>
                <Label check>
                  <Input
                    type="checkbox"
                    name="is_niveau_suivant"
                    checked={newNiveau.is_niveau_suivant}
                    onChange={handleNiveauInputChange}
                  />{" "}
                  Niveau Suivant
                </Label>
              </FormGroup>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={handleCreateNiveau}>
              Sauvegarder
            </Button>{" "}
            <Button color="secondary" onClick={toggleCreateNiveau}>
              Annuler
            </Button>
          </ModalFooter>
        </Modal>

        {/* Create Module Modal */}
        <Modal isOpen={modalCreateModule} toggle={toggleCreateModule}>
          <ModalHeader toggle={toggleCreateModule}>Ajouter un Module</ModalHeader>
          <ModalBody>
            <Form>
              <FormGroup>
                <Label>Titre</Label>
                <Input
                  type="text"
                  name="titre"
                  value={newModule.titre}
                  onChange={handleModuleInputChange}
                  placeholder="Titre"
                />
              </FormGroup>
              <FormGroup>
                <Label>Code</Label>
                <Input
                  type="text"
                  name="code"
                  value={newModule.code}
                  onChange={handleModuleInputChange}
                  placeholder="Code"
                />
              </FormGroup>
              <FormGroup>
                <Label>Niveau</Label>
                <Input
                  type="select"
                  name="niveau_id"
                  value={newNiveau.niveau_id}
                  onChange={handleModuleInputChange}
                >
                  <option value="">Sélectionner un Niveau</option>
                  {niveaux.map((niveau) => (
                    <option key={niveau.id} value={niveau.id}>
                      {niveau.niveau}
                    </option>
                  ))}
                </Input>
              </FormGroup>
              <FormGroup>
                <Label>Filière</Label>
                <Input
                  type="select"
                  name="filiere_id"
                  value={newModule.filiere_id}
                  onChange={handleModuleInputChange}
                >
                  <option value="">Sélectionner une filière</option>
                  {filieres.map((filiere) => (
                    <option key={filiere.id} value={filiere.id}>
                      {filiere.alias}
                    </option>
                  ))}
                </Input>
              </FormGroup>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={handleCreateModule}>
              Sauvegarder
            </Button>{" "}
            <Button color="secondary" onClick={toggleCreateModule}>
              Annuler
            </Button>
          </ModalFooter>
        </Modal>

        {/* Create Element Modal */}
        <Modal isOpen={modalCreateElement} toggle={toggleCreateElement}>
          <ModalHeader toggle={toggleCreateElement}>Ajouter un Élément</ModalHeader>
          <ModalBody>
            <Form>
              <FormGroup>
                <Label>Titre</Label>
                <Input
                  type="text"
                  name="titre"
                  value={newElement.titre}
                  onChange={handleElementInputChange}
                  placeholder="Titre"
                />
              </FormGroup>
              <FormGroup>
                <Label>Module</Label>
                <Input
                  type="select"
                  name="module_id"
                  value={newElement.module_id}
                  onChange={handleElementInputChange}
                >
                  <option value="">Sélectionner un module</option>
                  {modules.map((module) => (
                    <option key={module.id} value={module.id}>
                      {module.titre}
                    </option>
                  ))}
                </Input>
              </FormGroup>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={handleCreateElement}>
              Sauvegarder
            </Button>{" "}
            <Button color="secondary" onClick={toggleCreateElement}>
              Annuler
            </Button>
          </ModalFooter>
        </Modal>

        {/* Assign Module Modal */}
        <Modal isOpen={modalAssignModule} toggle={toggleAssignModule}>
          <ModalHeader toggle={toggleAssignModule}>Associer un Module à une Classe</ModalHeader>
          <ModalBody>
            <Form>
              <FormGroup>
                <Label>Filière</Label>
                <Input
                  type="select"
                  name="filiere_id"
                  value={selectedFiliere}
                  onChange={(e) => setSelectedFiliere(e.target.value)}
                >
                  <option value="">Sélectionner une filière</option>
                  {filieres.map((filiere) => (
                    <option key={filiere.id} value={filiere.id}>
                      {filiere.alias}
                    </option>
                  ))}
                </Input>
              </FormGroup>
              <FormGroup>
                <Label>Module</Label>
                <Input
                  type="select"
                  name="module_id"
                  value={selectedModule}
                  onChange={(e) => setSelectedModule(e.target.value)}
                >
                  <option value="">Sélectionner un module</option>
                  {modules.map((module) => (
                    <option key={module.id} value={module.id}>
                      {module.titre}
                    </option>
                  ))}
                </Input>
              </FormGroup>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button
              color="primary"
              onClick={() => handleAssignModule(selectedFiliere, selectedModule)}
            >
              Associer
            </Button>{" "}
            <Button color="secondary" onClick={toggleAssignModule}>
              Annuler
            </Button>
          </ModalFooter>
        </Modal>

        {/* Assign Element Modal */}
        <Modal isOpen={modalAssignElement} toggle={toggleAssignElement}>
          <ModalHeader toggle={toggleAssignElement}>Associer un Élément à un Module</ModalHeader>
          <ModalBody>
            <Form>
              <FormGroup>
                <Label>Module</Label>
                <Input
                  type="select"
                  name="module_id"
                  value={selectedModule}
                  onChange={(e) => setSelectedModule(e.target.value)}
                >
                  <option value="">Sélectionner un module</option>
                  {modules.map((module) => (
                    <option key={module.id} value={module.id}>
                      {module.titre}
                    </option>
                  ))}
                </Input>
              </FormGroup>
              <FormGroup>
                <Label>Élément</Label>
                <Input
                  type="select"
                  name="element_id"
                  value={newElement.module_id}
                  onChange={handleElementInputChange}
                >
                  <option value="">Sélectionner un élément</option>
                  {elements.map((element) => (
                    <option key={element.id} value={element.id}>
                      {element.titre}
                    </option>
                  ))}
                </Input>
              </FormGroup>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button
              color="primary"
              onClick={() => handleAssignElement(selectedModule, newElement.module_id)}
            >
              Associer
            </Button>{" "}
            <Button color="secondary" onClick={toggleAssignElement}>
              Annuler
            </Button>
          </ModalFooter>
        </Modal>
      </Container>
    </>
  );
};

export default PedagogicalStructure;