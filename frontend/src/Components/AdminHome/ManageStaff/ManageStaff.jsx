import './ManageStaff.css';
import React, {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import logo from '../../Assets/Logo.png';
import person_icon from "../../Assets/person.png";
import axios from "axios";

const ManageStaff = () => {
    let navigate = useNavigate();
    const [userName, setUserName] = useState('');
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
        verifyToken();
    }, []);

    const handleGenerateId = () => {
        // Lógica para generar un ID de usuario
        console.log('Generating ID for user:', userName);
    };

    const handleDeleteUser = () => {
        // Lógica para eliminar un usuario
        console.log('Deleting user:', userName);
        setUserName(''); // Limpiar el campo de entrada después de eliminar el usuario
    };

    return (
        <div className='manage-staff-container'>
            <div className="manage-staff-header">
                <div className="manage-staff-title">
                    <div className="text">Manage Staff</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='staff-actions'>
                <div className='user-input'>
                    <img src={person_icon} alt=""/>
                    <input type='text' value={userName} onChange={(e) => setUserName(e.target.value)}
                           placeholder='Enter username'/>
                </div>
                <button className='staff-button' onClick={handleGenerateId}>Generate ID</button>
                <button className='staff-button' onClick={handleDeleteUser}>Delete User</button>
                <Link to={'/AdministratorHome'}>
                    <button className='staff-button back'>Home</button>
                </Link>
            </div>
        </div>
    );
};

export default ManageStaff;
