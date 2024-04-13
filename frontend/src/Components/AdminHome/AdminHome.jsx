import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminHome.css';
import logo from "../Assets/Logo.png";
import axios from "axios";

const AdminHome = () => {
    let navigate = useNavigate();
    const [username, setUsername] = useState('');

    // Function to verify token validity and user role
    const verifyToken = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            console.log('No token found, redirecting to login.');
            navigate('/Login');
            return;
        }

        try {
            const response = await axios.get('http://localhost:3333/user/verify', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            // Check if the user is an administrator
            if (response.data.type !== 'ADMINISTRATOR') {
                console.log('User is not an administrator, redirecting to login.');
                navigate('/Login');
                return;
            }

            setUsername(response.data.username);
        } catch (error) {
            console.error('Token validation failed:', error);
            navigate('/Login');
        }
    };

    useEffect(() => {
        //verifyToken();
    }, []);

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

    return (
        <div className='admin-home-container'>
            <div className='logo-admin'>
                <img src={logo} alt="Logo" style={{width: '150px'}}/>
            </div>
            <div className='admin-actions'>
                <button className='admin-button' onClick={() => navigate('/AdministratorHome/ManageStaff')}>Manage Staff</button>
                <button className='admin-button' onClick={() => navigate('/AdministratorHome/ManageSchedule')}>Manage Schedule</button>
                <button className='admin-button' onClick={() => navigate('/AdministratorHome/ManageRooms')}>Manage Rooms</button>
                <button className='admin-button' onClick={() => navigate('/AdministratorHome/ManageActivities')}>Manage Activities</button>
                <button className='admin-button logout' onClick={handleLogout}>Log Out</button>
            </div>
        </div>
    );
};

export default AdminHome;
