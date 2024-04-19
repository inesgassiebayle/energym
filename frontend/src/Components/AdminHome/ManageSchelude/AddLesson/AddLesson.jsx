import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './AddLesson.css';
import logo from "../../../Assets/Logo.png";
import axios from "axios";

const LessonAddition = () => {
    const [lessonName, setLessonName] = useState('');
    const [lessonTime, setLessonTime] = useState('');
    const [activityName, setActivityName] = useState('');
    const [professorName, setProfessorName] = useState('');
    const [roomName, setRoomName] = useState('');
    const [lessonStartDate, setLessonStartDate] = useState('');
    const [isRecurring, setIsRecurring] = useState(false);
    const [endDate, setEndDate] = useState('');
    const [activities, setActivities] = useState([]);
    const [professors, setProfessors] = useState([]);
    const [rooms, setRooms] = useState([]);

    const navigate = useNavigate();

    useEffect(() => {
        const fetchData = async () => {
            try {
                const activityResponse = await axios.get('http://localhost:3333/activity/get');
                setActivities(activityResponse.data);

                const professorResponse = await axios.get('http://localhost:3333/professor/get');
                setProfessors(professorResponse.data);

                const roomResponse = await axios.get('http://localhost:3333/room/get');
                setRooms(roomResponse.data);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        fetchData();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();

        const lessonData = {
            name: lessonName,
            time: lessonTime,
            activity: activityName,
            professor: professorName,
            roomName: roomName,
            startDate: lessonStartDate,
            endDate: isRecurring ? endDate : undefined
        };

        try {
            const endpoint = isRecurring ? 'http://localhost:3333/lesson/addConcurrent' : 'http://localhost:3333/lesson/addSingle';
            const response = await axios.post(endpoint, lessonData);
            console.log(response.data);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error(error.response.data, error);
        }
    };

    const generateHourOptions = () => {
        let options = [];
        for (let i = 8; i <= 21; i++) {
            options.push(`${i.toString().padStart(2, '0')}:00`);
        }
        return options;
    };


    return (
        <div className='create-activity-container'>
            <div className="create-activity-header">
                <div className="create-activity-title">
                    <div className="text">Add Lesson</div>
                </div>
                <div className="logo">
                    <img src={logo} alt="logo"/>
                </div>
            </div>
            <form onSubmit={handleSubmit}>
                <input type='text' value={lessonName} onChange={(e) => setLessonName(e.target.value)}
                       placeholder='Lesson Name' required/>
                <select value={lessonTime} onChange={(e) => setLessonTime(e.target.value)} required>
                    <option value="">Select Lesson Time</option>
                    {generateHourOptions().map((hour, index) => (
                        <option key={index} value={hour}>{hour}</option>
                    ))}
                </select> {/* Aquí falta la etiqueta de cierre */}
                <select value={activityName} onChange={(e) => setActivityName(e.target.value)} required>
                    <option value="">Select Activity</option>
                    {activities.map((activity, index) => (
                        <option key={index} value={activity}>{activity}</option>
                    ))}
                </select> {/* Aquí falta la etiqueta de cierre */}
                <select value={professorName} onChange={(e) => setProfessorName(e.target.value)} required>
                    <option value="">Select Professor</option>
                    {professors.map((professor, index) => (
                        <option key={index} value={professor}>{professor}</option>
                    ))}
                </select>
                <select value={roomName} onChange={(e) => setRoomName(e.target.value)} required>
                    <option value="">Select Room</option>
                    {rooms.map((room, index) => (
                        <option key={index} value={room}>{room}</option>
                    ))}
                </select>
                <input type='date' value={lessonStartDate} onChange={(e) => setLessonStartDate(e.target.value)}
                       placeholder='Lesson Start Date' required/>
                {isRecurring && (
                    <input type='date' value={endDate} onChange={(e) => setEndDate(e.target.value)}
                           placeholder='End Date' required/>
                )}
                <label>
                    Recurring Lesson:
                    <input type="checkbox" checked={isRecurring} onChange={() => setIsRecurring(!isRecurring)}/>
                </label>
                <div className='form-actions'>
                    <button type='submit'>Add</button>
                    <button type='button' onClick={() => navigate('/AdministratorHome/ManageSchedule')}>Cancel</button>
                </div>
            </form>
        </div>
    );
}

export default LessonAddition;
