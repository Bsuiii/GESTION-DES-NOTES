/*!

=========================================================
* Argon Dashboard React - v1.2.4
=========================================================

* Product Page: https://www.creative-tim.com/product/argon-dashboard-react
* Copyright 2024 Creative Tim (https://www.creative-tim.com)
* Licensed under MIT (https://github.com/creativetimofficial/argon-dashboard-react/blob/master/LICENSE.md)

* Coded by Creative Tim

=========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

*/
import Index from "views/Index.js";
import Profile from "views/examples/Profile.js";
import Login from "views/examples/Login.js";
import PedagogicalStructure from "views/examples/PedagoicalStructure";
import User from "views/examples/users.js";
import Accounts from "views/examples/accounts.js";
import Student from "views/examples/student.js";

var routes = [
  {
    path: "/index",
    name: "Dashboard",
    icon: "ni ni-tv-2 text-primary",
    component: <Index />, 
    layout: "/admin",
  },
  {
    path: "/user-profile",
    name: "User Profile",
    icon: "ni ni-single-02 text-yellow",
    component: <Profile />, 
    layout: "/admin",
  },
  {
    path: "/student",
    name: "Student",
    icon: "ni ni-hat-3 text-green",
    component: <Student />, 
    layout: "/admin",
  },
  {
    path: "/PedagogicalStructure",
    name: "Pedagogical Structure",
    icon: "ni ni-bullet-list-67 text-red",
    component: <PedagogicalStructure />, 
    layout: "/admin",
  },
  {
    path: "/users",
    name: "users",
    icon: "ni ni-single-02 text-blue",
    component: <User />, 
    layout: "/admin",
  },  
  {
    path: "/accounts",
    name: "accounts",
    icon: "ni ni-single-02 text-blue",
    component: <Accounts/>, 
    layout: "/admin",
  },
  {
    path: "/login",
    name: "Login",
    icon: "ni ni-key-25 text-info",
    component: <Login />, 
    layout: "/auth",
  },
];
export default routes;
