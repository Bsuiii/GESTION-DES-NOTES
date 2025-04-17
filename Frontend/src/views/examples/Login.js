import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { 
  Button, 
  Card, 
  CardBody, 
  FormGroup, 
  Form, 
  Input, 
  Row, 
  Col,
  Container 
} from "reactstrap";
import authService from "../../services/authService";
import loginImage from "../../assets/img/theme/gestion-de-notes-de-frais-avec-le-logiciel-ebrigade-scaled.jpg";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      await authService.login(email, password);
      navigate("/dashboard");
    } catch (err) {
      setError("Invalid email or password");
    }
  };

  return (
    <div style={{
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      backgroundColor: "#f8f9fa",
      padding: "20px 0",
      minHeight: "calc(100vh - 56px)"
    }}>
      <Container className="py-0">
        <Row className="justify-content-center">
          <Col md="11" className="p-0">
            <Card style={{
              display: "flex",
              flexDirection: "row",
              border: "none",
              overflow: "hidden",
              boxShadow: "0 4px 20px rgba(0, 0, 0, 0.1)"
            }}>
              {/* Image Section */}
              <div className="d-none d-md-block" style={{
                flex: 1,
                backgroundImage: `url(${loginImage})`,
                backgroundSize: "cover",
                backgroundPosition: "center",
                minHeight: "500px",
                display: "flex",
                alignItems: "flex-end"
              }}>
                <div style={{
                  padding: "2rem",
                  color: "white",
                  background: "linear-gradient(transparent, rgba(0,0,0,0.7))",
                  width: "100%",
                  height:"100%"
                }}>
                  <h3>Grade Management System</h3>
                  <p>Access your professional workspace</p>
                </div>
              </div>

              {/* Login Form Section */}
              <div style={{
                flex: 1,
                padding: "2rem",
                minHeight: "500px",
                display: "flex",
                flexDirection: "column",
                justifyContent: "center"
              }}>
                <CardBody className="p-0">
                  {/* University Logo */}
                  <div className="text-center mb-4">
                    <img
                      src={require("../../assets/img/brand/université-Ibn-Tofail-de-Kénitra.png")}
                      alt="University Logo"
                      style={{
                        maxHeight: "80px",
                        width: "auto",
                        marginBottom: "1rem"
                      }}
                    />
                  </div>
                  <Form onSubmit={handleLogin}>
                    <FormGroup>
                      <label htmlFor="email" className="form-label">Email or phone</label>
                      <Input
                        id="email"
                        type="text"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        style={{ borderRadius: "8px", padding: "12px" }}
                      />
                    </FormGroup>

                    <FormGroup>
                      <div className="d-flex justify-content-between">
                        <label htmlFor="password" className="form-label">Password</label>
                        <Link to="/auth/forgot-password" style={{ fontSize: "0.875rem" }}>
                          Forgot password?
                        </Link>
                      </div>
                      <Input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        style={{ borderRadius: "8px", padding: "12px" }}
                      />
                    </FormGroup>

                    {error && <div className="text-danger text-center mb-3">{error}</div>}

                    <Button
                      color="primary"
                      block
                      style={{
                        borderRadius: "8px",
                        padding: "12px",
                        fontWeight: 600,
                        margin: "1.5rem 0"
                      }}
                      type="submit"
                    >
                      Sign In
                    </Button>
                  </Form>

                  <div className="text-center text-muted my-3">OR</div>

                  <div className="text-center">
                    <span className="text-muted">Don't have an account? </span>
                    <Link to="/auth/register" style={{ fontWeight: 600 }}>Register</Link>
                  </div>
                </CardBody>
              </div>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default Login;