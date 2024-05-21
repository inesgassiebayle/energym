import React, { useEffect, useState } from 'react';
import axios from "axios";
import star from '../../Assets/star2.png';
import {useNavigate} from "react-router-dom";
import authentication from "../Hoc/Hoc";
import spinner from "../../Assets/spinner.svg";
import "../../Reviews.css"
import "../../Modal.css"

const MoreModal = ({ isOpen, onClose, lessonId, lessonDate, lessonTime, lessonName, lessonRoom, lessonActivity}) => {
    let navigate = useNavigate();
    const [loadingReviews, setLoadingReviews] = useState(false);
    const [reviews, setReviews] = useState([]);


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

    useEffect(() => {
        if (!isOpen) return;
        fetchReviews();

    }, [isOpen, navigate]);

    if (!isOpen) return null;

    return (
        <div className="modal">
            <div className="modal-header">
                <h5 className="modal-title">Details for {lessonName}</h5>
                <button onClick={onClose} className="modal-close-button">&times;</button>
            </div>
            <div className="modal-body">
                <p>Start Date: {lessonDate}</p>
                <p>Time: {lessonTime}</p>
                <p>Room: {lessonRoom}</p>
                <p>Activity: {lessonActivity}</p>
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
                <button  type="button" className="cancel" onClick={onClose}>Close</button>
            </div>
        </div>
    );
};

export default authentication(MoreModal);
