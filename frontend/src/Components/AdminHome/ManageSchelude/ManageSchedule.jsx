import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import logo from '../../Assets/Logo.png';
import axios from 'axios';
import ModifyLessonModal from './ModifyLessonModal.jsx';
import authentication from '../Hoc/Hoc'

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
                const response = await axios.get(`http://localhost:3333/lesson`, {
                    params: {
                        startDate: selectedDate
                    }
                });
                setClassesForSelectedDate(response.data);
            } catch (error) {
                console.error('Error fetching classes:', error);
                setError('Failed to fetch classes.');
            }
        }
    };

    useEffect(() => {
        fetchClassesForSelectedDate();
    }, [navigate, selectedDate]);

    const handleDeleteLesson = async (lesson) => {
        setSelectedLesson(lesson);
        setConfirmDelete(true);
    };

    const confirmDeleteHandler = async () => {
        const lessonData = {
            username: selectedLesson.professor,
            startDate: selectedDate,
            time: selectedLesson.time
        };

        try {
            const endpoint = 'http://localhost:3333/lesson';
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
        <div className="home-components-container">
            <div className="home-components-header">
                <div className="home-components-title">
                    <div className="home-components-text">Manage Schedule</div>
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
                                    <span>{classInfo.name} by {classInfo.professor} at {classInfo.time}</span>
                                    <button className="home-components-modification-button" onClick={() => handleDeleteLesson(classInfo)}>Delete</button>
                                    <button className='home-components-modification-button' onClick={() => handleView(classInfo)}>View</button>
                                </div>
                            ))}
                        </ul>
                    ) : <p>No classes planned for the selected day.</p>}
                </div>
            )}
            <div className="home-components-actions">
                <Link to="/AdministratorHome/ManageSchedule/AddLesson">
                    <button className="home-components-button">Add Lesson</button>
                </Link>
                <Link to="/AdministratorHome">
                    <button className="home-components-button back">Home</button>
                </Link>
            </div>
            {confirmDelete && (
                <div className="modal">
                    <p>Are you sure you want to delete the lesson {selectedLesson?.name}?</p>
                    <div className="modal-footer">
                        <button onClick={confirmDeleteHandler} className="modal-button cancel">Yes</button>
                        <button onClick={() => setConfirmDelete(false)} className="modal-button">No</button>
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

export default authentication(ManageSchedule);