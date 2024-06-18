import React, {useCallback, useEffect, useState} from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import ClassInfoModal from "./ClassInfoModal";
import ReviewsModal from "./ReviewsModal";
import Assistance from "./Assistance";
import StudentsModal from "./StudentsModal";

const Modal = ({ lessonId, closeModal }) => {
    const { username } = useParams();

    const [error, setError] = useState('');

    const [showMoreModal, setShowMoreModal] = useState(false);
    const [showReviewsModal, setShowReviewModal] = useState(false);
    const [showAssistanceModal, setShowAssistanceModal] = useState(false);
    const [showStudentsModal, setShowStudentsModal] = useState(false);

    const [lessonName, setLessonName] = useState('');
    const [lessonDate, setLessonDate] = useState('');
    const [lessonTime, setLessonTime] = useState('');
    const [lessonRoom, setLessonRoom] = useState('');
    const [lessonActivity, setLessonActivity] = useState('');
    const [lessonProfessor, setLessonProfessor] = useState('');

    const [oldClass, setOldClass] = useState(false);
    const [futureClass, setFutureClass] = useState(false);
    const [presentClass, setPresentClass] = useState(false);

    const classifyClass = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/compare-date/${lessonId}`);
            if (response.data === "Past") {
                setOldClass(true);
            }
            if (response.data === "Present") {
                setPresentClass(true);
            }
            if (response.data === "Future") {
                setFutureClass(true);
            }
        } catch (error) {
            console.error('Error comparing date:', error);
        }
    };

    const getLesson = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/getLessonById/${lessonId}`);
            const lesson = response.data;
            console.log(lesson)

            // Actualiza los estados con los datos de la lección
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

    const openMoreModal = useCallback(() => {
        setShowMoreModal(true);
    }, []);

    const openReviewsModal = () => {
        setShowReviewModal(true);
    }

    const openAssistanceModal = () => {
        setShowAssistanceModal(true);
    }

    const openStudentsModal = () => {
        setShowStudentsModal(true);
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

                    {oldClass && (
                        <button className='home-components-modification-button'
                                onClick={() => openReviewsModal()}>Reviews</button>
                    )}
                    {presentClass && (
                        <button className='home-components-modification-button'
                                onClick={() => openAssistanceModal()}>Assistance
                        </button>
                    )}
                    {futureClass && (
                        <button className='home-components-modification-button'
                                onClick={() => openStudentsModal()}>More</button>
                    )}

                </div>
                <div className="modal-footer">
                    <button className="cancel" onClick={closeModal}>Close</button>
                </div>
            </div>

            <ClassInfoModal
                isOpen={showMoreModal}
                onClose={() => setShowMoreModal(false)}
                lessonName={lessonName}
                date={lessonDate}
                time={lessonTime}
                username ={username}
            />

            <ReviewsModal
                isOpen={showReviewsModal}
                onClose={() => setShowReviewModal(false)}
                lessonName={lessonName}
                lessonId={lessonId}
            />

            <Assistance
                isOpen={showAssistanceModal}
                onClose={() => setShowAssistanceModal(false)}
                lessonName={lessonName}
                date={lessonDate}
                time={lessonTime}
                username ={username}
            />

            <StudentsModal
                isOpen={showStudentsModal}
                onClose={() => setShowStudentsModal(false)}
                lessonName={lessonName}
                date={lessonDate}
                time={lessonTime}
                username ={username}
            />

        </>

    );
};

export default Modal;
