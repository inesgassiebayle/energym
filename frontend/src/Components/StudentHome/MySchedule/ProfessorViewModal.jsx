import React, { useEffect, useState } from 'react';
import axios from "axios";
import star from '../../Assets/star2.png';
import {useNavigate} from "react-router-dom";
import spinner from "../../Assets/spinning-loading.gif";
import './Booking.css';

const ProfessorViewModal = ({ isOpen, onClose, lessonName, lessonId, username}) => {
    let navigate = useNavigate(); // Added useNavigate hook
    const [reviews, setReviews] = useState([]);
    const [averageRating, setAverageRating] = useState(0);
    const [loadingReviews, setLoadingReviews] = useState(false);
    const [loadingAverageRating, setLoadingAverageRating] = useState(false);
    const [professorRating, setProfessorRating] = useState(0);
    const [initialLessons, setInitialLessons] = useState([]);

    const handleClose = () => {
        onClose();
    };
    const fetchProfessorRating = async () => {
        try {
            const response = await axios.get('http://localhost:3333/professor/lessons2', {
                params: { username: username }
            });
            setInitialLessons(response.data);
        } catch (error) {
            console.error('Error fetching classes:', error);
        }
    };

    const calculateProfessorRating = (lessons) => {
        let totalRating = 0;
        lessons.forEach(lesson => {
            totalRating += lesson.rating;
        });
        setProfessorRating(totalRating / lessons.length);
    }

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

    const fetchAverageRating = async () => {
        setLoadingAverageRating(true);
        if (reviews.length === 0) {
            setAverageRating(0);
            setLoadingAverageRating(false);
            return;
        }
        const totalRating = reviews.reduce((sum, review) => sum + review.rating, 0);
        const averageRating = totalRating / reviews.length;
        setAverageRating(averageRating);
        setLoadingAverageRating(false);
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

    const resetState = () => {
        setAverageRating(0);
    };
    useEffect(() => {
        if (isOpen) {
            fetchProfessorRating();
        }
    }, [isOpen, username]);

    useEffect(() => {
        if (initialLessons.length > 0) {
            calculateProfessorRating(initialLessons);
        }
    }, [initialLessons]);

    useEffect(() => {
        if (!isOpen){
            resetState();
            return
        }
        fetchReviews();
        if (reviews.length > 0) {
            fetchAverageRating();
        }
    }, [isOpen, navigate]);

    if (!isOpen) return null;

    return (
        <div className="modal" tabIndex="-1" role="dialog">
            <div className="modal-header">
                <h2 className="modal-main-title">Average Rating of {username}: { reviews.length > 0 ? averageRating.toFixed(1) :
                    ( "No ratings yet." )}
                </h2>

            </div>
            <div
                className="modal-subtitle">Reviews for "{lessonName}"
            </div>
            <div className="modal-body">
                <p>Average Rating: {loadingAverageRating ? (
                    <img src={spinner} alt="Loading..." style={{width: '50px'}}/>
                ) : (reviews.length > 0 ? averageRating.toFixed(1) : "No ratings yet.")}</p>
                {loadingReviews ? (
                    <img src={spinner} alt="Loading..." style={{width: '50px'}}/>
                ) : (
                    reviews.length > 0 ? (
                        <ul>
                            {reviews.map((review, index) => (
                                <div className="review-square" key={index}>
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
                            ))}
                        </ul>
                    ) : (
                        <p>No reviews for the selected class.</p>
                    )
                )}
            </div>
            <div className="modal-footer">
                <button className="modal-close-button cancel" onClick={handleClose}>Close</button>
            </div>
        </div>
    );
};

export default ProfessorViewModal;
