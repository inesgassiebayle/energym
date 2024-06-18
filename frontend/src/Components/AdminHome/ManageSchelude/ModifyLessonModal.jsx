import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../../Modal.css';
import authentication from "../Hoc/Hoc";
import './AddLesson.css'

const ModifyLessonModal = ({ isOpen, onClose, lesson, date, onSave }) => {
    const [name, setName] = useState('');
    const [time, setTime] = useState('');
    const [activity, setActivity] = useState('');
    const [professor, setProfessor] = useState('');
    const [roomName, setRoomName] = useState('');
    const [startDate, setStartDate] = useState('');

    const [oldName, setOldName] = useState('');
    const [oldTime, setOldTime] = useState('');
    const [oldActivity, setOldActivity] = useState('');
    const [oldProfessor, setOldProfessor] = useState('');
    const [oldRoomName, setOldRoom] = useState('');
    const [oldStartDate, setOldStartDate] = useState('');

    const [activities, setActivities] = useState([]);
    const [professors, setProfessors] = useState([]);
    const [rooms, setRooms] = useState([]);

    const [professorError, setProfessorUnavailableError] = useState('');
    const [roomError, setRoomUnavailableError] = useState('');
    const [activityError, setActivityUnsupported] = useState('');
    const [dateError, setDateError] = useState('');
    const [error, setError] = useState('');


    const fetchDetails = async () => {
        try {
            const response = await axios.get('http://localhost:3333/lesson', {
                params: {
                    username: lesson.professor,
                    startDate: date,
                    time: lesson.time
                }
            });
            const lessonDetails = response.data;
            setOldActivity(lessonDetails.activity);
            setActivity(lessonDetails.activity);
            setOldProfessor(lessonDetails.professor);
            setProfessor(lessonDetails.professor);
            setOldRoom(lessonDetails.room);
            setRoomName(lessonDetails.room);
        } catch (error) {
            console.error('Error fetching lesson details:', error);
        }

        try {
            const [actRes, profRes, roomRes] = await Promise.all([
                axios.get('http://localhost:3333/activity/get'),
                axios.get('http://localhost:3333/professor/get'),
                axios.get('http://localhost:3333/room/get')
            ]);
            setActivities(actRes.data);
            setProfessors(profRes.data);
            setRooms(roomRes.data);
        } catch (error) {
            console.error('Error fetching initial data:', error);
        }
    };

    const isPastDate = (date, time) => {
        const today = new Date();
        const dateTime = new Date(`${date}T${time}`);
        return dateTime < today;
    };

    useEffect(() => {
        if (isOpen && lesson) {
            setOldName(lesson.name);
            setOldTime(lesson.time);
            setOldStartDate(date);
            setName(lesson.name);
            setTime(lesson.time);
            setStartDate(date);
            fetchDetails();
        }
    }, [isOpen, lesson, date]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (isPastDate(startDate, time)) {
            setDateError('Cannot modify classes to a past date or time.');
            return;
        }

        const updateData = {
            oldName,
            oldTime,
            oldDate: oldStartDate,
            name,
            time,
            activity,
            professor,
            oldProfessor,
            roomName,
            startDate,
        };

        try {
            const response = await axios.patch('http://localhost:3333/lesson/modify', updateData);
            console.log('Lesson updated:', response.data);
            onSave(); // Invoke save callback to update the parent component
            onClose(); // Close the modal
        } catch (error) {
            const errorMsg = error.response?.data || 'An unexpected error occurred.';
            console.error('Error while sending request:', errorMsg);
            setProfessorUnavailableError('');
            setRoomUnavailableError('');
            setActivityUnsupported('');
            if (errorMsg.includes("Professor is not available")) {
                setProfessorUnavailableError("Professor is unavailable at the new time/date.");
            } else if (errorMsg.includes("Room is not available or does not support the activity")) {
                setRoomUnavailableError("Room is not available or does not support the activity.");
            } else if (errorMsg.includes('New activity not supported in the selected room')){
                setActivityUnsupported("New activity not supported in the selected room.");
            } else {
                setError(errorMsg);
            }
        }
    };

    const generateHourOptions = () => {
        const options = [];
        for (let i = 8; i <= 21; i++) {
            options.push(`${i.toString().padStart(2, '0')}:00`);
        }
        return options;
    };

    if (!isOpen) return null;

    return (
        <div className="modal">
            <div className="modal-header">
                <h5 className="modal-title">Modify Lesson {lesson.name}</h5>
                <button onClick={onClose} className="modal-close-button">&times;</button>
            </div>
            <div className="modal-body">
                <form onSubmit={handleSubmit} className="modal-form">
                    <input
                        type="text"
                        value={name}
                        onChange={e => setName(e.target.value)}
                        placeholder="Lesson Name"
                    />

                    <select
                        value={time}
                        onChange={(e) => {
                            setTime(e.target.value);
                            setProfessorUnavailableError('');
                            setRoomUnavailableError('');
                        }}
                        required
                    >
                        <option value="">Select Lesson Time</option>
                        {generateHourOptions().map((hour, index) => (
                            <option key={index} value={hour}>{hour}</option>
                        ))}
                    </select>

                    <select
                        value={activity}
                        onChange={(e) => {
                            setActivity(e.target.value);
                            setActivityUnsupported('');
                        }}
                        required
                    >
                        <option value="">Select Activity</option>
                        {activities.map((activity, index) => (
                            <option key={index} value={activity}>{activity}</option>
                        ))}
                    </select>
                    {activityError && <div className="error-message" style={{ color: 'red', textAlign: 'center' }}>{activityError}</div>}

                    <select
                        value={professor}
                        onChange={(e) => {
                            setProfessor(e.target.value);
                            setProfessorUnavailableError(''); // Clear error
                        }}
                        required
                    >
                        <option value="">Select Professor</option>
                        {professors.map((prof, index) => (
                            <option key={index} value={prof}>{prof}</option>
                        ))}
                    </select>
                    {professorError && <div className="error-message" style={{ color: 'red', textAlign: 'center' }}>{professorError}</div>}

                    <select
                        value={roomName}
                        onChange={(e) => {
                            setRoomName(e.target.value);
                            setRoomUnavailableError('');
                        }}
                        required
                    >
                        <option value="">Select Room</option>
                        {rooms.map((room, index) => (
                            <option key={index} value={room}>{room}</option>
                        ))}
                    </select>
                    {roomError && <div className="error-message" style={{ color: 'red', textAlign: 'center' }}>{roomError}</div>}
                    {dateError && <div className="error-message" style={{ color: 'red', textAlign: 'center' }}>{dateError}</div>}

                    <input
                        className="modal-date-picker"
                        type="date"
                        value={startDate}
                        onChange={e => {
                            setStartDate(e.target.value);
                            setDateError('');
                            setProfessorUnavailableError('');
                            setRoomUnavailableError('');
                        }}
                        placeholder="Start Date (YYYY-MM-DD)"
                    />
                </form>
            </div>
            <div className="modal-footer">
                <button type="submit" className="submit" onClick={handleSubmit}>Save Changes</button>
                <button type="button" className="cancel" onClick={onClose}>Cancel</button>
            </div>
        </div>
    );
};

export default authentication(ModifyLessonModal);
