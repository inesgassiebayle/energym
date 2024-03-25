import './App.css';
import React from "react";
import SignUp from "./Components/SignUp/SignUp";
import ForgotPassword from "./Components/ForgotPassword/ForgotPassword";
import Home from "./Components/Home/Home";
import Login from "./Components/Login/Login";
import TrainerSignup from "./Components/TrainerSignup/TrainerSignup";
import {useNavigate, Route, Routes} from "react-router-dom";
import ChangePassword from "./Components/ChangePassword/ChangePassword";
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

      </Routes>
  );
}

export default App;
