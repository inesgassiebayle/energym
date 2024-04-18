import './ProfessorView.css';
import React, {useEffect, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import logo from '../../Assets/Logo.png';
import axios from "axios";

const ProfessorView = () => {
    const { trainer } = useParams();
    let navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [lessons, setLessons] = useState([]);

    // Function to verify token validity and user role
    const verifyToken = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            console.log('No token found, redirecting to login.');
            navigate('/login');
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
                navigate('/login');
                return;
            }

            setUsername(response.data.username);
        } catch (error) {
            console.error('Token validation failed:', error);
            navigate('/login');
        }
    };

    const trainerLessons = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/professor/${trainer}/lessons`);
            setLessons(response.data);
        } catch (error){
            console.error('Failed to fetch lessons:', error);
        }
    };

    useEffect(() => {
        verifyToken();
        trainerLessons();
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
                {lessons.map((lesson, index) => (
                    <div key={index}>
                        <h2>{lesson.name}</h2>
                        <p>{lesson.date}</p>
                    </div>
                ))}
                <Link to={"/AdministratorHome"}>
                    <button className='staff-button back'>Home</button>
                </Link>
            </div>
        </div>
    );
};

export default ProfessorView;
