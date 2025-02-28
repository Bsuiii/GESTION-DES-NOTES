import React from "react";
import { useLocation, Route, Routes, Navigate } from "react-router-dom";
// reactstrap components
import { Container, Row, Col } from "reactstrap";

// core components
import AuthNavbar from "components/Navbars/AuthNavbar.js";
import AuthFooter from "components/Footers/AuthFooter.js";

import routes from "routes.js";


  const getRoutes = (routes) => {
    return routes.map((prop, key) => {
      if (prop.layout === "/auth") {
        return (
          <Route path={prop.path} element={prop.component} key={key} exact />
        );
      } else {
        return null;
      }
    });
  };

const auth = () => {
  return (
    <div
      className="bg-gradient-info" // Apply the gradient background
      style={{
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
      }}
    >
      {/* Navbar */}
      <AuthNavbar />

      {/* Main Content */}
      <div
        style={{
          flex: 1,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          padding: "20px",
        }}
      >
        <Col lg="5" md="7" style={{ maxWidth: "500px", width: "100%" }}>
          {/* Carte principale */}
          <Container className="mt--8 pb-5">
          <Row className="justify-content-center">
            <Routes>
              {getRoutes(routes)}
              <Route path="*" element={<Navigate to="/auth/login" replace />} />
            </Routes>
          </Row>
        </Container>
        </Col>
      </div>

      {/* Footer */}
      <AuthFooter />
    </div>
  );
};

export default auth;