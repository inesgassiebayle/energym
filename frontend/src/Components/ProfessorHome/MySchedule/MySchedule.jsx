import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './MySchedule.css';

const MySchedule = () => {
    const [selectedDate, setSelectedDate] = useState('');
    const [classesForSelectedDate, setClassesForSelectedDate] = useState([]);
    const navigate = useNavigate();

    const handleDateChange = (e) => {
        const selectedDate = e.target.value;
        setSelectedDate(selectedDate);
        setClassesForSelectedDate([]);
    };

    const handleGoHome = () => {
        navigate('/ProfessorHome');
    };

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
                    {classesForSelectedDate.length > 0 ? (
                        <ul>
                            {classesForSelectedDate.map((classInfo, index) => (
                                <li key={index}>{classInfo}</li>
                            ))}
                        </ul>
                    ) : (
                        <p>No hay clases planeadas para el día seleccionado.</p>
                    )}
                </div>
            )}
            <button className="go-home-button" onClick={handleGoHome}>Home</button>
        </div>
    );
};

export default MySchedule;
