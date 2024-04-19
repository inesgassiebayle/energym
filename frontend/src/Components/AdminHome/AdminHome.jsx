import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminHome.css';
import logo from "../Assets/Logo.png";
import axios from "axios";
import authentication from './Hoc/Hoc';

const AdminHome = () => {
    let navigate = useNavigate();

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
                navigate('/Login');
            } catch (error) {
                console.error('Failed to invalidate the token on the server:', error);
            }
        }
    };

    const handleDelete = async () => {
        const confirmation = window.confirm('Are you sure you want to delete your account? This action cannot be undone.');

        if (confirmation) {
            const token = localStorage.getItem('token');
            if (token) {
                try {
                    await axios.delete('http://localhost:3333/user/delete', {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });
                    localStorage.removeItem('token');
                    navigate('/Login');
                } catch (error) {
                    console.error('Failed to delete the account:', error);
                }
            }
        }
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
                <button className='admin-button logout' onClick={handleDelete}>Delete account</button>

            </div>
        </div>
    );
};

export default authentication(AdminHome);
