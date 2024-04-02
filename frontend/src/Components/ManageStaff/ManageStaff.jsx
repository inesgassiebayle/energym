
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import logo from '../Assets/Logo.png';

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
        <div className='container-manage-staff'>
            <div className='header-staff'>
                <img src={logo} alt='Logo' style={{ width: '100px' }} />
                <h1>Manage Staff</h1>
            </div>
            <div className='staff-actions'>
                <div className='user-input'>
                    <input type='text' value={userName} onChange={(e) => setUserName(e.target.value)} placeholder='Enter user name' />
                </div>
                <button className='staff-button' onClick={handleGenerateId}>Generate ID</button>
                <button className='staff-button' onClick={handleDeleteUser}>Delete User</button>
                <Link to={'/AdminHome'}>
                    <button className='staff-button back'>Back to Admin Home</button>
                </Link>
            </div>
        </div>
    );
};

export default ManageStaff;
