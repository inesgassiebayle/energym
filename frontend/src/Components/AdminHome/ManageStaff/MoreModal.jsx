import './MoreModal.css';
import React, { useEffect, useState } from 'react';
import axios from "axios";
import star from '../../Assets/star2.png';
import {useNavigate} from "react-router-dom";

const MoreModal = ({ isOpen, onClose, lesson, date }) => {
    let navigate = useNavigate(); // Added useNavigate hook
    const [room, setRoom] = useState('');
    const [professor, setProfessor] = useState('');
    const [time, setTime] =  useState('');
    const [reviews, setReviews] = useState([]);

    const fetchLesson = async () => {
        try {
            const response = await axios.post('http://localhost:3333/lesson/get', {
                    name: lesson,
                    startDate: date
            });
            console.log(response.data);
            setRoom(response.data.room);
            setTime(response.data.time);
            setProfessor(response.data.time);

        } catch (error) {
            console.error('Error fetching lesson details:', error);
        }
    };

    const fetchReviews = async () => {
        try {
            const response = await axios.post('http://localhost:3333/lesson/reviews', {
                    name: lesson,
                    startDate: date
            });

            const parsedReviews = response.data.map(review => ({
                ...review,
                rating: parseInt(review.rating, 10)
            }));

            console.log(parsedReviews);
            setReviews(parsedReviews);

        } catch (error) {
            console.error('Error fetching lesson reviews:', error.response ? error.response.data : error.message);
        }
    };

    const ReviewSquare = ({ review }) => (
        <div className="review-square">
            <h4>{review.username}</h4>
            <p>{review.comment}</p>
            {Number.isInteger(review.rating) && <p>Rating: {Array.from({length: review.rating}).map((_, index) => <img key={index} src={star} alt={"rating"}/>)}</p>}
        </div>
    );

    useEffect(() => {
        if (!isOpen) return;
        fetchLesson();
        fetchReviews();
    }, [isOpen, navigate]);

    if (!isOpen) return null;

    return (
        <div className="modal" tabIndex="-1" role="dialog">
            <div className="modal-dialog" role="document">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Details for "{lesson}"</h5> {/* Changed from Modify Room */}
                    </div>
                    <div className="modal-body">
                        <p>Start Date: {date}</p>
                        <p>Time: {time}</p>
                        <p>Room: {room}</p>
                        <p>Professor: {professor}</p>
                        <div className="reviews-container">
                            {reviews.map((review, index) => (
                                <ReviewSquare key={index} review={review}/>
                            ))}
                        </div>
                    </div>

                    <div className="modal-footer">
                        <button className="cancel" onClick={onClose}>Close</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MoreModal;
