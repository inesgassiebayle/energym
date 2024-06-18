import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import CreateReviewModal from "./CreateReviewModal";
import ShowReviewsModal from "./ShowReviewsModal";
import Booking from "./Booking";
import DeleteBooking from "./DeleteBooking";
import ModifyReviewModal from "./ModifyReviewModal";
import ProfessorViewModal from "./ProfessorViewModal";

const Modal = ({ lessonId, closeModal }) => {
    const { username } = useParams();

    const [lessonName, setLessonName] = useState('');
    const [lessonDate, setLessonDate] = useState('');
    const [lessonTime, setLessonTime] = useState('');
    const [lessonRoom, setLessonRoom] = useState('');
    const [lessonActivity, setLessonActivity] = useState('');
    const [lessonProfessor, setLessonProfessor] = useState('');
    const [error, setError] = useState('');
    const [showModifyReviewModal, setShowModifyReviewModal] = useState(false);

    const [concurrency, setConcurrency] = useState(false);
    const [day, setDay] = useState('');
    const [startDay, setStartDay] = useState('');
    const [endDay, setEndDay] = useState('');

    const [oldBookedClasses, setOldBookedClasses] = useState(false);
    const [oldNotBookedClasses, setOldNotBookedClasses] = useState(false);
    const [futureBookedClasses, setFutureBookedClasses] = useState(false);
    const [futureFullClasses, setFutureFullClasses] = useState(false);
    const [futureAvailableClasses, setFutureAvailableClasses] = useState(false);
    const [oldReviewedClasses, setOldReviewedClasses] = useState(false);

    const [showCreateReviewModal, setShowCreateReviewModal] = useState(false);
    const [showMoreInfoModal, setShowMoreInfoModal] = useState(false);

    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState('');
    const [showBooking, setShowBooking] = useState(false);
    const [showBookingDelete, setShowBookingDeletion] = useState(false);
    const [reviewId, setReviewId] = useState(0);
    const [showReviews , setShowReviews] = useState(false)


    const classifyClass = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/lesson/classify/${lessonId}/${username}`);
            switch (response.data) {
                case "Future class booked":
                    setFutureBookedClasses(true);
                    break;
                case "Future class full":
                    setFutureFullClasses(true);
                    break;
                case "Future class available":
                    setFutureAvailableClasses(true);
                    break;
                case "Past class booked":
                    setOldBookedClasses(true);
                    break;
                case "Past class not booked":
                    setOldNotBookedClasses(true);
                    break;
                case "Past class booked and reviewed":
                    setOldReviewedClasses(true);
                    break;
                default:
                    setError('Unexpected response');
            }
        } catch (error) {
            setError(`Error: ${error.response ? error.response.data : error.message}`);
        }
    };

    const openCreateReviewModal = () => {
        setShowCreateReviewModal(true);
    }

    const openReviewsModal = () => {
        setShowReviews(true);
    }

    const openBookingDelete = () => {
        checkBookingConcurrency();
        setShowBookingDeletion(true);
    }

    const checkConcurrency = async () => {
        try {
            const response = await axios.get('http://localhost:3333/lesson/concurrent', {
                params: {
                    professor: lessonProfessor,
                    date: lessonDate,
                    time: lessonTime
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

    const checkBookingConcurrency = async () => {
        try {
            const response = await axios.get('http://localhost:3333/booking/concurrent', {
                params: {
                    professor: lessonProfessor,
                    date: lessonDate,
                    time: lessonTime,
                    student: username
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

    const openBooking = () => {
        checkConcurrency();
        setShowBooking(true);
    }

    const closeReviewView = () => {
        setShowCreateReviewModal(false);
        closeModal();
    }

    function handleRating (e) {
        setRating(e);
    }

    function handleComment (e) {
        setComment(e);
    }

    const openMoreInfoModal = () => {
        setShowMoreInfoModal(true);
    }

    const getLesson = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/getLessonById/${lessonId}`);
            const lesson = response.data;

            setLessonName(lesson.name);
            setLessonDate(lesson.date);
            setLessonTime(lesson.time);
            setLessonRoom(lesson.room);
            setLessonActivity(lesson.activity);
            setLessonProfessor(lesson.professor);

        } catch (error) {
            setError(`Error: ${error.response ? error.response.data : error.message}`);
        }
    };

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
            setRating(Number(response.data.rating));
            setComment(response.data.comment);
            setReviewId(Number(response.data.reviewId));

        } catch (error) {
            console.error('Error fetching review:', error);
        }
    }

    const openModifyReview =  () => {
        fetchOldReview();
        setShowModifyReviewModal(true);
    }

    const closeBooking = () => {
        setShowBooking(false);
        closeModal();
    }

    const closeModifyReview = () => {
        setShowModifyReviewModal(false);
        closeModal();
    }

    const closeBookingDelete = () => {
        setShowBookingDeletion(false);
        closeModal();
    }

    useEffect(() => {
        if (lessonId) {
            classifyClass();
            getLesson();
        }
    }, [lessonId]);

    return (
        <>
            <div className="overlay" onClick={closeModal}></div>
            <div className="modal">
                <div className="modal-header">
                    <button onClick={closeModal} className="modal-close-button">&times;</button>
                </div>
                <div className="modal-body">
                    <p><strong>Lesson Name:</strong> {lessonName}</p>
                    <p><strong>Date:</strong> {lessonDate}</p>
                    <p><strong>Time:</strong> {lessonTime}</p>
                    <p><strong>Room:</strong> {lessonRoom}</p>
                    <p><strong>Activity:</strong> {lessonActivity}</p>
                    <p><strong>Professor:</strong> {lessonProfessor}</p>
                    {oldBookedClasses && (
                        <button className='home-components-modification-button'
                                onClick={() => openCreateReviewModal()}>Create Review</button>
                    )}
                    {oldNotBookedClasses && (
                        <button className='home-components-modification-button' onClick={() => openReviewsModal()}>View
                            Review</button>
                    )}
                    {futureBookedClasses && (
                        <button className='home-components-modification-button'
                                onClick={() => openBookingDelete()}>Cancel booking</button>
                    )}
                    {futureFullClasses && (
                        <span className='future-full'>Full capacity</span>
                    )}
                    {futureAvailableClasses && (
                        <button className='home-components-modification-button' onClick={() => openBooking()}>Book
                            lesson</button>
                    )}
                    {oldReviewedClasses && (
                        <button className='home-components-modification-button'
                                onClick={() => openModifyReview()}>Modify Review</button>
                    )}
                    <button className='home-components-modification-button'
                            onClick={() => openMoreInfoModal()}>More about {lessonProfessor}
                    </button>

                </div>
                <div className="modal-footer">
                    <button className="cancel" onClick={closeModal}>Close</button>
                </div>
            </div>

            <CreateReviewModal
                isOpen={showCreateReviewModal}
                onClose={closeReviewView}
                username ={username}
                lessonName={lessonName}
                lessonProfessor={lessonProfessor}
                lessonTime={lessonTime}
                lessonDate={lessonDate}
            />

            <ShowReviewsModal
                isOpen={showReviews}
                onClose={() => setShowReviews(false)}
                date={lessonDate}
                time={lessonTime}
                username={lessonProfessor}
                id={lessonId}
                lessonName={lessonName}
            />
            <Booking
                isOpen={showBooking}
                onClose={closeBooking}
                username ={username}
                lessonName={lessonName}
                lessonProfessor={lessonProfessor}
                lessonTime={lessonTime}
                lessonDate={lessonDate}
                concurrency={concurrency}
                day={day}
                startDay={startDay}
                endDay={endDay}
            />
            <DeleteBooking
                isOpen={showBookingDelete}
                onClose={closeBookingDelete}
                username ={username}
                lessonName={lessonName}
                lessonProfessor={lessonProfessor}
                lessonTime={lessonTime}
                lessonDate={lessonDate}
                concurrency={concurrency}
                day={day}
                startDay={startDay}
                endDay={endDay}
            />
            <ModifyReviewModal
                isOpen={showModifyReviewModal}
                onClose={closeModifyReview}
                username ={username}
                lessonName={lessonName}
                reviewId = {reviewId}
                rating = {rating}
                comment = {comment}
                handleRating={handleRating}
                handleComment={handleComment}
            />

            <ProfessorViewModal
                isOpen={showMoreInfoModal}
                onClose={() => setShowMoreInfoModal(false)}
                date={lessonDate}
                time={lessonTime}
                username={lessonProfessor}
            />


        </>

    );
};

export default Modal;
