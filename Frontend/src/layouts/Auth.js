import React from "react";
import { Route, Routes, Navigate } from "react-router-dom";
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

const Auth = () => {
  return (
    <div style={{ minHeight: "100vh" }}>
      <AuthNavbar />
      <Routes>
        {getRoutes(routes)}
        <Route path="*" element={<Navigate to="/auth/login" replace />} />
      </Routes>
      <AuthFooter />
    </div>
  );
};

export default Auth;