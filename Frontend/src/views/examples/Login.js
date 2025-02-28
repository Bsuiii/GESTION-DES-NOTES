import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  Button,
  Card,
  CardHeader,
  CardBody,
  FormGroup,
  Form,
  Input,
  InputGroupAddon,
  InputGroupText,
  InputGroup,
  Row,
  Col,
} from "reactstrap";
import authService from "../../services/authService"; // Adjust the path to your authService

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      // Call the login function from authService
      const userDetails = await authService.login(email, password);

      // Redirect to a protected route (e.g., /dashboard)
      navigate("/dashboard");
    } catch (err) {
      setError("Invalid email or password"); // Display error message
      console.error("Login failed:", err);
    }
  };

  return (
    <>
      <Col lg="12" md="20" style={{ marginTop: "130px" }}>
        {/* Main Card */}
        <Card
          style={{
            border: "none",
            borderRadius: "20px",
            boxShadow: "0 8px 32px rgba(0, 0, 0, 0.2)",
            background: "rgba(255, 255, 255, 0.9)",
            backdropFilter: "blur(10px)",
            overflow: "hidden",
          }}
        >
          {/* Card Header */}
          <CardHeader
            style={{
              backgroundColor: "transparent",
              borderBottom: "none",
              padding: "2rem 1rem 1rem",
              textAlign: "center",
            }}
          >
            <h1
              style={{
                fontSize: "2.5rem",
                fontWeight: "bold",
                color: "#4a90e2",
                marginBottom: "0.5rem",
              }}
            >
              Welcome Back!
            </h1>
            <p style={{ color: "#6c757d", fontSize: "0.9rem" }}>
              Sign in to continue to your account.
            </p>
          </CardHeader>

          {/* Card Body */}
          <CardBody style={{ padding: "2rem" }}>
            {/* Form */}
            <Form role="form" onSubmit={handleLogin}>
              {/* Email Field */}
              <FormGroup style={{ marginBottom: "1.5rem" }}>
                <InputGroup
                  style={{
                    borderRadius: "10px",
                    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
                  }}
                >
                  <InputGroupAddon addonType="prepend">
                    <InputGroupText
                      style={{
                        backgroundColor: "#ffffff",
                        border: "none",
                        padding: "0.75rem",
                      }}
                    >
                      <i
                        className="ni ni-single-02"
                        style={{ color: "#4a90e2" }}
                      />
                    </InputGroupText>
                  </InputGroupAddon>
                  <Input
                    placeholder="Email"
                    type="text" // Use type="text" instead of type="email"
                    autoComplete="username"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    style={{
                      border: "none",
                      borderRadius: "10px",
                      padding: "0.75rem",
                    }}
                  />
                </InputGroup>
              </FormGroup>

              {/* Password Field */}
              <FormGroup style={{ marginBottom: "1.5rem" }}>
                <InputGroup
                  style={{
                    borderRadius: "10px",
                    boxShadow: "0 2px 4px rgba(0, 0, 0, 0.1)",
                  }}
                >
                  <InputGroupAddon addonType="prepend">
                    <InputGroupText
                      style={{
                        backgroundColor: "#ffffff",
                        border: "none",
                        padding: "0.75rem",
                      }}
                    >
                      <i
                        className="ni ni-lock-circle-open"
                        style={{ color: "#4a90e2" }}
                      />
                    </InputGroupText>
                  </InputGroupAddon>
                  <Input
                    placeholder="Password"
                    type="password"
                    autoComplete="current-password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    style={{
                      border: "none",
                      borderRadius: "10px",
                      padding: "0.75rem",
                    }}
                  />
                </InputGroup>
              </FormGroup>

              {/* Error Message */}
              {error && (
                <div
                  style={{
                    color: "red",
                    textAlign: "center",
                    marginBottom: "1rem",
                  }}
                >
                  {error}
                </div>
              )}

              {/* Remember Me Checkbox */}
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  marginBottom: "1.5rem",
                }}
              >
                <input
                  type="checkbox"
                  id="customCheckLogin"
                  style={{ marginRight: "0.5rem" }}
                />
                <label
                  htmlFor="customCheckLogin"
                  style={{ color: "#6c757d", fontSize: "0.875rem" }}
                >
                  Remember me
                </label>
              </div>

              {/* Login Button */}
              <div style={{ textAlign: "center" }}>
                <Button
                  style={{
                    width: "100%",
                    backgroundColor: "#4a90e2",
                    border: "none",
                    borderRadius: "10px",
                    padding: "0.75rem",
                    fontSize: "1rem",
                    fontWeight: "bold",
                    color: "#ffffff",
                    boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
                    transition: "all 0.3s ease",
                  }}
                  type="submit"
                  onMouseEnter={(e) => {
                    e.target.style.backgroundColor = "#357ABD";
                    e.target.style.transform = "translateY(-2px)";
                  }}
                  onMouseLeave={(e) => {
                    e.target.style.backgroundColor = "#4a90e2";
                    e.target.style.transform = "translateY(0)";
                  }}
                >
                  Sign in
                </Button>
              </div>
            </Form>
          </CardBody>

          {/* Link to Register Page */}
          <Row style={{ marginTop: "1rem", marginBottom: "2rem" }}>
            <Col
              style={{
                textAlign: "center",
                display: "flex",
                justifyContent: "center",
              }}
              xs="12"
            >
              <Link
                to="/auth/register"
                style={{
                  color: "#4a90e2",
                  textDecoration: "none",
                  fontSize: "0.875rem",
                  fontWeight: "500",
                }}
              >
                <small>Don't have an account? Create one</small>
              </Link>
            </Col>
          </Row>
        </Card>
      </Col>
    </>
  );
};

export default Login;