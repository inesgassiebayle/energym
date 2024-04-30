import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './MySchedule.css';
import {Link, useNavigate, useParams} from 'react-router-dom';



const MySchedule = () => {
    const { username } = useParams();
    const [selectedDate, setSelectedDate] = useState('');
    const [error, setError] = useState('');
    const [lessons, setLessons] = useState([]);

    const handleDateChange = (e) => {
        const selectDate = e.target.value;
        setSelectedDate(selectDate);
    };

    useEffect(() => {
        if (selectedDate) {
            const fetchClassesForSelectedDate = async () => {
                try {
                    const response = await axios.get('http://localhost:3333/professor/lessons', {
                        params: {
                            username: username,
                            date: selectedDate
                        }
                    });
                    setLessons(response.data);
                } catch (error) {
                    console.error('Error fetching classes:', error);
                    setError('Failed to fetch classes.');
                }
            };
            fetchClassesForSelectedDate();
        }
        console.log('Viewing details for trainer:', username);
    }, [username, selectedDate]);



    return (
        <div className="my-schedule-container">
            <h2>My Schedule</h2>
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
            {selectedDate && (
                <div className="schedule-info">
                    <h3>Classes for {selectedDate}:</h3>
                    {lessons.length > 0 ? (
                        <ul>
                            {lessons.map((classInfo, index) => (
                                <li key={index}>{classInfo.name} {classInfo.time}</li>
                            ))}
                        </ul>
                    ) : (
                        <p>No hay clases planeadas para el día seleccionado.</p>
                    )}
                </div>
            )}
            <Link to={'/trainer/${userData.username}'}>
                <button className='staff-button back'>Home</button>
            </Link>
            </div>
    );
};

export default MySchedule;
