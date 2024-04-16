import './ManageStaff.css';
import React, {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import logo from '../../Assets/Logo.png';
import person_icon from "../../Assets/person.png";
import axios from "axios";

const ManageStaff = () => {
    let navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [showConfirmDialog, setShowConfirmDialog] = useState(false);

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
        console.log('Generating ID for user:', username);
    };

    const handleDeleteUser = () => {
        console.log('Deleting user:', username);
        setUsername('');
    };

    const ConfirmationDialog = () => (
        <div className="confirmation-dialog">
            <div className="confirmation-content">
                <h2>Confirm Delete</h2>
                <p>Are you sure you want to delete {username}?</p>
                <button onClick={confirmDeleteHandler}>Confirm</button>
                <button onClick={() => setShowConfirmDialog(false)}>Cancel</button>
            </div>
        </div>
    );

    const confirmDeleteHandler = async () => {
        try {
            await axios.delete(`http://localhost:3333/user/${username}/delete`);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error('Error deleting user:', error);
        }
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
                    <input type='text' value={username} onChange={(e) => setUsername(e.target.value)}
                           placeholder='Enter username'/>
                </div>
                <button className='staff-button' onClick={() => handleDeleteUser(username)}>Delete User</button>
                <Link to={'/AdministratorHome'}>
                    <button className='staff-button back'>Home</button>
                </Link>
            </div>
            {showConfirmDialog && <ConfirmationDialog />}
        </div>
    );
};

export default ManageStaff;
