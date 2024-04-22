import './MoreModal.css';
import React, { useEffect, useState } from 'react';
import axios from "axios";
import star from '../../Assets/star2.png';
import {useNavigate} from "react-router-dom";
import authentication from "../Hoc/Hoc";

const MoreModal = ({ isOpen, onClose, trainer, date, time}) => {
    let navigate = useNavigate(); // Added useNavigate hook
    const [room, setRoom] = useState('');
    const [activity, setActivity] = useState('');
    const [reviews, setReviews] = useState([]);
    const [lesson, setName] = useState('');

    const fetchLesson = async () => {
        try {
            const response = await axios.post('http://localhost:3333/lesson/get', {
                    username: trainer,
                    startDate: date,
                    time: time
            });
            console.log(response.data);
            setRoom(response.data.room);
            setActivity(response.data.activity);
            setName(response.data.name);

        } catch (error) {
            console.error('Error fetching lesson details:', error);
        }
    };

    const fetchReviews = async () => {
        try {
            const response = await axios.post('http://localhost:3333/lesson/reviews', {
                    username: trainer,
                    startDate: date,
                    time: time
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
                        <h5 className="modal-title">Details for "{lesson}"</h5>
                    </div>
                    <div className="modal-body">
                        <p>Start Date: {date}</p>
                        <p>Time: {time}</p>
                        <p>Room: {room}</p>
                        <p>Activity: {activity}</p>
                        <div className="reviews-container">
                            <p>Class reviews: </p>
                            {reviews.length>0 ? (<ul>
                                {reviews.map((review, index) => (
                                    <ReviewSquare key={index} review={review}/>
                                ))}
                            </ul>
                            ) : (
                                <p>No reviews for the selected class.</p>
                            )}
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

export default authentication(MoreModal);
