import React, {useState} from 'react';
import axios from 'axios';

const CreateReviewModal = ({isOpen, onClose, username, lessonName, lessonProfessor, lessonTime, lessonDate}) => {
    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState('');

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
            console.error('Error creating review:', error);
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
            <div className="header">
                <h5 className="title">Create Review for {lessonName}</h5>
            </div>
            <div>
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
            <div className="footer">
                <button onClick={handleClose}>Cancel</button>
                <button onClick={handleSubmit}>Submit</button>
            </div>
        </div>
    )
};
export default CreateReviewModal;