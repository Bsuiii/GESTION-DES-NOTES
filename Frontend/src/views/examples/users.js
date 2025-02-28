import React, { useState, useEffect } from "react";
import {
  Card,
  CardHeader,
  Table,
  Container,
  Row,
  Button,
  Form,
  FormGroup,
  Input,
  Label,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
} from "reactstrap";
import Header from "components/Headers/Header.js";
import { FaEdit, FaPlus } from "react-icons/fa";
import { fetchUsers, createUser } from "services/userServices";
import createAccount from "services/accountServices";

const Account = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [showUserForm, setShowUserForm] = useState(false);
  const [showPersonForm, setShowPersonForm] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedPerson, setSelectedPerson] = useState(null);
  const [selectedUser, setSelectedUser] = useState(null);
  const [newUser, setNewUser] = useState({
    role: "",
    login: "",
    password: "",
  });
  const [newPerson, setNewPerson] = useState({
    firstName: "",
    lastName: "",
    cin: "",
    email: "",
    phone: "",
  });
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch users from the backend on component mount
  useEffect(() => {
    const loadUsers = async () => {
      try {
        const usersData = await fetchUsers(); // No need to pass the token manually
        setUsers(usersData);
        setLoading(false);
      } catch (error) {
        setError(error.message);
        setLoading(false);
      }
    };
    loadUsers();
  }, []);

  // Handle search input change
  const handleSearch = (e) => {
    setSearchQuery(e.target.value);
  };

  // Filter users based on search query
  const filteredUsers = users.filter((user) => {
    const firstName = user.firstname || "";
    const lastName = user.lastname || "";
    const cin = user.cin || "";

    return (
      firstName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      lastName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      cin.toLowerCase().includes(searchQuery.toLowerCase())
    );
  });

  // Handle selecting a user for account creation
  const handleSelectPerson = (user) => {
    setSelectedPerson(user);
    setShowUserForm(true);
  };

  // Handle editing a user
  const handleEditPerson = (user) => {
    setSelectedUser(user);
    setShowEditModal(true);
  };

  // Generate a random password
  const generatePassword = () => {
    const length = 10;
    const charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    let password = "";
    for (let i = 0; i < length; i++) {
      password += charset.charAt(Math.floor(Math.random() * charset.length));
    }
    return password;
  };

  // Handle creating a new user
  const handleCreatePerson = async () => {
    try {
      const userData = {
        firstname: newPerson.firstName,
        lastname: newPerson.lastName,
        cin: newPerson.cin,
        email: newPerson.email,
        tel: newPerson.phone,
      };

      const createdUser = await createUser(userData);
      setUsers([...users, createdUser]);
      alert("User created successfully!");

      // Reset form and close modal
      setNewPerson({
        firstName: "",
        lastName: "",
        cin: "",
        email: "",
        phone: "",
      });
      setShowPersonForm(false);
    } catch (error) {
      console.error("Error creating user:", error);
      alert("Failed to create user. Please try again.");
    }
  };

  // Handle creating a new account
  const handleCreateAccount = async () => {
    try {
      if (!selectedPerson) {
        throw new Error("No user selected for account creation.");
      }
  
      const login = `${selectedPerson.firstname}${selectedPerson.lastname}`.toLowerCase();
      const password = generatePassword();
  
      const accountData = {
        login,
        password,
        role_id: newUser.role === "ADMIN_SP" ? 2 : 1,
        user_id: selectedPerson.id,
      };
  
      await createAccount(accountData);
  
      alert(`Account created successfully!\nLogin: ${login}\nPassword: ${password}`);
  
      setShowUserForm(false);
    } catch (error) {
      console.error("Error creating account:", error);
      alert("Failed to create account. Please try again.");
    }
  };
  

  // Handle resetting password
  const handleResetPassword = () => {
    const newPassword = generatePassword();
    alert(`Password reset to: ${newPassword}`);
  };

  // Handle toggling account status
  const handleToggleAccountStatus = (user) => {
    const newStatus = user.status === "active" ? "disabled" : "active";
    alert(`Account status changed to: ${newStatus}`);
    // Update the account status in the database
  };

  // Loading and error states
  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <>
      <Header />
      <Container className="mt--7" fluid>
        {/* Table Section */}
        <Row>
          <div className="col">
            <Card className="shadow">
              <CardHeader className="border-0 d-flex justify-content-between align-items-center">
                <h3 className="mb-0">Users List</h3>
                <div className="d-flex align-items-center">
                  <Input
                    type="text"
                    placeholder="Search by Name or CIN"
                    value={searchQuery}
                    onChange={handleSearch}
                    className="mr-2"
                    style={{ width: "auto" }}
                  />
                  <Button color="primary" onClick={() => setShowPersonForm(true)}>
                    <FaPlus /> {/* Plus icon */}
                  </Button>
                </div>
              </CardHeader>
              <Table className="align-items-center table-flush" responsive>
                <thead className="thead-light">
                  <tr>
                    <th scope="col">First Name</th>
                    <th scope="col">Last Name</th>
                    <th scope="col">CIN</th>
                    <th scope="col">Email</th>
                    <th scope="col">Phone</th>
                    <th scope="col">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredUsers.map((user) => (
                    <tr key={user.id}>
                      <td>{user.firstname}</td>
                      <td>{user.lastname}</td>
                      <td>{user.cin}</td>
                      <td>{user.email}</td>
                      <td>{user.tel}</td>
                      <td>
                        <Button color="success" onClick={() => handleSelectPerson(user)}>
                          Create Account
                        </Button>
                        <Button color="info" onClick={() => handleEditPerson(user)} className="ml-2">
                          <FaEdit />
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card>
          </div>
        </Row>

        {/* Modal for Creating a User */}
        <Modal isOpen={showPersonForm} toggle={() => setShowPersonForm(false)}>
          <ModalHeader toggle={() => setShowPersonForm(false)}>Create New User</ModalHeader>
          <ModalBody>
            <Form>
              <FormGroup>
                <Label>First Name</Label>
                <Input
                  type="text"
                  value={newPerson.firstName}
                  onChange={(e) => setNewPerson({ ...newPerson, firstName: e.target.value })}
                />
              </FormGroup>
              <FormGroup>
                <Label>Last Name</Label>
                <Input
                  type="text"
                  value={newPerson.lastName}
                  onChange={(e) => setNewPerson({ ...newPerson, lastName: e.target.value })}
                />
              </FormGroup>
              <FormGroup>
                <Label>CIN</Label>
                <Input
                  type="text"
                  value={newPerson.cin}
                  onChange={(e) => setNewPerson({ ...newPerson, cin: e.target.value })}
                />
              </FormGroup>
              <FormGroup>
                <Label>Email</Label>
                <Input
                  type="email"
                  value={newPerson.email}
                  onChange={(e) => setNewPerson({ ...newPerson, email: e.target.value })}
                />
              </FormGroup>
              <FormGroup>
                <Label>Phone</Label>
                <Input
                  type="text"
                  value={newPerson.phone}
                  onChange={(e) => setNewPerson({ ...newPerson, phone: e.target.value })}
                />
              </FormGroup>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={handleCreatePerson}>
              Create User
            </Button>
            <Button color="secondary" onClick={() => setShowPersonForm(false)}>
              Cancel
            </Button>
          </ModalFooter>
        </Modal>

        {/* Modal for Creating an Account */}
        <Modal isOpen={showUserForm} toggle={() => setShowUserForm(false)}>
          <ModalHeader toggle={() => setShowUserForm(false)}>Create User Account</ModalHeader>
          <ModalBody>
            <Form>
              <FormGroup>
                <Label>Role</Label>
                <Input
                  type="select"
                  value={newUser.role}
                  onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
                >
                  <option value="">Select Role</option>
                  <option value="ADMIN_SP">ADMIN_SP</option>
                  <option value="ADMIN_NOTES">ADMIN_NOTES</option>
                </Input>
              </FormGroup>
              <FormGroup>
                <Label>Generated Login</Label>
                <Input
                  type="text"
                  value={selectedPerson ? `${selectedPerson.firstname}${selectedPerson.lastname}`.toLowerCase() : ""}
                  readOnly
                />
              </FormGroup>
              <FormGroup>
                <Label>Generated Password</Label>
                <Input type="text" value={newUser.password} readOnly />
              </FormGroup>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={handleCreateAccount}>
              Create Account
            </Button>
            <Button color="secondary" onClick={() => setShowUserForm(false)}>
              Cancel
            </Button>
          </ModalFooter>
        </Modal>

        {/* Modal for Editing User Account */}
        <Modal isOpen={showEditModal} toggle={() => setShowEditModal(false)}>
          <ModalHeader toggle={() => setShowEditModal(false)}>Edit User Account</ModalHeader>
          <ModalBody>
            <Form>
              <FormGroup>
                <Label>Role</Label>
                <Input
                  type="select"
                  value={selectedUser?.role || ""}
                  onChange={(e) => setSelectedUser({ ...selectedUser, role: e.target.value })}
                >
                  <option value="ADMIN_SP">ADMIN_SP</option>
                  <option value="ADMIN_NOTES">ADMIN_NOTES</option>
                </Input>
              </FormGroup>
              <FormGroup>
                <Label>Account Status</Label>
                <Input
                  type="select"
                  value={selectedUser?.status || "active"}
                  onChange={(e) => setSelectedUser({ ...selectedUser, status: e.target.value })}
                >
                  <option value="active">Active</option>
                  <option value="disabled">Disabled</option>
                </Input>
              </FormGroup>
              <Button color="warning" onClick={handleResetPassword}>
                Reset Password
              </Button>
              <Button
                color="danger"
                onClick={() => handleToggleAccountStatus(selectedUser)}
                className="ml-2"
              >
                {selectedUser?.status === "active" ? "Disable Account" : "Enable Account"}
              </Button>
            </Form>
          </ModalBody>
          <ModalFooter>
            <Button color="primary" onClick={() => setShowEditModal(false)}>
              Save Changes
            </Button>
            <Button color="secondary" onClick={() => setShowEditModal(false)}>
              Cancel
            </Button>
          </ModalFooter>
        </Modal>
      </Container>
    </>
  );
};

export default Account;