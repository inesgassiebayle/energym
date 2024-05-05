import React, { useEffect, useState } from 'react';
import axios from "axios";
import star from '../../Assets/star2.png';
import {useNavigate} from "react-router-dom";
import spinner from "../../Assets/spinning-loading.gif";

const ClassInfoModal = ({ isOpen, onClose, lessonName, date, time, username}) => {
    let navigate = useNavigate(); // Added useNavigate hook
    const [reviews, setReviews] = useState([]);
    const [loadingReviews, setLoadingReviews] = useState(false);


    const fetchReviews = async () => {
        try {
            setLoadingReviews(true)
            const response = await axios.get('http://localhost:3333/lesson/reviews', {
                params: {
                    username: username,
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

        fetchReviews();
    }, [isOpen, navigate]);

    if (!isOpen) return null;

    return (
        <div className="modalStaff" tabIndex="-1" role="dialog">
            <div className="modal-header">
                <h5 className="modal-title">Reviews for "{lessonName}"</h5>
            </div>
            <div className="modal-body">
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