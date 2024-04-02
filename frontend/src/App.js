import './App.css';
import React from "react";
import SignUp from "./Components/SignUp/SignUp";
import ForgotPassword from "./Components/ForgotPassword/ForgotPassword";
import Home from "./Components/Home/Home";
import Login from "./Components/Login/Login";
import TrainerSignup from "./Components/TrainerSignup/TrainerSignup";
import {useNavigate, Route, Routes} from "react-router-dom";
import ChangePassword from "./Components/ChangePassword/ChangePassword";
import AdminHome from "./Components/AdminHome/AdminHome";
import ManageSchedule from "./Components/ManageSchelude/ManageSchedule";
import ManageStaff from "./Components/ManageStaff/ManageStaff";
import ManageRooms from "./Components/ManageRooms/ManageRooms";
import CreateRoom from "./Components/CreateRoom/CreateRoom";
import DeleteRoom from "./Components/DeleteRoom/DeleteRoom";


function App() {
    const navigate = useNavigate();
  return (
      <Routes>
          <Route path="/" element={<Home/>}/>
          <Route path="/Login" element={<Login/>}/>
          <Route path="/Signup" element={<SignUp/>}/>
          <Route path="/Login/RestorePassword" element={<ForgotPassword/>}/>
          <Route path="/SignUp/TrainerSignUp" element={<TrainerSignup/>}/>
          <Route path="/Login/RestorePassword/ChangePassword" element={<ChangePassword/>}/>
          <Route path="/AdminHome" element={<AdminHome/>}/>
          <Route path="/ManageSchedule" element={<ManageSchedule/>}/>
          <Route path="/ManageStaff" element={<ManageStaff/>}/>
          <Route path="/ManageRooms" element={<ManageRooms/>}/>
          <Route path="/CreateRoom" element={<CreateRoom/>}/>
          <Route path="/DeleteRoom" element={<DeleteRoom/>}/>




      </Routes>
  );
}

export default App;
