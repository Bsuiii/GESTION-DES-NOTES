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
import { fetchElements, createElement, deleteElement, fetchModules } from "services/pedagogicalService";

const Elements = () => {
  const [elements, setElements] = useState([]);
  const [modules, setModules] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    titre: "",
    module_id: "",
  });

  useEffect(() => {
    const loadData = async () => {
      try {
        const [elementsData, modulesData] = await Promise.all([
          fetchElements(),
          fetchModules()
        ]);
        setElements(elementsData);
        setModules(modulesData);
      } catch (error) {
        console.error("Failed to fetch data:", error);
      }
    };
    loadData();
  }, []);

  const toggleModal = () => setModalOpen(!modalOpen);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async () => {
    try {
      const created = await createElement(formData);
      setElements([...elements, created]);
      setFormData({
        titre: "",
        module_id: "",
      });
      toggleModal();
    } catch (error) {
      console.error("Failed to create element:", error);
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteElement(id);
      setElements(elements.filter(e => e.id !== id));
    } catch (error) {
      console.error("Failed to delete element:", error);
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
                <h3 className="mb-0">Gestion des Éléments</h3>
              </CardHeader>
              <CardBody>
                <div className="d-flex justify-content-end mb-3">
                  <Button color="success" onClick={toggleModal}>
                    Ajouter un Élément
                  </Button>
                </div>
                <Table className="align-items-center table-flush" responsive>
                  <thead className="thead-light">
                    <tr>
                      <th>Titre</th>
                      <th>Module</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {elements.map((element) => (
                      <tr key={element.id}>
                        <td>{element.titre}</td>
                        <td>{modules.find(m => m.id === element.module_id)?.titre || element.module_id}</td>
                        <td>
                          <Button color="primary" size="sm">
                            Modifier
                          </Button>{" "}
                          <Button color="danger" size="sm" onClick={() => handleDelete(element.id)}>
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
          <ModalHeader toggle={toggleModal}>Ajouter un Élément</ModalHeader>
          <ModalBody>
            <Form>
              <FormGroup>
                <Label>Titre</Label>
                <Input
                  type="text"
                  name="titre"
                  value={formData.titre}
                  onChange={handleInputChange}
                  placeholder="Titre"
                />
              </FormGroup>
              <FormGroup>
                <Label>Module</Label>
                <Input
                  type="select"
                  name="module_id"
                  value={formData.module_id}
                  onChange={handleInputChange}
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

export default Elements;