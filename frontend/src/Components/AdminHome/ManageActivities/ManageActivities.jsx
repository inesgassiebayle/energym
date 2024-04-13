import {Link, useNavigate} from "react-router-dom";
import logo from "../../Assets/Logo.png";
import React, {useEffect, useState} from "react";
import './ManageActivities.css'
import axios from "axios";

const ManageActivities = () => {
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
        verifyToken();
    }, []);


    return (
        <div className='manage-activities-container'>
            <div className="manage-activities-header">
                <div className="manage-activities-title">
                    <div className="text">Manage Activities</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='activities-actions'>
                <button className='activities-button'
                        onClick={() => navigate('/AdministratorHome/ManageActivities/AddActivity')}>Add Activity
                </button>
                <button className='activities-button'
                        onClick={() => navigate('/AdministratorHome/ManageActivities/DeleteActivity')}>Delete Activity
                </button>
                <Link to={"/AdministratorHome"}><button className='activities-button back'>Home</button></Link>
            </div>
        </div>
    );
}
export default ManageActivities;
