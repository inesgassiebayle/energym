import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './AddLesson.css';
import logo from "../../../Assets/Logo.png";
import axios from "axios";

const LessonAddition = () => {
    const [lessonName, setLessonName] = useState('');
    const [lessonTime, setLessonTime] = useState('');
    const [activityName, setActivityName] = useState('');
    const [professorName, setProfessorName] = useState('');
    const [roomName, setRoomName] = useState('');  // Estado para el nombre de la sala
    const [lessonStartDate, setLessonStartDate] = useState('');
    const [isRecurring, setIsRecurring] = useState(false);
    const [endDate, setEndDate] = useState('');

    let navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();  // Evitar el envío automático del formulario

        if(isRecurring){
            try {
                const response = await axios.post('http://localhost:3333/lesson/addConcurrent', {
                    name: lessonName,
                    time: lessonTime,
                    activity: activityName,
                    professor: professorName,
                    roomName: roomName,
                    startDate: lessonStartDate,
                    endDate: endDate
                });
                console.log(response.data);
                navigate('/AdministratorHome');
            } catch (error) {
                console.error('Error sending request:', error);
            }
        }
        else {
            try {
                const response = await axios.post('http://localhost:3333/lesson/addSingle', {
                    name: lessonName,
                    time: lessonTime,
                    activity: activityName,
                    professor: professorName,
                    roomName: roomName,
                    startDate: lessonStartDate
                });
                console.log(response.data);
                navigate('/AdministratorHome');
            } catch (error) {
                console.error('Error sending request:', error);
            }
        }
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
                <input type='time' value={lessonTime} onChange={(e) => setLessonTime(e.target.value)}
                       placeholder='Lesson Time' required/>
                <input type='text' value={activityName} onChange={(e) => setActivityName(e.target.value)}
                       placeholder='Activity Name' required/>
                <input type='text' value={professorName} onChange={(e) => setProfessorName(e.target.value)}
                       placeholder='Professor Name' required/>
                <input type='text' value={roomName} onChange={(e) => setRoomName(e.target.value)}
                       placeholder='Room Name' required/>
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




