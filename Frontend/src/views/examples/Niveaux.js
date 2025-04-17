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
  Col
} from "reactstrap";
import Header from "components/Headers/Header.js";
import { fetchNiveaux, createNiveau, deleteNiveau, fetchFilieres } from "services/pedagogicalService";

const Niveaux = () => {
  const [niveaux, setNiveaux] = useState([]);
  const [filieres, setFilieres] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    alias: "",
    niveau: "",
    filiere_id: "",
    is_niveau_suivant: false,
  });

  useEffect(() => {
    const loadData = async () => {
      try {
        const [niveauxData, filieresData] = await Promise.all([
          fetchNiveaux(),
          fetchFilieres()
        ]);
        setNiveaux(niveauxData);
        setFilieres(filieresData);
      } catch (error) {
        console.error("Failed to fetch data:", error);
      }
    };
    loadData();
  }, []);

  const toggleModal = () => setModalOpen(!modalOpen);

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === "checkbox" ? checked : value,
    });
  };

  const handleSubmit = async () => {
    try {
      const created = await createNiveau(formData);
      setNiveaux([...niveaux, created]);
      setFormData({
        alias: "",
        niveau: "",
        filiere_id: "",
        is_niveau_suivant: false,
      });
      toggleModal();
    } catch (error) {
      console.error("Failed to create niveau:", error);
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteNiveau(id);
      setNiveaux(niveaux.filter(n => n.id !== id));
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
                <h3 className="mb-0">Gestion des Niveaux</h3>
              </CardHeader>
              <CardBody>
                <div className="d-flex justify-content-end mb-3">
                  <Button color="success" onClick={toggleModal}>
                    Ajouter un Niveau
                  </Button>
                </div>
                <Table className="align-items-center table-flush" responsive>
                  <thead className="thead-light">
                    <tr>
                      <th>Alias</th>
                      <th>Niveau</th>
                      <th>Filière</th>
                      <th>Niveau Suivant</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {niveaux.map((niveau) => (
                      <tr key={niveau.id}>
                        <td>{niveau.alias}</td>
                        <td>{niveau.niveau}</td>
                        <td>{filieres.find(f => f.id === niveau.filiere_id)?.alias || niveau.filiere_id}</td>
                        <td>{niveau.is_niveau_suivant ? "Oui" : "Non"}</td>
                        <td>
                          <Button color="primary" size="sm">
                            Modifier
                          </Button>{" "}
                          <Button color="danger" size="sm" onClick={() => handleDelete(niveau.id)}>
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

        <Modal isOpen={modalOpen} toggle={toggleModal}>
          <ModalHeader toggle={toggleModal}>Ajouter un Niveau</ModalHeader>
          <ModalBody>
            <Form>
              <FormGroup>
                <Label>Alias</Label>
                <Input
                  type="text"
                  name="alias"
                  value={formData.alias}
                  onChange={handleInputChange}
                  placeholder="Alias"
                />
              </FormGroup>
              <FormGroup>
                <Label>Niveau</Label>
                <Input
                  type="text"
                  name="niveau"
                  value={formData.niveau}
                  onChange={handleInputChange}
                  placeholder="Niveau"
                />
              </FormGroup>
              <FormGroup>
                <Label>Filière</Label>
                <Input
                  type="select"
                  name="filiere_id"
                  value={formData.filiere_id}
                  onChange={handleInputChange}
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
                    checked={formData.is_niveau_suivant}
                    onChange={handleInputChange}
                  />{" "}
                  Niveau Suivant
                </Label>
              </FormGroup>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={handleSubmit}>
              Sauvegarder
            </Button>{" "}
            <Button color="secondary" onClick={toggleModal}>
              Annuler
            </Button>
          </ModalFooter>
        </Modal>
      </Container>
    </>
  );
};

export default Niveaux;