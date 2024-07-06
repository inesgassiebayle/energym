import './App.css';
import React from "react";
import SignUp from "./Components/GetStarted/SignUp/SignUp";
import GetStarted from "./Components/GetStarted/GetStarted";
import Login from "./Components/GetStarted/Login/Login";
import TrainerSignup from "./Components/GetStarted/TrainerSignup/TrainerSignup";
import {useNavigate, Route, Routes} from "react-router-dom";
import ChangePassword from "./Components/GetStarted/Login/ChangePassword/ChangePassword";
import AdminHome from "./Components/AdminHome/AdminHome";
import ManageStaff from "./Components/AdminHome/ManageStaff/ManageStaff";
import ProfessorView from "./Components/AdminHome/ManageStaff/ProfessorView";
import StudentHome from "./Components/StudentHome/StudentHome";
import ManageActivities from "./Components/AdminHome/ManageActivities/ManageActivities";
import ActivityAddition from "./Components/AdminHome/ManageActivities/AddActivity/ActivityAddition";
import ManageRooms from "./Components/AdminHome/ManageRooms/ManageRooms";
import CreateRoom from "./Components/AdminHome/ManageRooms/CreateRoom/CreateRoom";
import ProfessorHome from "./Components/ProfessorHome/ProfessorHome";
import MySchedule from "./Components/ProfessorHome/MySchedule/MySchedule";
import Payment from "./Components/StudentHome/Payment";
import Calendar from "./Components/StudentHome/MySchedule/Calendar";
import ProfessorCalendar from "./Components/ProfessorHome/MySchedule/Calendar";
import AdminCalendar from "./Components/AdminHome/ManageSchelude/Calendar";


function App() {
    const navigate = useNavigate();
    return (
        <Routes>
            <Route path="/" element={<GetStarted/>}/>
            <Route path="/login" element={<Login/>}/>
            <Route path="/signup" element={<SignUp/>}/>
            <Route path="/signup-trainer" element={<TrainerSignup/>}/>
            <Route path="/change-password" element={<ChangePassword/>}/>
            <Route path="/AdministratorHome" element={<AdminHome/>}/>
            <Route path="/AdministratorHome/ManageStaff" element={<ManageStaff/>}/>
            <Route path="/AdministratorHome/staff/:trainer" element={<ProfessorView/>}/>
            <Route path="/StudentHome" element={<StudentHome/>}/>
            <Route path="/AdministratorHome/ManageActivities" element={<ManageActivities/>}/>
            <Route path="/AdministratorHome/ManageActivities/AddActivity" element={<ActivityAddition/>}/>
            <Route path="/AdministratorHome/ManageRooms" element={<ManageRooms/>}/>
            <Route path="/AdministratorHome/ManageRooms/CreateRoom" element={<CreateRoom/>}/>
            <Route path="/trainer/:username" element={<ProfessorHome/>}/>
            <Route path="/trainer/:username/schedule" element={<MySchedule/>}/>
                <Route path="/trainer/:username/calendar" element={<ProfessorCalendar/>}/>
                <Route path="/student/:username" element={<StudentHome/>}/>
            <Route path="/student/:username/payment" element={<Payment/>}/>
            <Route path="/student/:username/calendar" element={<Calendar/>}/>
            <Route path='/AdministratorHome/Calendar' element={<AdminCalendar/>}/>
        </Routes>
    );
}

export default App;