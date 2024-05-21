import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminHome.css';
import logo from "../Assets/Logo.png";
import deleteIcon from "../Assets/person.png"
import axios from "axios";
import authentication from './Hoc/Hoc';
import ChangePasswordModal from "./ChangePasswordModal";

const AdminHome = () => {
    let navigate = useNavigate();
    const [showChangePasswordModal, setShowChangePasswordModal] = useState(false);

    const handleLogout = async () => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                await axios.post('http://localhost:3333/user/logout', {}, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                localStorage.removeItem('token');
                navigate('/login');
            } catch (error) {
                console.error('Failed to invalidate the token on the server:', error);
            }
        }
    };

    const handleDeleteAccountClick = () => {
        setShowChangePasswordModal(true);
    };

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
                <button className='admin-button' onClick={() => navigate('/AdministratorHome/ManageRooms')}>Manage
                    Rooms
                </button>
                <button className='admin-button' onClick={() => navigate('/AdministratorHome/ManageActivities')}>Manage
                    Activities
                </button>
                <button className='admin-button logout' onClick={handleLogout}>Log Out</button>

                <button className='admin-button delete-account' onClick={handleDeleteAccountClick}>
                    <img src={deleteIcon} alt="Delete account" />
                </button>

                {showChangePasswordModal && (
                    <div className="modal-overlay">
                        <div className="modal-content">
                            <ChangePasswordModal
                                onClose={() => setShowChangePasswordModal(false)}
                            />
                        </div>
                    </div>
                )}

            </div>
        </div>
    );
};

export default authentication(AdminHome);
