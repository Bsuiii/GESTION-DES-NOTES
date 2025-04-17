import React, { useEffect, useState } from "react";
import {
  Card,
  CardHeader,
  Table,
  Row,
  Button,
  Container,
  Input,
  Modal,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Form,
  FormGroup,
  Label,
} from "reactstrap";
import { FaEdit, FaTrash } from "react-icons/fa";
import accountService from "services/accountServices"; // Import the service
import Header from "components/Headers/Header.js";

// Password generator function
const generatePassword = () => {
  const length = 10;
  const charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  let password = "";
  for (let i = 0; i < length; i++) {
    const randomIndex = Math.floor(Math.random() * charset.length);
    password += charset[randomIndex];
  }
  return password;
};

const AccountsList = () => {
  const [accounts, setAccounts] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [accountToDelete, setAccountToDelete] = useState(null);

  // Fetch accounts on component mount
  useEffect(() => {
    const getAccounts = async () => {
      try {
        const data = await accountService.fetchAccounts(); // Fetch accounts
        setAccounts(data);
      } catch (error) {
        console.error("Error fetching accounts:", error);
        alert("Failed to fetch accounts. Please try again.");
      }
    };

    getAccounts();
  }, []);

  // Handle search
  const handleSearch = (e) => {
    setSearchQuery(e.target.value);
  };

  // Filter accounts based on search query
  const filteredAccounts = accounts.filter((account) =>
    account.login.toLowerCase().includes(searchQuery.toLowerCase())
  );

  // Handle edit button click
  const handleEditClick = (account) => {
    setSelectedAccount(account);
    setShowEditModal(true);
  };

  // Handle delete confirmation
  const handleDeleteConfirmation = (account) => {
    setAccountToDelete(account);
    setShowDeleteModal(true);
  };

  // Handle delete account
  const handleDeleteAccount = async () => {
    try {
      await accountService.deleteAccount(accountToDelete.id);
      setAccounts(accounts.filter((acc) => acc.id !== accountToDelete.id)); // Remove the deleted account from the list
      setShowDeleteModal(false);
      alert("Account deleted successfully!");
    } catch (error) {
      console.error("Error deleting account:", error);
      alert("Failed to delete account. Please try again.");
    }
  };

  // Handle role change
  const handleRoleChange = (e) => {
    setSelectedAccount({ ...selectedAccount, roleId: e.target.value });
  };

  // Handle status change
  const handleStatusChange = (e) => {
    setSelectedAccount({ ...selectedAccount, _active: e.target.value === "active" });
  };

  // Handle reset password
  const handleResetPassword = async () => {
    try {
      const newPassword = generatePassword(); // Generate a new password
      await accountService.updateAccount(selectedAccount.id, { password: newPassword });
      alert(`Password reset successfully! New password: ${newPassword}`);
    } catch (error) {
      console.error("Error resetting password:", error);
      alert("Failed to reset password. Please try again.");
    }
  };

  // Handle toggle account status
  const handleToggleAccountStatus = async () => {
    try {
      const updatedAccount = { ...selectedAccount, _active: !selectedAccount._active };
      await accountService.updateAccount(selectedAccount.id, updatedAccount);
      setAccounts(accounts.map((acc) => (acc.id === selectedAccount.id ? updatedAccount : acc)));
      alert(`Account ${updatedAccount._active ? "enabled" : "disabled"} successfully!`);
    } catch (error) {
      console.error("Error toggling account status:", error);
      alert("Failed to toggle account status. Please try again.");
    }
  };

  // Handle save changes
  const handleSaveChanges = async () => {
    try {
      await accountService.updateAccount(selectedAccount.id, selectedAccount);
      setAccounts(accounts.map((acc) => (acc.id === selectedAccount.id ? selectedAccount : acc)));
      setShowEditModal(false);
      alert("Changes saved successfully!");
    } catch (error) {
      console.error("Error saving changes:", error);
      alert("Failed to save changes. Please try again.");
    }
  };

  return (
    <>
      <Header />
    <Container className="mt--7" fluid>
      <Row>
        <div className="col">
          <Card className="shadow">
            <CardHeader className="border-0 d-flex justify-content-between align-items-center">
              <h3 className="mb-0">Accounts List</h3>
              <div className="d-flex align-items-center">
                <Input
                  type="text"
                  placeholder="Search by Login"
                  value={searchQuery}
                  onChange={handleSearch}
                  className="mr-2"
                  style={{ width: "auto" }}
                />
              </div>
            </CardHeader>
            <Table className="align-items-center table-flush" responsive>
              <thead className="thead-light">
                <tr>
                  <th scope="col">ID</th>
                  <th scope="col">Login</th>
                  <th scope="col">Password</th>
                  <th scope="col">User ID</th>
                  <th scope="col">Role ID</th>
                  <th scope="col">Active</th>
                  <th scope="col">Locked</th>
                  <th scope="col">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredAccounts.map((account) => (
                  <tr key={account.id}>
                    <td>{account.id}</td>
                    <td>{account.login}</td>
                    <td>{account.password}</td>
                    <td>{account.userId}</td>
                    <td>{account.roleId}</td>
                    <td>{account._active ? "Yes" : "No"}</td>
                    <td>{account._locked ? "Yes" : "No"}</td>
                    <td>
                      <Button color="info" onClick={() => handleEditClick(account)}>
                        <FaEdit />
                      </Button>
                      <Button
                        color="danger"
                        onClick={() => handleDeleteConfirmation(account)}
                        className="ml-2"
                      >
                        <FaTrash />
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </Card>

          {/* Edit Modal */}
          <Modal isOpen={showEditModal} toggle={() => setShowEditModal(false)}>
            <ModalHeader toggle={() => setShowEditModal(false)}>Edit User Account</ModalHeader>
            <ModalBody>
              <Form>
                <FormGroup>
                  <Label>Role</Label>
                  <Input
                    type="select"
                    value={selectedAccount?.roleId || ""}
                    onChange={handleRoleChange}
                  >
                    <option value={1}>ADMIN_SP</option>
                    <option value={2}>ADMIN_NOTES</option>
                    <option value={3}>USER</option>
                  </Input>
                </FormGroup>
                <FormGroup>
                  <Label>Account Status</Label>
                  <Input
                    type="select"
                    value={selectedAccount?._active ? "active" : "disabled"}
                    onChange={handleStatusChange}
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
                  onClick={handleToggleAccountStatus}
                  className="ml-2"
                >
                  {selectedAccount?._active ? "Disable Account" : "Enable Account"}
                </Button>
              </Form>
            </ModalBody>
            <ModalFooter>
              <Button color="primary" onClick={handleSaveChanges}>
                Save Changes
              </Button>
              <Button color="secondary" onClick={() => setShowEditModal(false)}>
                Cancel
              </Button>
            </ModalFooter>
          </Modal>

          {/* Delete Confirmation Modal */}
          <Modal isOpen={showDeleteModal} toggle={() => setShowDeleteModal(false)}>
            <ModalHeader toggle={() => setShowDeleteModal(false)}>Delete Account</ModalHeader>
            <ModalBody>
              Are you sure you want to delete the account for <strong>{accountToDelete?.login}</strong>?
            </ModalBody>
            <ModalFooter>
              <Button color="danger" onClick={handleDeleteAccount}>
                Delete
              </Button>
              <Button color="secondary" onClick={() => setShowDeleteModal(false)}>
                Cancel
              </Button>
            </ModalFooter>
          </Modal>
        </div>
      </Row>
      </Container>
    </>
  );
};

export default AccountsList;