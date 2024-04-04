import {Link} from "react-router-dom";
import React from "react";
import './ManageSchedule.css';
import logo from "../../Assets/Logo.png"; // Asegúrate de tener el logo en la carpeta correspondiente

const ManageSchedule = () => {
    return (
        <div className='container-manage-schedule'>
            <div className='header-schedule'>
                <img src={logo} alt="Logo" style={{width: '100px'}}/>
                <h1>Manage Schedule</h1>
            </div>
            <div className='schedule-actions'>
                <button className='schedule-button'>Add Class</button>
                <button className='schedule-button'>Delete Class</button>
                <Link to={"/Home"}><button className='schedule-button back'>Back to Admin Home</button></Link>
            </div>
        </div>
    )
}

export default ManageSchedule;
