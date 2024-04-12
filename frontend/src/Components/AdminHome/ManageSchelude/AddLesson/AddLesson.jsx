import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './AddLesson.css';
import logo from "../../../Assets/Logo.png";
import axios from "axios";


const LessonAddition = () => {
    const [lessonName, setLessonName] = useState('');
    const [lessonTime, setLessonTime] = useState('');
    const [activityName, setActivityName] = useState('');
    const [professorName, setProfessorName] = useState('');
    const [lessonStartDate, setLessonStartDate] = useState('');
    const [isRecurring, setIsRecurring] = useState(false);
    const [endDate, setEndDate] = useState('');


    let navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault(); // Evitar el envío automático del formulario
        try {
            const response = await axios.post('http://localhost:3333/lesson/add', {
                name: lessonName,
                time: lessonTime,
                activity: activityName,
                professor: professorName,
                startDate: lessonStartDate,
                endDate: endDate, // Nuevo campo endDate
                recurring: isRecurring
            });
            console.log(response.data);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error('Error al enviar solicitud:', error);
        }
    };

    return (
        <div className='create-activity-container'>
            <div className="create-activity-header">
                <div className="create-activity-title">
                    <div className="text">Add Lesson</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
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
                <input type='date' value={lessonStartDate} onChange={(e) => setLessonStartDate(e.target.value)}
                       placeholder='Lesson Start Date' required/>
                {/* Nuevo campo para End Date, visible solo si isRecurring es verdadero */}
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
