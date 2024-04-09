import {Link} from "react-router-dom";
import React from "react";
import './ManageSchedule.css';
import logo from "../../Assets/Logo.png"; // Asegúrate de tener el logo en la carpeta correspondiente

const ManageSchedule = () => {
    return (
        <div className='manage-schedule-container'>
            <div className="schedule-header">
                <div className="schedule-title">
                    <div className="text">Manage Schedule</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='schedule-actions'>
                <button className='schedule-button'>Add Lesson</button>
                <button className='schedule-button'>Delete Lesson</button>
                <Link to={"/AdministratorHome"}>
                    <button className='schedule-button back'>Home</button>
                </Link>
            </div>
        </div>
    )
}

export default ManageSchedule;
