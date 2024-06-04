import React, { useEffect, useState } from 'react';
import axios from "axios";
import star from '../../Assets/star2.png';
import {useNavigate} from "react-router-dom";
import spinner from "../../Assets/spinner.svg";
import authentication from "../Common/Hoc/Authentication";

const ProfessorViewModal = ({ isOpen, onClose, date, time, username}) => {
    let navigate = useNavigate(); // Added useNavigate hook
    const [reviews, setReviews] = useState([]);
    const [averageRating, setAverageRating] = useState(0);
    const [loadingReviews, setLoadingReviews] = useState(false);
    const [loadingAverageRating, setLoadingAverageRating] = useState(false);
    const [professorRating, setProfessorRating] = useState(0);
    const [activity, setActivity] = useState(''); // Added activity state

    const handlePClose = () => {
        onClose();
    };

    const fetchProfessorRating = async () => {
        try {
            setLoadingAverageRating(true);
            const response = await axios.get('http://localhost:3333/professor/lessons2', {
                params: { username: username }
            });
            setProfessorRating(response.data.average);
            console.log(response.data.average);
        } catch (error) {
            console.error('Error fetching classes:', error);
        }finally {
            setLoadingAverageRating(false);
        }
    };

    const fetchActivity = async () => {
        try {
            setLoadingReviews(true);
            const response = await axios.get('http://localhost:3333/lesson/activity', {
                params: {
                    username: username,
                    startDate: date,
                    time: time,
                }
            });
            if (response.status === 200) {
                setActivity(response.data.name);
                console.log(response.data.name);
            } else {
                console.error('Activity not found or error occurred');
                setActivity('No activity found');
            }
        } catch (error) {
            console.error('Error fetching activity:', error);
            setActivity('No activity found');
        } finally {
            setLoadingReviews(false);
        }
    };

    const fetchReviews = async () => {
        try {
            setLoadingReviews(true)
            console.log(username);
            console.log(activity);
            const response = await axios.get('http://localhost:3333/lesson/reviewsByActivity', {
                params: {
                    username: username,
                    activity: activity,
                }
            });
            const parsedReviews = response.data.map(review => ({
                ...review,
                rating: parseInt(review.rating, 10)
            }));
            setReviews(parsedReviews);

        } catch (error) {
            console.error('Error fetching lesson reviews:', error.response ? error.response.data : error.message);
        }  finally {
            setLoadingReviews(false);
        }
    };

    useEffect(() => {
        if (isOpen) {
            fetchProfessorRating();
            fetchActivity();
        }
    }, [isOpen, username]);


    useEffect(() => {
        if (activity && activity !== 'No activity found') {
            fetchReviews();
        }
    }, [activity, username]);

    if (!isOpen) return null;

    return (
            <div className="modal large">
                <div className="modal-header">
                    <h2 className="modal-title">Average Rating of {username}: {loadingAverageRating ? (
                        <img src={spinner} alt="Loading..." style={{width: '50px'}}/>
                    ) : (
                        professorRating > 0 ? professorRating.toFixed(1) : "No ratings yet."
                    )}</h2>
                    <button onClick={onClose} className="modal-close-button">&times;</button>
                </div>
                <div className="modal-subtitle">Reviews for "{activity}"</div>
                <div className="modal-body">
                    <div className="reviews-container">
                        {loadingReviews ? (
                            <img src={spinner} alt="Loading..." style={{width: '50px'}}/>
                        ) : (
                            reviews.length > 0 ? (
                                reviews.map((review, index) => (
                                    <div key={index} className="review-square">
                                        <h4>{review.username}</h4>
                                        <p>Lesson: {review.lessonName} on {review.lessonDate} at {review.lessonTime}</p>
                                        <p>Comment: {review.comment}</p>
                                        <div style={{display: 'flex', alignItems: 'center'}}>
                                            {Number.isInteger(review.rating) && Array.from({length: review.rating}).map((_, i) => (
                                                <img key={i} src={star} alt="rating"/>
                                            ))}
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <p>No reviews for the selected activity : {activity}.</p>
                            )
                        )}
                    </div>
                </div>
                <div className="modal-footer">
                    <button className="cancel" onClick={handlePClose}>Close</button>
                </div>
            </div>
    );
};

export default authentication(ProfessorViewModal);
