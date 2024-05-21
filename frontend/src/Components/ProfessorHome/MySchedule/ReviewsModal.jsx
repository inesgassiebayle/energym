import React, { useEffect, useState } from 'react';
import axios from "axios";
import star from '../../Assets/star2.png';
import {useNavigate} from "react-router-dom";
import spinner from "../../Assets/spinner.svg";

const ClassInfoModal = ({ isOpen, onClose, lessonName, lessonId}) => {
    let navigate = useNavigate(); // Added useNavigate hook
    const [reviews, setReviews] = useState([]);
    const [averageRating, setAverageRating] = useState(0);
    const [loadingReviews, setLoadingReviews] = useState(false);
    const [loadingAverageRating, setLoadingAverageRating] = useState(false);


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
            fetchAverageRating(parsedReviews);

        } catch (error) {
            console.error('Error fetching lesson reviews:', error.response ? error.response.data : error.message);
        }  finally {
            setLoadingReviews(false);
        }
    };

    const fetchAverageRating = async (reviews) => {
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
        if (!isOpen){
            resetState();
            return
        }
        fetchReviews();
    }, [isOpen, navigate]);

    useEffect(() => {
        return () => {
            setAverageRating(0);
        };
    }, [isOpen]);

    if (!isOpen) return null;

    return (
        <div className="modal">
            <div className="modal-header">
                <h5 className="modal-title">Reviews for {lessonName}</h5>
                <button onClick={onClose} className="modal-close-button">&times;</button>
            </div>
            <div className="modal-body">
                <p>Average Rating:{loadingAverageRating ? (
                    <img src={spinner} alt="Loading..." style={{width: '50px'}}/>
                ) : (reviews.length > 0 ? (averageRating) : <p></p>)}</p>
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
            <div className="modal-footer">
                <button className="cancel" onClick={onClose}>Close</button>
            </div>
        </div>

    );
};

export default ClassInfoModal;
