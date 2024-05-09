import React, { useEffect, useState } from 'react';
import axios from "axios";
import star from '../../Assets/star2.png';
import {useNavigate} from "react-router-dom";
import spinner from "../../Assets/spinning-loading.gif";
import './Booking.css';
import ModifyReviewModal from "./ModifyReviewModal";
import ProfessorViewModal from "./ProfessorViewModal";

const InfoForStudentModal = ({ isOpen, onClose, lessonName, date, time, username}) => {
    let navigate = useNavigate(); // Added useNavigate hook
    const [room, setRoom] = useState('');
    const [activity, setActivity] = useState('');
    const [loadingLesson, setLoadingLesson] = useState(false);
    const [showProfessorModal, setShowProfessorModal] = useState(false);

    const handleClose = () => {
        onClose();
    };
    const handleProfessorClick = () => {
        setShowProfessorModal(true);
    }
    const closeProfessorModal = () => {
        setShowProfessorModal(false);
    }

    const fetchLesson = async () => {
        try {
            setLoadingLesson(true)
            const response = await axios.get('http://localhost:3333/lesson', {
                params: {
                    username: username,
                    startDate: date,
                    time: time
                }
            })

            console.log(response.data);
            setRoom(response.data.room);
            setActivity(response.data.activity);

        } catch (error) {
            console.error('Error fetching lesson details:', error);
        } finally {
            setLoadingLesson(false);
        }
    };

    useEffect(() => {
        if (!isOpen) return;

        fetchLesson();
    }, [isOpen, navigate]);

    if (!isOpen) return null;

    return (
        <div className="modalStaff" tabIndex="-1" role="dialog">
            <div className="modal-header">
                <h5 className="modal-title">Details for "{lessonName}"</h5>
            </div>
            <div className="modal-body">
                <p>Start Date: {date}</p>
                <p>Time: {time}</p>
                <p>Professor: <span onClick={handleProfessorClick} className="clickable-text">{username}</span></p>
                <p>Room: {loadingLesson ? <img src={spinner} alt="Loading..." style={{width: '50px'}}/> : room}</p>
                <p>Activity: {loadingLesson ? <img src={spinner} alt="Loading..." style={{width: '50px'}}/> : activity}</p>
            </div>
            <div className="modal-footer">
                <button className="cancel" onClick={handleClose}>Close</button>
            </div>
            <ProfessorViewModal
                isOpen={showProfessorModal}
                onClose={closeProfessorModal}
                lessonName={lessonName}
                date={date}
                time={time}
                username={username}
            />
        </div>
    );
};

export default InfoForStudentModal;