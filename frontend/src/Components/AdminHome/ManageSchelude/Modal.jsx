import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import ModifyLessonModal from "./ModifyLessonModal";

const Modal = ({ isOpen, closeModal, lessonId, updateEvent, removeEvent }) => {
    const { username } = useParams();

    const [showModifyModal, setShowModifyModal] = useState(false);
    const [confirmDelete, setConfirmDelete] = useState(false);

    const [lessonName, setLessonName] = useState('');
    const [lessonDate, setLessonDate] = useState('');
    const [lessonTime, setLessonTime] = useState('');
    const [lessonRoom, setLessonRoom] = useState('');
    const [lessonActivity, setLessonActivity] = useState('');
    const [lessonProfessor, setLessonProfessor] = useState('');
    const [error, setError] = useState('');

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

    return (
        <>
            <div className="overlay" onClick={closeModal}></div>
            <div className="modal">
                <div className="modal-header">
                    <button onClick={closeModal} className="modal-close-button">&times;</button>
                </div>
                <div className="modal-body">
                    <button className="home-components-modification-button"
                            onClick={() => setConfirmDelete(true)}>Delete
                    </button>
                    <button className='home-components-modification-button' onClick={() => setShowModifyModal(true)}>View
                    </button>
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
                        onClose={closeModal}
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
