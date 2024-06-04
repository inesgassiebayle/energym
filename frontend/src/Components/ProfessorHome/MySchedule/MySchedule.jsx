import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import './MySchedule.css';
import {Link, useNavigate, useParams} from 'react-router-dom';
import ClassInfoModal from "./ClassInfoModal";
import authentication from "../Common/Hoc/Authentication";
import ReviewsModal from "./ReviewsModal";
import Assistance from "./Assistance";


const MySchedule = () => {
    const { username } = useParams();
    const [selectedDate, setSelectedDate] = useState('');
    const [error, setError] = useState('');
    const [lessons, setLessons] = useState([]);
    const [initialLessons, setInitialLessons] = useState([]);
    const [selectedLesson, setSelectedLesson] = useState('');
    const [showMoreModal, setShowMoreModal] = useState(false);
    const [showReviewsModal, setShowReviewModal] = useState(false);
    const [showAssistanceModal, setShowAssistanceModal] = useState(false);
    const [selectedLessonDate, setSelectedLessonDate] = useState('');
    const [selectedLessonTime, setSelectedLessonTime] = useState('');
    const [pastLessons, setPastLessons] = useState([]);
    const [presentLessons, setPresentLessons] = useState([]);
    const [futureLessons, setFutureLessons] = useState([]);
    const [initialPastLessons, setInitialPastLessons] = useState([]);
    const [initialPresentLessons, setInitialPresentLessons] = useState([]);
    const [initialFutureLessons, setInitialFutureLessons] = useState([]);
    const [selectedLessonId, setSelectedLessonId] = useState('');

    const handleDateChange = (e) => {
        const selectDate = e.target.value;
        setSelectedDate(selectDate);
    };

    const handleInformation = useCallback((lesson) => {
        setSelectedLesson(lesson.name);
        console.log(lesson.name);
        setSelectedLessonDate(lesson.date);
        console.log(lesson.date);
        setSelectedLessonTime(lesson.time);
        console.log(lesson.time);
        setSelectedLessonId(lesson.id);
        console.log(lesson.id);
    }, []);

    const openMoreModal = useCallback((lesson) => {
        handleInformation(lesson);
        setShowMoreModal(true);
    }, [handleInformation]);

    const openReviewsModal = (lesson) => {
        handleInformation(lesson);
        setShowReviewModal(true);
    }

    const openAssistanceModal = (lesson) => {
        handleInformation(lesson);
        setShowAssistanceModal(true);
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
                    response.data.forEach(lesson => {
                        console.log(lesson.name);
                        console.log(lesson.date);
                        console.log(lesson.id);
                    })
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
        if(!selectedDate){
            const fetchClasses = async () => {
                try {
                    const response = await axios.get('http://localhost:3333/professor/lessonsByUser', {
                        params: {
                            username: username
                        }
                    });
                    setInitialLessons(response.data);
                } catch (error) {
                    console.error('Error fetching classes:', error);
                    setError('Failed to fetch classes.');
                }
            };
            fetchClasses();
        }
    }, [username, selectedDate]);


    useEffect(() => {
        classifyClasses(lessons);
        classifyInitialClasses(initialLessons);

    }, [lessons, initialLessons]);

    const classifyClasses = async (classes) => {
        var oldClasses = [];
        var futureClasses = [];
        var presentClasses = [];

        for (let cls of classes) {
            try {
                const response = await axios.get('http://localhost:3333/compare-date', {
                    params: {
                        date: cls.date,
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

    const classifyInitialClasses = async (classes) => {
        var oldClasses = [];
        var futureClasses = [];
        var presentClasses = [];
        console.log(classes);
        for (let cls of classes) {
            try {
                const response = await axios.get('http://localhost:3333/compareInitialDate', {
                    params: {
                        date: cls.date,
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
        setInitialPastLessons(oldClasses);
        setInitialPresentLessons(presentClasses);
        setInitialFutureLessons(futureClasses);

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
            {!selectedDate ? (
                <>
                    <h3>Today's Classes:</h3>
                    {initialPresentLessons.length > 0 ? initialPresentLessons.map((classInfo, index) => (
                        <div key={index} className='staff-item'>
                            <span>{classInfo.name} at {classInfo.time}</span>
                            <button className='home-components-modification-button' onClick={() => openMoreModal(classInfo)}>More</button>
                            <button className='home-components-modification-button' onClick={() => openAssistanceModal(classInfo)}>Assistance</button>
                        </div>
                    )) : <p>No classes today.</p>}

                    <h3>Last Five Lessons:</h3>
                    {initialPastLessons.length > 0 ? initialPastLessons.slice(0, 5).map((classInfo, index) => (
                        <div key={index} className='staff-item'>
                            <span>{classInfo.name} at {classInfo.time}</span>
                            <button className='home-components-modification-button' onClick={() => openMoreModal(classInfo)}>More</button>
                            <button className='home-components-modification-button' onClick={() => openReviewsModal(classInfo)}>Reviews</button>
                        </div>
                    )) : <p>No past classes.</p>}

                    <h3>Next Five Lessons:</h3>
                    {initialFutureLessons.length > 0 ?  initialFutureLessons.slice(0, 5).map((classInfo, index) => (
                        <div key={index} className='staff-item'>
                            <span>{classInfo.name} at {classInfo.time}</span>
                            <button className='home-components-modification-button' onClick={() => openMoreModal(classInfo)}>More</button>
                        </div>
                    )) : <p>No future classes.</p>}
                </>
            ) : (
                <div className="schedule-info">
                    <h3>Classes for {selectedDate}:</h3>
                    {lessons.length > 0 ? (
                        <ul>
                            {pastLessons.map((classInfo, index) => (
                                <div key={index} className='staff-item'>
                                    <span>{classInfo.name} at {classInfo.time}</span>
                                    <button className='home-components-modification-button' onClick={() => openReviewsModal(classInfo)}>Reviews
                                    </button>
                                    <button className='home-components-modification-button' onClick={() => openMoreModal(classInfo)}>More</button>

                                </div>
                            ))}
                            {presentLessons.map((classInfo, index) => (
                                <div key={index} className='staff-item'>
                                    <span>{classInfo.name} at {classInfo.time}</span>
                                    <button className='home-components-modification-button' onClick={() => openAssistanceModal(classInfo)}>Assistance
                                    </button>
                                    <button className='home-components-modification-button' onClick={() => openMoreModal(classInfo)}>More</button>

                                </div>
                            ))}
                            {futureLessons.map((classInfo, index) => (
                                <div key={index} className='staff-item'>
                                    <span>{classInfo.name} at {classInfo.time} </span>
                                    <button className='home-components-modification-button' onClick={() => openMoreModal(classInfo)}>More</button>
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
                isOpen={showMoreModal}
                onClose={() => setShowMoreModal(false)}
                lessonName={selectedLesson}
                date={selectedLessonDate}
                time={selectedLessonTime}
                username ={username}
            />

            <ReviewsModal
                isOpen={showReviewsModal}
                onClose={() => setShowReviewModal(false)}
                lessonName={selectedLesson}
                lessonId={selectedLessonId}
            />

            <Assistance
                isOpen={showAssistanceModal}
                onClose={() => setShowAssistanceModal(false)}
                lessonName={selectedLesson}
                date={selectedLessonDate}
                time={selectedLessonTime}
                username ={username}
            />
        </div>
    );
};

export default authentication(MySchedule);
