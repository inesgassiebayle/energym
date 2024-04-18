import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './ManageSchedule.css';
import logo from '../../Assets/Logo.png';
import axios from 'axios';

const ManageSchedule = () => {
    const navigate = useNavigate();
    const [selectedDate, setSelectedDate] = useState('');
    const [classesForSelectedDate, setClassesForSelectedDate] = useState([]);
    // const [selectedLesson, setSelectedLesson] = useState('');
    // const [confirmDelete, setConfirmDelete] = useState(false);
    const [error, setError] = useState(null);

    // Authentication check
    useEffect(() => {
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

                if (response.data.type !== 'ADMINISTRATOR') {
                    console.log('User is not an administrator, redirecting to login.');
                    navigate('/Login');
                    return;
                }
            } catch (error) {
                console.error('Token validation failed:', error);
                navigate('/Login');
            }
        };

        verifyToken();
    }, [navigate]);

    useEffect(() => {
        if (selectedDate) {
            const fetchClassesForSelectedDate = async () => {
                try {
                    const response = await axios.get(`http://localhost:3333/lesson/${selectedDate}/getLessons`);
                    setClassesForSelectedDate(response.data);
                } catch (error) {
                    console.error('Error fetching classes:', error);
                    setError('Failed to fetch classes.');
                }
            };
            fetchClassesForSelectedDate();
        }
    }, [selectedDate]);

    const handleDateChange = (e) => {
        setSelectedDate(e.target.value);
    };

    return (
        <div className="manage-schedule-container">
            <div className="schedule-header">
                <div className="schedule-title">
                    <div className="text">Manage Schedule</div>
                </div>
                <div className="logo">
                    <img src={logo} alt="Logo" />
                </div>
            </div>
            <div className="date-picker">
                <label htmlFor="date">Select a date:</label>
                <input
                    type="date"
                    id="date"
                    name="date"
                    value={selectedDate}
                    onChange={handleDateChange}
                />
            </div>
            {error && <p className="error-message">{error}</p>}
            {selectedDate && (
                <div className="schedule-info">
                    <h3>Classes for {selectedDate}:</h3>
                    {classesForSelectedDate.length > 0 ? (
                        <ul>
                            {classesForSelectedDate.map((classInfo, index) => (
                                <div key={index} className='staff-item'>
                                    <span>{classInfo.name} at {classInfo.time}</span>
                                    {/*<button className='modification-button'*/}
                                    {/*        onClick={() => handleDelete()}>Delete*/}
                                    {/*</button>*/}
                                    {/*<button className='modification-button' onClick={() => handleView()}>View*/}
                                    {/*</button>*/}

                                </div>
                            ))}
                        </ul>
                    ) : (
                        <p>No classes planned for the selected day.</p>
                    )}
                </div>
            )}
            <div className="schedule-actions">
                <Link to="/AdministratorHome/ManageSchedule/AddLesson">
                    <button className="schedule-button">Add Lesson</button>
                </Link>
                <Link to="/AdministratorHome">
                    <button className="schedule-button back">Home</button>
                </Link>
            </div>
        </div>
    );
};

export default ManageSchedule;





