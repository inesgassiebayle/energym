import React, { useState, useEffect } from 'react';
import axios from 'axios';
import '../../Modal.css';
import authentication from "../Hoc/Hoc";

const ModifyLessonModal = ({ isOpen, onClose, lesson, date , onSave }) => {
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


    const fetchDetails = async () => {
        try {
            const response = await axios.get('http://localhost:3333/lesson', {
                params: {
                    username: lesson.professor,
                    startDate: date,
                    time: lesson.time
                }
            });
            setOldActivity(response.data.activity);
            console.log(response.data.activity);
            setActivity(response.data.activity);
            console.log(response.data.professor);
            setOldProfessor(response.data.professor);
            setProfessor(response.data.professor);
            console.log(response.data.room);
            setOldRoom(response.data.room);
            setRoomName(response.data.room);
            setActivityUnsupported();
        } catch (error) {
            console.error('Error fetching data:', error);
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
        const updateData = {
            oldName: oldName,
            oldTime: oldTime,
            oldDate: oldStartDate,
            name: name,
            time: time,
            activity: activity,
            professor: professor,
            oldProfessor: oldProfessor,
            roomName: roomName,
            startDate: startDate,

        };
        try {
            const responseUpdate = await axios.patch('http://localhost:3333/lesson/modify', updateData);
            console.log(responseUpdate.data)
            onSave();
            onClose();
        } catch (error) {
            const errorMsg = error.response?.data || 'An unexpected error occurred.';
            console.error('Error while sending request:', errorMsg);
            setProfessorUnavailableError('');
            setRoomUnavailableError('');
            setActivityUnsupported('');
            if (errorMsg.includes("Professor is not available")) {
                setProfessorUnavailableError("Professor is unavailable at the new time/date");
            } else if (errorMsg.includes("Room is not available or does not support the activity")) {
                setRoomUnavailableError("Room is not available or does not support the activity")
            } else if (errorMsg.includes('New activity not supported in the selected room')){
                setActivityUnsupported("New activity not supported in the selected room")
            }
        }
    };

    const generateHourOptions = () => {
        let options = [];
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
                    <input type="text" value={name} onChange={e => setName(e.target.value)} placeholder="Lesson Name"/>

                    <select value={time} onChange={(e) => {
                        setTime(e.target.value);
                        setProfessorUnavailableError('');
                        setRoomUnavailableError('');

                    }} required>
                        <option value="">Select Lesson Time</option>
                        {generateHourOptions().map((hour, index) => (
                            <option key={index} value={hour}>{hour}</option>
                        ))}
                    </select>


                    <select value={activity} onChange={(e) => {
                        setActivity(e.target.value);
                        setActivityUnsupported('');
                    }} required>
                        <option value="">Select Activity</option>
                        {activities.map((activity, index) => (
                            <option key={index} value={activity}>{activity}</option>
                        ))}
                    </select>
                    {activityError &&
                        <div className="error-message" style={{color: 'red', textAlign: 'center'}}>{activityError}</div>}
                    <select value={professor}
                            onChange={(e) => {
                                setProfessor(e.target.value);
                                setProfessorUnavailableError(''); //limpiar el error

                            }} required>
                        <option value="">Select Professor</option>
                        {professors.map((professor, index) => (
                            <option key={index} value={professor}>{professor}</option>
                        ))}
                    </select>
                    {professorError &&
                        <div className="error-message" style={{color: 'red', textAlign: 'center'}}>{professorError}</div>}

                    <select value={roomName} onChange={(e) => {
                        setRoomName(e.target.value);
                        setRoomUnavailableError('');
                    }} required>

                        <option value="">Select Room</option>
                        {rooms.map((room, index) => (
                            <option key={index} value={room}>{room}</option>
                        ))}
                    </select>
                    {roomError && <div className="error-message" style={{color: 'red', textAlign: 'center'}}>{roomError}</div>}

                    <input className="modal-date-picker" type="date" value={startDate} onChange={e => {
                        setStartDate(e.target.value);
                        setProfessorUnavailableError('');
                        setRoomUnavailableError('');
                    }}
                           placeholder="Start Date (YYYY-MM-DD)"/>
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