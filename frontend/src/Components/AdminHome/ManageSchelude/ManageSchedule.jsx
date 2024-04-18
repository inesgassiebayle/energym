
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './ManageSchedule.css';
import logo from '../../Assets/Logo.png';
import axios from 'axios';

const ManageSchedule = () => {
    const [selectedDate, setSelectedDate] = useState('');
    const [classesForSelectedDate, setClassesForSelectedDate] = useState([]);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchClassesForSelectedDate = async () => {
            try{
                const response = await axios.get('http://localhost:3333/lesson/:date/getLessons');
                setClassesForSelectedDate(response.data);
            } catch (error) {
                console.error('Error fetching classes:', error);
            }
        }
        fetchClassesForSelectedDate();
    }, [selectedDate]);

    const fetchClassesForSelectedDate = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/lessons/${selectedDate}`);
            setClassesForSelectedDate(response.data);
            setError(null); // Limpiar el error si la solicitud tiene éxito
        } catch (error) {
        }
    };

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
            {error && <p className="error-message">{error}</p>} {/* Mostrar el mensaje de error si existe */}
            {selectedDate && (
                <div className="schedule-info">
                    <h3>Classes for {selectedDate}:</h3>
                    {classesForSelectedDate.length > 0 ? (
                        <ul>
                            {classesForSelectedDate.map((classInfo, index) => (
                                <li key={index}>{classInfo.name}</li>
                                // Reemplaza classInfo.name con la propiedad correcta de la clase
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
                </Link>
                <Link to="/AdministratorHome">
                    <button className="schedule-button back">Home</button>
                </Link>
            </div>
        </div>
    );
};

export default ManageSchedule;
