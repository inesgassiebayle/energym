import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './ManageSchedule.css';
import logo from '../../Assets/Logo.png';
import axios from 'axios';
import ModifyLessonModal from './ModifyLessonModal.jsx';

const ManageSchedule = () => {
    const navigate = useNavigate();
    const [selectedDate, setSelectedDate] = useState('');
    const [classesForSelectedDate, setClassesForSelectedDate] = useState([]);
    const [error, setError] = useState(null);
    const [confirmDelete, setConfirmDelete] = useState(false);
    const [selectedLesson, setSelectedLesson] = useState(null);
    const [showModifyModal, setShowModifyModal] = useState(false);

    const handleDateChange = (e) => {
        setSelectedDate(e.target.value);
    };

    const fetchClassesForSelectedDate = async () => {
        if (selectedDate) {
            try {
                const response = await axios.get(`http://localhost:3333/lesson/${selectedDate}/getLessons`);
                setClassesForSelectedDate(response.data);
            } catch (error) {
                console.error('Error fetching classes:', error);
                setError('Failed to fetch classes.');
            }
        }
    };

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
        fetchClassesForSelectedDate();
    }, [navigate, selectedDate]);

    const handleDeleteLesson = async (lesson) => {
        setSelectedLesson(lesson);
        setConfirmDelete(true);
    };

    const confirmDeleteHandler = async () => {
        const lessonData = {
            name: selectedLesson.name,
            startDate: selectedDate,
            time: selectedLesson.time
        };

        try {
            const endpoint = 'http://localhost:3333/lesson/delete';
            const response = await axios({
                method: 'delete',
                url: endpoint,
                data: lessonData
            });
            console.log(response.data);
            fetchClassesForSelectedDate();
            setConfirmDelete(false);
            setSelectedLesson(null);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error('Error:', error.response.data);
        }
    };

    const handleView = (lesson) => {
        setSelectedLesson(lesson);
        setShowModifyModal(true);
    };

    const closeModal = () => {
        setShowModifyModal(false);
        setSelectedLesson(null);
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
                <input type="date" id="date" name="date" value={selectedDate} onChange={handleDateChange} />
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
                                    <button className="modification-button delete" onClick={() => handleDeleteLesson(classInfo)}>Delete</button>
                                    <button className='modification-button' onClick={() => handleView(classInfo)}>View</button>
                                </div>
                            ))}
                        </ul>
                    ) : <p>No classes planned for the selected day.</p>}
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
            {confirmDelete && (
                <div className="confirmation-message">
                    <p>Are you sure you want to delete the lesson "{selectedLesson?.name}"?</p>
                    <div className="confirmation-actions">
                        <button onClick={confirmDeleteHandler}>Yes</button>
                        <button onClick={() => setConfirmDelete(false)} className="cancel">No</button>
                    </div>
                </div>
            )}
            {showModifyModal && (
                <ModifyLessonModal
                    isOpen={showModifyModal}
                    onClose={closeModal}
                    lesson={selectedLesson}
                    date = {selectedDate}
                    onSave={() => {
                        fetchClassesForSelectedDate();
                        closeModal();
                    }}
                />
            )}
        </div>
    );
};

export default ManageSchedule;