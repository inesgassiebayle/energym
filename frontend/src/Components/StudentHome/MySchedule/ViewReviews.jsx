import React from 'react';
import axios from 'axios';
import './Booking.css';

const ModifyReviewModal = ({isOpen, onClose, lessonId, lessonName}) => {
    if (!isOpen) return null;
    const [loadingReviews, setLoadingReviews] = useState(false);



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

    const handleClose = () => {
        onClose();
    }

    return (
        <div className="modal">
            <div className="modal-header">
                <h5 className="modal-title">View Reviews for {lessonName}</h5>
                <button onClick={handleClose} className="modal-close-button">&times;</button>
            </div>
            <div className="modal-body">

            </div>
        </div>
    )
};

export default ModifyReviewModal;