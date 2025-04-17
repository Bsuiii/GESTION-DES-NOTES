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
import { fetchFilieres, createFiliere, updateFiliere, deleteFiliere } from "services/pedagogicalService";

const Filieres = () => {
  const [filieres, setFilieres] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingFiliere, setEditingFiliere] = useState(null);
  const [formData, setFormData] = useState({
    alias: "",
    intitule: "",
    anneeAccreditation: "",
    anneeFinAccreditation: "",
    coordonnateur_id: "",
  });

  useEffect(() => {
    const loadData = async () => {
      try {
        const data = await fetchFilieres();
        setFilieres(data);
      } catch (error) {
        console.error("Failed to fetch filieres:", error);
      }
    };
    loadData();
  }, []);

  const toggleModal = () => {
    setModalOpen(!modalOpen);
    if (!modalOpen) {
      setEditingFiliere(null);
      setFormData({
        alias: "",
        intitule: "",
        anneeAccreditation: "",
        anneeFinAccreditation: "",
        coordonnateur_id: "",
      });
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    if (editingFiliere) {
      setEditingFiliere({ ...editingFiliere, [name]: value });
    } else {
      setFormData({ ...formData, [name]: value });
    }
  };

  const handleEdit = (filiere) => {
    setEditingFiliere(filiere);
    setModalOpen(true);
  };

  const handleSubmit = async () => {
    try {
      if (editingFiliere) {
        const updated = await updateFiliere(editingFiliere.id, editingFiliere);
        setFilieres(filieres.map(f => f.id === updated.id ? updated : f));
      } else {
        const created = await createFiliere(formData);
        setFilieres([...filieres, created]);
      }
      toggleModal();
    } catch (error) {
      console.error("Failed to save filiere:", error);
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteFiliere(id);
      setFilieres(filieres.filter(f => f.id !== id));
    } catch (error) {
      console.error("Failed to delete filiere:", error);
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
                <h3 className="mb-0">Gestion des Filières</h3>
              </CardHeader>
              <CardBody>
                <div className="d-flex justify-content-end mb-3">
                  <Button color="success" onClick={toggleModal}>
                    Ajouter une Filière
                  </Button>
                </div>
                <Table className="align-items-center table-flush" responsive>
                  <thead className="thead-light">
                    <tr>
                      <th>Alias</th>
                      <th>Intitulé</th>
                      <th>Accréditation</th>
                      <th>Fin Accréditation</th>
                      <th>Coordonnateur</th>
                      <th>Actions</th>
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
                          <Button color="primary" size="sm" onClick={() => handleEdit(filiere)}>
                            Modifier
                          </Button>{" "}
                          <Button color="danger" size="sm" onClick={() => handleDelete(filiere.id)}>
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
          <ModalHeader toggle={toggleModal}>
            {editingFiliere ? "Modifier Filière" : "Ajouter une Filière"}
          </ModalHeader>
          <ModalBody>
            <Form>
              <FormGroup>
                <Label>Alias</Label>
                <Input
                  type="text"
                  name="alias"
                  value={editingFiliere ? editingFiliere.alias : formData.alias}
                  onChange={handleInputChange}
                  placeholder="Alias"
                />
              </FormGroup>
              <FormGroup>
                <Label>Intitulé</Label>
                <Input
                  type="text"
                  name="intitule"
                  value={editingFiliere ? editingFiliere.intitule : formData.intitule}
                  onChange={handleInputChange}
                  placeholder="Intitulé"
                />
              </FormGroup>
              <FormGroup>
                <Label>Accréditation</Label>
                <Input
                  type="text"
                  name="anneeAccreditation"
                  value={editingFiliere ? editingFiliere.anneeAccreditation : formData.anneeAccreditation}
                  onChange={handleInputChange}
                  placeholder="Accréditation"
                />
              </FormGroup>
              <FormGroup>
                <Label>Fin Accréditation</Label>
                <Input
                  type="text"
                  name="anneeFinAccreditation"
                  value={editingFiliere ? editingFiliere.anneeFinAccreditation : formData.anneeFinAccreditation}
                  onChange={handleInputChange}
                  placeholder="Fin Accréditation"
                />
              </FormGroup>
              <FormGroup>
                <Label>Coordonnateur</Label>
                <Input
                  type="text"
                  name="coordonnateur_id"
                  value={editingFiliere ? editingFiliere.coordonnateur_id : formData.coordonnateur_id}
                  onChange={handleInputChange}
                  placeholder="Coordonnateur"
                />
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

export default Filieres;