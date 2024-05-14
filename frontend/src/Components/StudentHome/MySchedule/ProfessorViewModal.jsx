import React, { useEffect, useState } from 'react';
import axios from "axios";
import star from '../../Assets/star2.png';
import {useNavigate} from "react-router-dom";
import spinner from "../../Assets/spinner.svg";
import './profView.css'

const ProfessorViewModal = ({ isOpen, onClose, lessonName, date, time, username}) => {
    let navigate = useNavigate(); // Added useNavigate hook
    const [reviews, setReviews] = useState([]);
    const [averageRating, setAverageRating] = useState(0);
    const [loadingReviews, setLoadingReviews] = useState(false);
    const [loadingAverageRating, setLoadingAverageRating] = useState(false);
    const [professorRating, setProfessorRating] = useState(0);
    const [initialLessons, setInitialLessons] = useState([]);
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
            const lessons = response.data;
            setInitialLessons(lessons);
        } catch (error) {
            console.error('Error fetching classes:', error);
        }finally {
            setLoadingAverageRating(false);
        }
    };

    const calculateProfessorRating = (lessons) => {
        console.log('initialLessons');
        let totalRating = 0;
        let count = 0;
        lessons.forEach(lesson => {
            console.log(lesson.review);
            totalRating += Number(lesson.review); // Convert review to number before adding
            count++;
        });
        if (count > 0) {
            setProfessorRating(totalRating / count);
        } else {
            setProfessorRating(0);  // En caso de que no haya reviews
        }

    }

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
            setActivity('No activity found');  // Set a default or error state for activity
        } finally {
            setLoadingReviews(false);
        }
    };

    const ReviewPSquare = ({ review }) => (
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
        if (initialLessons.length > 0) {
            calculateProfessorRating(initialLessons);
        }
    }, [initialLessons]);

    useEffect(() => {
        if (activity && activity !== 'No activity found') { // Ensuring activity is set and valid
            fetchReviews();
        }
    }, [activity, username]); // Added username in case it impacts fetched data

    if (!isOpen) return null;

    return (
        <div className="modalStaff" tabIndex="-1" role="dialog">
            <div className="modal-header">
                <h2 className="modal-title">Average Rating of {username}: {loadingAverageRating ? (
                    <img src={spinner} alt="Loading..." style={{width: '50px'}}/>
                ) : (
                    professorRating > 0 ? professorRating.toFixed(1) : "No ratings yet."
                )}</h2>
            </div>
            <div className="modal-subtitle">Reviews for "{activity}"</div>
            <div className="modal-body reviews-container">
                {loadingReviews ? (
                    <img src={spinner} alt="Loading..." style={{width: '50px' }} />
                ) : (
                    reviews.length > 0 ? (
                        reviews.map((review, index) => (
                            <div key={index} className="review-square">
                                <h4>{review.username}</h4>
                                <p>{review.comment}</p>
                                <div style={{ display: 'flex', alignItems: 'center' }}>
                                    {Number.isInteger(review.rating) && Array.from({ length: review.rating }).map((_, i) => (
                                        <img key={i} src={star} alt="rating" />
                                    ))}
                                </div>
                            </div>
                        ))
                    ) : (
                        <p>No reviews for the selected activity : {activity}.</p>
                    )
                )}
            </div>
            <div className="modal-footer">
                <button className="cancel" onClick={handlePClose}>Close</button>
            </div>
        </div>
    );
};

export default ProfessorViewModal;
