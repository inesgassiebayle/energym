
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './ModifyLessonModal.css';

const ModifyLessonModal = ({ isOpen, onClose, lesson, onSave }) => {
    const [name, setName] = useState('');
    const [time, setTime] = useState('');
    const [activity, setActivity] = useState('');
    const [professor, setProfessor] = useState('');
    const [roomName, setRoomName] = useState('');
    const [startDate, setStartDate] = useState('');

    useEffect(() => {
        if (isOpen && lesson) {
            setName(lesson.name);
            setTime(lesson.time);
            setActivity(lesson.activity);
            setProfessor(lesson.professor);
            setRoomName(lesson.roomName);
            setStartDate(lesson.startDate);
        }
    }, [isOpen, lesson]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const updateData = {
            name, time, activity, professor, roomName, startDate
        };
        try {
            await axios.put(`http://localhost:3333/lesson/${lesson.id}/update`, updateData);
            onSave();
            onClose();
        } catch (error) {
            console.error('Error updating lesson:', error);
            alert('Failed to update lesson');
        }
    };

    if (!isOpen) return null;

    return (
        <div className="modify-lesson-modal">
            <form onSubmit={handleSubmit}>
                <input type="text" value={name} onChange={e => setName(e.target.value)} placeholder="Lesson Name" />
                <input type="text" value={time} onChange={e => setTime(e.target.value)} placeholder="Time (HH:mm:ss)" />
                <input type="text" value={activity} onChange={e => setActivity(e.target.value)} placeholder="Activity" />
                <input type="text" value={professor} onChange={e => setProfessor(e.target.value)} placeholder="Professor" />
                <input type="text" value={roomName} onChange={e => setRoomName(e.target.value)} placeholder="Room Name" />
                <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} placeholder="Start Date (YYYY-MM-DD)" />
                <button type="submit">Save Changes</button>
                <button type="button" onClick={onClose}>Cancel</button>
            </form>
        </div>
    );
};

export default ModifyLessonModal;

