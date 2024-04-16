import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './ManageSchedule.css';
import logo from '../../Assets/Logo.png';
import axios from 'axios';

const ManageSchedule = () => {
    const [selectedDate, setSelectedDate] = useState('');
    const [classesForSelectedDate, setClassesForSelectedDate] = useState([]);
    const navigate = useNavigate();

    const handleDateChange = (e) => {
        const selectedDate = e.target.value;
        setSelectedDate(selectedDate);
        // Aquí deberías llamar a tu API para obtener las clases planificadas para la fecha seleccionada
        // Por ahora, simplemente dejamos un array vacío
        setClassesForSelectedDate([]);
    };

    return (
        <div className="manage-schedule-container">
            <div className="schedule-header">
                <div className="schedule-title">
                    <div className="text">Manage Schedule</div>
                </div>
                <div className="logo">
                    <img src={logo} alt="" />
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
            <div className="schedule-actions">
                <Link to="/AdministratorHome/ManageSchedule/AddLesson">
                    <button className="schedule-button">Add Lesson</button>
                    <button className="schedule-button back">Home</button>
                </Link>
            </div>
        </div>
    );
};

export default ManageSchedule;
