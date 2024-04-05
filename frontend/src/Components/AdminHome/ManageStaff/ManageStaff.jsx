import './ManageStaff.css';
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import logo from '../../Assets/Logo.png';
import person_icon from "../../Assets/person.png";

const ManageStaff = () => {
    const [userName, setUserName] = useState('');

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
