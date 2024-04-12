import React from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminHome.css';
import logo from "../Assets/Logo.png";

const AdminHome = () => {
    let navigate = useNavigate();

    return (
        <div className='admin-home-container'>
            <div className='logo-admin'>
                <img src={logo} alt="Logo" style={{width: '150px'}}/>
            </div>
            <div className='admin-actions'>
                <button className='admin-button' onClick={() => navigate('/AdministratorHome/ManageStaff')}>Manage
                    Staff
                </button>
                <button className='admin-button' onClick={() => navigate('/AdministratorHome/ManageSchedule')}>Manage
                    Schedule
                </button>
                <button className='admin-button'onClick={() => navigate('/AdministratorHome/ManageRooms')}>Manage Rooms </button>
                <button className='admin-button' onClick={() => navigate('/AdministratorHome/ManageActivities')}>Manage
                    Activities
                </button>
                <button className='admin-button logout' onClick={() => navigate('/')}>Log Out</button>
            </div>
        </div>
    );
}

export default AdminHome;
