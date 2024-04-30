import './MoreModal.css';
import React, { useEffect, useState } from 'react';
import axios from "axios";
import star from '../../Assets/star2.png';
import {useNavigate} from "react-router-dom";
import authentication from "../Hoc/Hoc";
import spinner from "../../Assets/spinning-loading.gif";

const MoreModal = ({ isOpen, onClose, trainer, date, time}) => {
    let navigate = useNavigate(); // Added useNavigate hook
    const [room, setRoom] = useState('');
    const [activity, setActivity] = useState('');
    const [reviews, setReviews] = useState([]);
    const [lesson, setName] = useState('');
    const [loadingLesson, setLoadingLesson] = useState(false);
    const [loadingReviews, setLoadingReviews] = useState(false);


    const fetchLesson = async () => {
        try {
            setLoadingLesson(true)
            const response = await axios.get('http://localhost:3333/lesson', {
                params: {
                    username: trainer,
                    startDate: date,
                    time: time
                }
            })

            console.log(response.data);
            setRoom(response.data.room);
            setActivity(response.data.activity);
            setName(response.data.name);

        } catch (error) {
            console.error('Error fetching lesson details:', error);
        } finally {
            setLoadingLesson(false);
        }
    };

    const fetchReviews = async () => {
        try {
            setLoadingReviews(true)
            const response = await axios.get('http://localhost:3333/lesson/reviews', {
                params: {
                    username: trainer,
                    startDate: date,
                    time: time
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

    useEffect(() => {
        if (!isOpen) return;

        fetchLesson();
        fetchReviews();
    }, [isOpen, navigate]);

    if (!isOpen) return null;

    return (
        <div className="modalStaff" tabIndex="-1" role="dialog">
                <div className="modal-header">
                        <h5 className="modal-title">Details for "{lesson}"</h5>
                    </div>
                    <div className="modal-body">
                        <p>Start Date: {date}</p>
                        <p>Time: {time}</p>
                        <p>Room: {loadingLesson ?
                            <img src={spinner} alt="Loading..." style={{width: '50px'}}/> : room}</p>
                        <p>Activity: {activity}</p>
                        <div className="reviews-container">
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
                        </div>
                    </div>

            <div className="modal-footer">
                <button className="cancel" onClick={onClose}>Close</button>
            </div>
        </div>
    );
};

export default authentication(MoreModal);
