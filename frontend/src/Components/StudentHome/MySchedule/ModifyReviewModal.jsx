import React from 'react';
import axios from 'axios';
import './Booking.css';

const ModifyReviewModal = ({isOpen, onClose, username, lessonName, reviewId, rating, comment,  handleRating, handleComment}) => {
    if (!isOpen) return null;
    const handleDelete = async () => {
        try {
            const response = await axios.delete('http://localhost:3333/review', {
                params: {
                    id: reviewId,
                    student: username
                }
            });
            console.log(response.data);
            handleClose();
        } catch (error) {
            console.error('Error creating review:', error);
        }
    };


    const handleSubmit = async () => {
        try {
            const response = await axios.patch('http://localhost:3333/review', {
                comment: comment,
                rating: rating,
                id: reviewId
            });
            console.log(response.data);
            handleClose();
        } catch (error) {
            console.error('Error creating review:', error);
        }
    };

    const handleClose = () => {
        onClose();
    }

    const handleRatingChange = (e) => {
        handleRating(Number(e.target.value));
    }

    return (
        <div className="modal">
            <div className="modal-header">
                <h5 className="modal-title">Create Review for {lessonName}</h5>
                <button onClick={handleClose} className="modal-close-button">&times;</button>
            </div>
            <div className="modal-body">
                <div>
                    <label htmlFor="rating">Rating</label>
                    <select id="rating" value={rating} onChange={handleRatingChange}>{[0, 1, 2, 3, 4, 5].map(r => (
                        <option key={r} value={r}>{r}</option>))}</select>
                </div>
                <div>
                    <label htmlFor="comment">Comment</label>
                    <textarea id="comment" value={comment} onChange={e => handleComment(e.target.value)}/>
                </div>
            </div>
            <div className="modal-footer">
                <button onClick={handleDelete} className="modal-button delete">Delete</button>
                <button onClick={handleSubmit} className="modal-button submit">Submit</button>
            </div>
        </div>
    )
};
export default ModifyReviewModal;