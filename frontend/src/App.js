import './App.css';
import React from "react";
import SignUp from "./Components/GetStarted/SignUp/SignUp";
import ForgotPassword from "./Components/GetStarted/Login/ForgotPassword/ForgotPassword";
import GetStarted from "./Components/GetStarted/GetStarted";
import Login from "./Components/GetStarted/Login/Login";
import TrainerSignup from "./Components/GetStarted/SignUp/TrainerSignup/TrainerSignup";
import {useNavigate, Route, Routes} from "react-router-dom";
import ChangePassword from "./Components/GetStarted/Login/ForgotPassword/ChangePassword/ChangePassword";
import AdminHome from "./Components/AdminHome/AdminHome";
import ManageSchedule from "./Components/AdminHome/ManageSchelude/ManageSchedule";
import ManageStaff from "./Components/AdminHome/ManageStaff/ManageStaff";
import StudentHome from "./Components/StudentHome/StudentHome";
import ManageActivities from "./Components/AdminHome/ManageActivities/ManageActivities";
import ActivityAddition from "./Components/AdminHome/ManageActivities/AddActivity/ActivityAddition";
import ActivityDeletion from "./Components/AdminHome/ManageActivities/DeleteActivity/ActivityDeletion";
import ManageRooms from "./Components/AdminHome/ManageRooms/ManageRooms";
import CreateRoom from "./Components/AdminHome/ManageRooms/CreateRoom/CreateRoom";


function App() {
    const navigate = useNavigate();
    return (
        <Routes>
            <Route path="/" element={<GetStarted/>}/>
            <Route path="/Login" element={<Login/>}/>
            <Route path="/Signup" element={<SignUp/>}/>
            <Route path="/Login/RestorePassword" element={<ForgotPassword/>}/>
            <Route path="/SignUp/TrainerSignUp" element={<TrainerSignup/>}/>
            <Route path="/Login/RestorePassword/ChangePassword" element={<ChangePassword/>}/>
            <Route path="/AdministratorHome" element={<AdminHome/>}/>
            <Route path="/AdministratorHome/ManageSchedule" element={<ManageSchedule/>}/>
            <Route path="/AdministratorHome/ManageStaff" element={<ManageStaff/>}/>
            <Route path="/StudentHome" element={<StudentHome/>}/>
            <Route path="/AdministratorHome/ManageActivities" element={<ManageActivities/>}/>
            <Route path="/AdministratorHome/ManageActivities/AddActivity" element={<ActivityAddition/>}/>
            <Route path="/AdministratorHome/ManageActivities/DeleteActivity" element={<ActivityDeletion/>}/>
            <Route path="/AdministratorHome/ManageRooms" element={<ManageRooms/>}/>
            <Route path="/AdministratorHome/ManageRooms/CreateRoom" element={<CreateRoom/>}/>

        </Routes>
    );
}

export default App;