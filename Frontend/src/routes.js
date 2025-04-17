import Index from "views/Index.js";
import Profile from "views/examples/Profile.js";
import Login from "views/examples/Login.js";
import User from "views/examples/users.js";
import Accounts from "views/examples/accounts.js";
import Student from "views/examples/student.js";
import DeliberationFile from "views/examples/DeliberationFile.js";
import NoteFile from "views/examples/NoteFile.js";
import ArchiveFile from "views/examples/ArchiveFile.js";
import Filieres from "views/examples/Filieres";
import Niveaux from "views/examples/Niveaux";
import Modules from "views/examples/Modules";
import Elements from "views/examples/Elements";

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
    path: "/pedagogical-structure",
    name: "Structure Pédagogique",
    icon: "ni ni-bullet-list-67 text-red",
    layout: "/admin",
    children: [
      {
        path: "/pedagogical-structure/filieres",
        name: "Filières",
        icon: "ni ni-badge text-indigo",  // Graduation cap alternative
        component: <Filieres />,
        layout: "/admin",
      },
      {
        path: "/pedagogical-structure/niveaux",
        name: "Niveaux",
        icon: "ni ni-bold text-cyan",  // Represents levels/steps
        component: <Niveaux />,
        layout: "/admin",
      },
      {
        path: "/pedagogical-structure/modules",
        name: "Modules",
        icon: "ni ni-book-bookmark text-purple",  // Book for modules
        component: <Modules />,
        layout: "/admin",
      },
      {
        path: "/pedagogical-structure/elements",
        name: "Éléments",
        icon: "ni ni-ruler-pencil text-teal",  // Pencil for elements
        component: <Elements />,
        layout: "/admin",
      },
    ],
  },
  {
    path: "/documents",
    name: "Documents",
    icon: "ni ni-folder-17 text-orange",
    layout: "/admin",
    children: [
      {
        path: "/documents/deliberationfile",
        name: "Fichier de Délibération",
        icon: "ni ni-single-copy-04 text-purple",
        component: <DeliberationFile />,
        layout: "/admin",
      },
      {
        path: "/documents/note-file",
        name: "Fichier de Note",
        icon: "ni ni-single-copy-04 text-teal",
        component: <NoteFile />,
        layout: "/admin",
      },
      {
        path: "/documents/archive-file",
        name: "Archive",
        icon: "ni ni-archive-2 text-gray",
        component: <ArchiveFile />,
        layout: "/admin",
      },
    ],
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
    component: <Accounts />,
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