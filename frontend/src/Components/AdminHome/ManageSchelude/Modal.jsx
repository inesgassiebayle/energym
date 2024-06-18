import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import ModifyLessonModal from "./ModifyLessonModal";
import star from "../../Assets/star2.png";
import spinner from "../../Assets/spinner.svg";

const Modal = ({ isOpen, closeModal, lessonId, updateEvent, removeEvent }) => {
    const { username } = useParams();

    const [showModifyModal, setShowModifyModal] = useState(false);
    const [confirmDelete, setConfirmDelete] = useState(false);

    const [loadingReviews, setLoadingReviews] = useState(false);
    const [reviews, setReviews] = useState([]);

    const [lessonName, setLessonName] = useState('');
    const [lessonDate, setLessonDate] = useState('');
    const [lessonTime, setLessonTime] = useState('');
    const [lessonRoom, setLessonRoom] = useState('');
    const [lessonActivity, setLessonActivity] = useState('');
    const [lessonProfessor, setLessonProfessor] = useState('');
    const [error, setError] = useState('');
    const [showReviews, setShowReviews] = useState(false);
    const [alterLesson, setAlterLesson] = useState(false);

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

    const classifyLesson = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/compare-date/${lessonId}`);
            const classification = response.data;

            if (classification === "Past") {
                setShowReviews(true);
            }

            if (classification === "Future") {
                setAlterLesson(true);
            }
        }
        catch (error) {
            setError(`Error: ${error.response ? error.response.data : error.message}`);
        }
    };

    const confirmDeleteHandler = async () => {
        const lessonData = {
            username: lessonProfessor,
            startDate: lessonDate,
            time: lessonTime
        };

        try {
            await axios.delete('http://localhost:3333/lesson', { data: lessonData });
            removeEvent(lessonId); // Elimina el evento del calendario
            setConfirmDelete(false);
            closeModal();
        } catch (error) {
            console.error('Error:', error.response.data);
        }
    };

    useEffect(() => {
        if (lessonId) {
            getLesson();
            classifyLesson();
            fetchReviews();
        }
    }, [lessonId]);

    const lessonData = {
        id: lessonId,
        name: lessonName,
        date: lessonDate,
        time: lessonTime,
        room: lessonRoom,
        activity: lessonActivity,
        professor: lessonProfessor
    };

    const fetchReviews = async () => {
        try {
            setLoadingReviews(true)
            const response = await axios.get('http://localhost:3333/lesson/reviews', {
                params: {
                    lessonId: lessonId
                }
            });

            const parsedReviews = response.data.map(review => ({
                ...review,
                rating: parseInt(review.rating, 10)
            }));

            console.log(parsedReviews);
            setReviews(parsedReviews);

        } catch (error) {
            console.error('Error fetching lesson reviews:', error.response ? error.response.data : error.message);
        }  finally {
            setLoadingReviews(false);
        }
    };

    const ReviewSquare = ({ review }) => (
        <div className="review-square">
            <h4>{review.username}</h4>
            <p>{review.comment}</p>
            <div style={{display: 'flex', alignItems: 'center'}}>
                {Number.isInteger(review.rating) && (
                    <p>
                        {Array.from({length: review.rating}).map((_, index) => (
                            <img key={index} src={star} alt="rating"/>
                        ))}
                    </p>
                )}
            </div>
        </div>
    );

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
                    {alterLesson && (
                        <>
                            <button className="home-components-modification-button"
                                    onClick={() => setConfirmDelete(true)}>Delete
                            </button>
                            <button className='home-components-modification-button'
                                    onClick={() => setShowModifyModal(true)}>Modify
                            </button>
                        </>
                    )}
                    {showReviews && (<div className="reviews-container">
                        <p>Class reviews: </p>
                        {loadingReviews ? (
                            <img src={spinner} alt="Loading..." style={{width: '50px'}}/>
                        ) : (
                            reviews.length > 0 ? (
                                <ul>
                                    {reviews.map((review, index) => (
                                        <ReviewSquare key={index} review={review}/>
                                    ))}
                                </ul>
                            ) : (
                                <p>No reviews for the selected class.</p>
                            )
                        )}
                    </div>)}
                </div>
                <div className="modal-footer">
                    <button className="cancel" onClick={closeModal}>Close</button>
                </div>
                {confirmDelete && (
                    <div className="modal">
                        <p>Are you sure you want to delete the lesson {lessonName}?</p>
                        <div className="modal-footer">
                            <button onClick={confirmDeleteHandler} className="modal-button cancel">Yes</button>
                            <button onClick={() => setConfirmDelete(false)} className="modal-button">No</button>
                        </div>
                    </div>
                )}
                {showModifyModal && (
                    <ModifyLessonModal
                        isOpen={showModifyModal}
                        onClose={() => setShowModifyModal(false)}
                        lesson={lessonData}
                        date={lessonDate}
                        onSave={() => {
                            setShowModifyModal(false);
                            closeModal();
                        }}
                    />
                )}
            </div>
        </>
    );
};

export default Modal;
