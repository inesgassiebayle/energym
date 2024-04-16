import './ProfessorView.css';
import React, {useEffect, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import logo from '../../Assets/Logo.png';
import axios from "axios";

const ProfessorView = () => {
    const { trainer } = useParams();
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
        console.log('Viewing details for trainer:', trainer);
    }, [trainer]);

    return (
        <div className='manage-staff-container'>
            <div className="manage-staff-header">
                <div className="manage-staff-title">
                    <div className="text">Trainer: {trainer}</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='staff-actions'>
                <Link to={"/AdministratorHome"}>
                    <button className='staff-button back'>Home</button>
                </Link>
            </div>
        </div>
    );
};

export default ProfessorView;