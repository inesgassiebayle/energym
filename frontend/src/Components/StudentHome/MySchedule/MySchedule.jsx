import React, {useEffect, useState} from 'react';
import {Link, useParams} from 'react-router-dom';
import authentication from "../Common/Hoc/Authentication";
import axios from 'axios';
import CreateReviewModal from "./CreateReviewModal";
import ModifyReviewModal from "./ModifyReviewModal";
import Booking from "./Booking";


const StudentSchedule = () => {
    const { username } = useParams();
    const [selectedDate, setSelectedDate] = useState('');
    const [lessons, setLessons] = useState([]);
    const [loading, setLoading] = useState(false);
    const [oldBookedClasses, setOldBookedClasses] = useState([]);
    const [oldNotBookedClasses, setOldNotBookedClasses] = useState([]);
    const [futureBookedClasses, setFutureBookedClasses] = useState([]);
    const [futureFullClasses, setFutureFullClasses] = useState([]);
    const [futureAvailableClasses, setFutureAvailableClasses] = useState([]);
    const [oldReviewedClasses, setOldReviewedClasses] = useState([]);
    const [showCreateReviewModal, setShowCreateReviewModal] = useState(false);
    const [lessonName, setLessonName] = useState('');
    const [lessonProfessor, setLessonProfessor] = useState('');
    const [lessonTime, setLessonTime] = useState('');
    const [lessonDate, setLessonDate] = useState('');
    const [showModifyReviewModal, setShowModifyReviewModal] = useState(false);
    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState('');
    const [showBooking, setShowBooking] = useState(false);
    const [concurrency, setConcurrency] = useState(false);
    const [day, setDay] = useState('');
    const [startDay, setStartDay] = useState('');
    const [endDay, setEndDay] = useState('');
    const handleDateChange = (e) => {
        const selectDate = e.target.value;
        setSelectedDate(selectDate);
    };

    const checkConcurrency = async (lesson) => {
        try {
            const response = await axios.get('http://localhost:3333/lesson/concurrent', {
                params: {
                    professor: lesson.professor,
                    date: selectedDate,
                    time: lesson.time
                }
            });
            if (response.data === 'Not concurrent lessons') {
                setConcurrency(false);
            } else {
                setConcurrency(true);
                setDay(response.data.day);
                setStartDay(response.data.startDay);
                setEndDay(response.data.endDay);
            }
        } catch (error) {
            console.error('Error checking concurrency:', error);
        }
    };

    const deleteBooking = async (lesson) => {
        try {
            const response =  await axios.delete('http://localhost:3333/student/booking', {
                params: {
                    professor: lesson.professor,
                    student: username,
                    startDate: lesson.startDate,
                    endDate: lesson.startDate,
                    time: lesson.time
                }
            });
            console.log(response.data);
            await fetchLessons();
            classifyClasses(lessons);
        }
        catch (error) {
            console.error('Error booking lesson:', error);
        }
    };

    const openCreateReviewModal = (lesson) => {
        handleInformation(lesson);
        fetchOldReview();
        setShowCreateReviewModal(true);
    }

    const openModifyReview =  (lesson) => {
        handleInformation(lesson);
        setShowModifyReviewModal(true);
    }

    const openBooking = (lesson) => {
        handleInformation(lesson);
        checkConcurrency(lesson);
        setShowBooking(true);
    }

    const handleBookingClosing = () => {
        setShowBooking(false);
        fetchLessons();
        classifyClasses(lessons);
    }

    const fetchOldReview = async () => {
        try {
            const response = await axios.get('http://localhost:3333/review', {
                params: {
                    username: username,
                    professor: lessonProfessor,
                    time: lessonTime,
                    date: lessonDate
                }
            });
            console.log(response.data);
            setRating(response.data.rating);
            setComment(response.data.comment);
        } catch (error) {
            console.error('Error fetching review:', error);
        }
    }

    const handleInformation = (lesson) => {
        setLessonName(lesson.name);
        setLessonProfessor(lesson.professor);
        setLessonTime(lesson.time);
        setLessonDate(selectedDate);
    }

    const fetchLessons = async () => {
        try {
            setLoading(true);
            const response = await axios.get('http://localhost:3333/lesson', {
                params : {
                    startDate: selectedDate
                }
            });
            console.log(response.data);
            setLessons(response.data);
        } catch (error) {
            console.error('Failed to fetch lessons:', error);
        }finally {
            setLoading(false);
        }
    }

    const classifyClasses = async (classes) => {
        var oldBookedClasses = [];
        var oldNotBookedClasses = [];
        var futureBookedClasses = [];
        var futureFullClasses = [];
        var futureAvailableClasses = [];
        var oldReviewedClasses = [];

        for (let cls of classes) {
            try {
                const response = await axios.get('http://localhost:3333/student/classify-lessons', {
                    params: {
                        username: username,
                        date: cls.startDate,
                        time: cls.time,
                        professor: cls.professor
                    }
                });
                if (response.data === "Future class booked") {
                    futureBookedClasses.push(cls);
                }
                if (response.data === "Future class full") {
                    futureFullClasses.push(cls);
                }
                if (response.data === "Future class available") {
                    futureAvailableClasses.push(cls);
                }
                if (response.data === "Past class booked") {
                    oldBookedClasses.push(cls);
                }
                if (response.data === "Past class not booked") {
                    oldNotBookedClasses.push(cls);
                }
                if (response.data === "Past class booked and reviewed") {
                    oldReviewedClasses.push(cls);
                }
            } catch (error) {
                console.error('Error comparing date:', error);
            }
        }
        setFutureAvailableClasses(futureAvailableClasses);
        setFutureFullClasses(futureFullClasses);
        setFutureBookedClasses(futureBookedClasses);
        setOldNotBookedClasses(oldNotBookedClasses);
        setOldBookedClasses(oldBookedClasses);
        setOldReviewedClasses(oldReviewedClasses);
    };

    useEffect(() => {
        if (selectedDate) {
            fetchLessons();
            console.log('Viewing lessons on date:', selectedDate);
        }
    }, [selectedDate]);

    useEffect(() => {
        classifyClasses(lessons);
    }, [lessons]);

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
                    {lessons.length > 0 ? (
                        <ul>
                            {lessons.sort((a, b) => a.time.localeCompare(b.time)).map((classInfo, index) => (
                                <div key={index} className='staff-item'>
                                    <span>{classInfo.name} at {classInfo.time}</span>
                                    {oldBookedClasses.includes(classInfo) &&
                                        <button className='more' onClick={() => openCreateReviewModal(classInfo)}>Create Review</button>}
                                    {oldNotBookedClasses.includes(classInfo) && <span className='past-not-booked'>Not Booked</span>}
                                    {futureBookedClasses.includes(classInfo) &&
                                        <button className='more' onClick={() => deleteBooking(classInfo)}>Cancel booking</button>}
                                    {futureFullClasses.includes(classInfo) && <span className='future-full'>Full capacity</span>}
                                    {futureAvailableClasses.includes(classInfo) &&
                                        <button className='more' onClick={() => openBooking(classInfo)}>Book lesson</button>}
                                    {oldReviewedClasses.includes(classInfo) &&
                                        <button className='more' onClick={() => openModifyReview(classInfo)}>Modify Review</button>}
                                </div>
                            ))}
                        </ul> ) : (<p>No available lessons on this date</p> )}
                </div>
            )}

            <Link to={`/student/${username}`}>
                <button className='staff-button back'>Home</button>
            </Link>
            <CreateReviewModal
                isOpen={showCreateReviewModal}
                onClose={() => setShowCreateReviewModal(false)}
                username ={username}
                lessonName={lessonName}
                lessonProfessor={lessonProfessor}
                lessonTime={lessonTime}
                lessonDate={selectedDate}
            />
            <Booking
                isOpen={showBooking}
                onClose={() => setShowBooking(false)}
                username ={username}
                lessonName={lessonName}
                lessonProfessor={lessonProfessor}
                lessonTime={lessonTime}
                lessonDate={selectedDate}
                concurrency={concurrency}
                day={day}
                startDay={startDay}
                endDay={endDay}
            />
        </div>
    );
};

export default authentication(StudentSchedule);
