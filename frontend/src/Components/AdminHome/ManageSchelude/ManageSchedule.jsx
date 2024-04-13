import {Link, useNavigate} from "react-router-dom";
import React, {useEffect, useState} from "react";
import './ManageSchedule.css';
import logo from "../../Assets/Logo.png";
import axios from "axios"; // Asegúrate de tener el logo en la carpeta correspondiente

const ManageSchedule = () => {
    const [username, setUsername] = useState('');
    let navigate = useNavigate();


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
        <div className='manage-schedule-container'>
            <div className="schedule-header">
                <div className="schedule-title">
                    <div className="text">Manage Schedule</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='schedule-actions'>
                <Link to="/AdministratorHome/ManageSchedule/AddLesson">
                    {/*agregue esto*/}
                 <button className='schedule-button'>Add Lesson</button>
                </Link>
                <button className='schedule-button'>Delete Lesson</button>
                <Link to={"/AdministratorHome"}>
                    <button className='schedule-button back'>Home</button>
                </Link>
            </div>
        </div>
    )
}

export default ManageSchedule;
