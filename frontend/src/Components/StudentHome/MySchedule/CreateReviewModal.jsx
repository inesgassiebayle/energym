import React, {useState} from 'react';
import axios from 'axios';
import './Booking.css';

const CreateReviewModal = ({isOpen, onClose, username, lessonName, lessonProfessor, lessonTime, lessonDate}) => {
    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState('');

    const [assistanceError, setAssistanceError] = useState('');


    if (!isOpen) return null;

    const handleSubmit = async () => {
        try {
            const response = await axios.post('http://localhost:3333/review', {
                comment: comment,
                rating: rating,
                username: username,
                professor: lessonProfessor,
                lessonTime: lessonTime,
                lessonDate: lessonDate
            });
            console.log(response.data);
            handleClose();
        } catch (error) {
            const errorMsg = error.response?.data || 'An unexpected error occurred.';
            console.error('Error while sending request:', errorMsg);
            setAssistanceError('');
            if (errorMsg.includes("Cannot rate a class a student did not assist")) {
                setAssistanceError("Cannot rate a class if student did not assist");
            }
        }
    };

    const handleClose = () => {
        setRating(0);
        setComment('');
        onClose();
    }

    const handleRatingChange = (e) => {
        setRating(Number(e.target.value));
    }


    return (
        <div className="modal">
            <div className="modal-header">
                <h5 className="modal-title">Create Review for {lessonName}</h5>
            </div>
            <div className="modal-body">
                <div>
                    <label htmlFor="rating">Rating</label>
                    <select id="rating" value={rating} onChange={handleRatingChange}>{[0, 1, 2, 3, 4, 5].map(r => (
                        <option key={r} value={r}>{r}</option>))}</select>
                </div>
                <div>
                    <label htmlFor="comment">Comment</label>
                    <textarea id="comment" value={comment} onChange={e => setComment(e.target.value)}/>
                </div>
            </div>
            <div className="modal-footer">
                <button onClick={handleClose} className="modal-button cancel">Cancel</button>
                <button onClick={handleSubmit} className="modal-button submit">Submit</button>
                {assistanceError && <div className="error-message" style={{ color: 'red', textAlign: 'center' }}>{assistanceError}</div>}

            </div>
        </div>
    )
};
export default CreateReviewModal;