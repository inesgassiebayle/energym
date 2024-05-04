import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './MySchedule.css';
import {Link, useNavigate, useParams} from 'react-router-dom';
import ClassInfoModal from "./ClassInfoModal";
import authentication from "../Common/Hoc/Authentication";


const MySchedule = () => {
    const { username } = useParams();
    const [selectedDate, setSelectedDate] = useState('');
    const [error, setError] = useState('');
    const [lessons, setLessons] = useState([]);
    const [selectedLesson, setSelectedLesson] = useState('');
    const [showModifyModal, setShowModifyModal] = useState(false);
    const [selectedLessonDate, setSelectedLessonDate] = useState('');
    const [selectedLessonTime, setSelectedLessonTime] = useState('');
    const [pastLessons, setPastLessons] = useState([]);
    const [presentLessons, setPresentLessons] = useState([]);
    const [futureLessons, setFutureLessons] = useState([]);



    const handleDateChange = (e) => {
        const selectDate = e.target.value;
        setSelectedDate(selectDate);
    };

    const handleInformation = (lesson) => {
        setSelectedLesson(lesson.name);
        console.log(lesson.name);
        setSelectedLessonDate(lesson.startDate);
        console.log(lesson.startDate);
        setSelectedLessonTime(lesson.time);
        console.log(lesson.time);
        setShowModifyModal(true);
    }

    useEffect(() => {
        if (selectedDate) {
            const fetchClassesForSelectedDate = async () => {
                try {
                    const response = await axios.get('http://localhost:3333/professor/lessons', {
                        params: {
                            username: username,
                            date: selectedDate
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
        console.log('Viewing details for trainer:', username);
    }, [username, selectedDate]);

    useEffect(() => {
        classifyClasses(lessons);
    }, [lessons]);

    const classifyClasses = async (classes) => {
        var oldClasses = [];
        var futureClasses = [];
        var presentClasses = [];

        for (let cls of classes) {
            try {
                const response = await axios.get('http://localhost:3333/compare-date', {
                    params: {
                        date: cls.startDate,
                        time: cls.time
                    }
                });
                if (response.data === "Past") {
                    oldClasses.push(cls);
                }
                if (response.data === "Present") {
                    presentClasses.push(cls);
                }
                if (response.data === "Future") {
                    futureClasses.push(cls);
                }
            } catch (error) {
                console.error('Error comparing date:', error);
            }
        }
        setPastLessons(oldClasses);
        setPresentLessons(presentClasses);
        setFutureLessons(futureClasses);
    };


    return (
        <div className ="my-schedule-container">
            <h2>My Schedule</h2>
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
                    {lessons.length > 0 ? (
                        <ul>
                            {pastLessons.map((classInfo, index) => (
                                <div key={index} className='staff-item'>
                                    <span>{classInfo.name} at {classInfo.time}</span>
                                    <button className='more' onClick={() => handleInformation(classInfo)}>More</button>
                                    <button className='more' onClick={() => handleInformation(classInfo)}>Reviews</button>
                                </div>
                            ))}
                            {presentLessons.map((classInfo, index) => (
                                <div key={index} className='staff-item'>
                                    <span>{classInfo.name} at {classInfo.time}</span>
                                    <button className='more' onClick={() => handleInformation(classInfo)}>More</button>
                                    <button className='more' onClick={() => handleInformation(classInfo)}>Assistence</button>
                                </div>
                            ))}
                            {futureLessons.map((classInfo, index) => (
                                <div key={index} className='staff-item'>
                                    <span>{classInfo.name} at {classInfo.time}</span>
                                    <button className='more' onClick={() => handleInformation(classInfo)}>More</button>
                                </div>
                            ))}
                        </ul>
                    ) : (
                        <p>No hay clases planeadas para el día seleccionado.</p>
                    )}
                </div>
            )}

            <Link to={`/trainer/${username}`}>
                <button className='staff-button back'>Home</button>
            </Link>

            <ClassInfoModal
                isOpen={showModifyModal}
                onClose={() => setShowModifyModal(false)}
                lessonName={selectedLesson}
                date={selectedLessonDate}
                time={selectedLessonTime}
                username ={username}
            />
        </div>
    );
};

export default authentication(MySchedule);
