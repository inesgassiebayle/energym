import './ProfessorView.css';
import React, {useEffect, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import logo from '../../Assets/Logo.png';
import axios from "axios";
import MoreModal from "./MoreModal";
import authentication from "../Hoc/Hoc";

const ProfessorView = () => {
    const { trainer } = useParams();
    let navigate = useNavigate();
    const [error, setError] = useState(null);
    const [lessons, setLessons] = useState([]);
    const [fullname, setFullname] = useState({firstName: '', lastName: ''});
    const [selectedLesson, setSelectedLesson] = useState('');
    const [showModifyModal, setShowModifyModal] = useState(false);
    const [selectedLessonDate, setSelectedLessonDate] = useState('');
    const [selectedLessonTime, setSelectedLessonTime] = useState('');
    const [selectedDate, setSelectedDate] = useState('');
    const [selectedLessonId, setSelectedLessonId] = useState('');
    const [lessonName, setLessonName] = useState('');
    const [lessonRoom, setLessonRoom] = useState('');
    const [lessonActivity, setLessonActivity] = useState('');

    const trainerFullname = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/professor/${trainer}/fullname`);
            setFullname(response.data);
        } catch (error){
            console.error('Failed to fetch fullname:', error);
        }
    }

    const handleDateChange = (e) => {
        setSelectedDate(e.target.value);
    };

    const handleInformation = (lesson) => {
        setSelectedLessonDate(lesson.date);
        setSelectedLessonTime(lesson.time);
        setSelectedLessonId(lesson.id);
        setLessonName(lesson.name);
        setLessonActivity(lesson.activity);
        setLessonRoom(lesson.room);
        console.log(lesson);
        setShowModifyModal(true);
    };

    useEffect(() => {
        trainerFullname();

        if (selectedDate) {
            const fetchClassesForSelectedDate = async () => {
                try {
                    const response = await axios.get('http://localhost:3333/professor/lessons', {
                        params: {
                            date: selectedDate,
                            username: trainer
                        }
                    });
                    setLessons(response.data);
                } catch (error) {
                    console.error('Error fetching classes:', error);
                    setError('Failed to fetch classes.');
                }
            };
            fetchClassesForSelectedDate();
        }

        console.log('Viewing details for trainer:', trainer);
    }, [trainer, selectedDate]);


    return (
        <div className='manage-staff-container'>
            <div className="manage-staff-header">
                <div className="manage-staff-title">
                    <div className="text">Trainer: {fullname.firstName} {fullname.lastName}</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className="date-picker">
                <label htmlFor="date">Professor classes on:</label>
                <input
                    type="date"
                    id="date"
                    name="date"
                    value={selectedDate}
                    onChange={handleDateChange}
                />
            </div>
            {error && <p className="error-message">{error}</p>}
            {selectedDate && (
                <div className="schedule-info">
                    <h3>Classes for {selectedDate}:</h3>
                    {lessons.length > 0 ? (
                        <ul>
                            {lessons.map((classInfo, index) => (
                                <div key={index} className='staff-item'>
                                    <span>{classInfo.name} by {trainer} at {classInfo.time}</span>
                                    <button className='more' onClick={() => handleInformation(classInfo)}>More</button>
                                </div>
                            ))}

                        </ul>
                    ) : (
                        <p>No classes planned for the selected day.</p>
                    )}
                </div>
            )}
            <div className='staff-actions'>
                <Link to={"/AdministratorHome"}>
                    <button className='staff-button back'>Home</button>
                </Link>
            </div>
            <MoreModal
                isOpen={showModifyModal}
                onClose={() => setShowModifyModal(false)}
                lessonId={selectedLessonId}
                lessonDate={selectedLessonDate}
                lessonTime={selectedLessonTime}
                lessonName={lessonName}
                lessonRoom={lessonRoom}
                lessonActivity={lessonActivity}
            />
        </div>
    );
};

export default authentication(ProfessorView);
