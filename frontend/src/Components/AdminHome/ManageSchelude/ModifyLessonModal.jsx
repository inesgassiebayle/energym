import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './ModifyLessonModal.css';

const ModifyLessonModal = ({ isOpen, onClose, lesson, date , onSave }) => {
    const [name, setName] = useState('');
    const [time, setTime] = useState('');
    const [activity, setActivity] = useState('');
    const [professor, setProfessor] = useState('');
    const [roomName, setRoomName] = useState('');
    const [startDate, setStartDate] = useState('');

    const [oldName, setOldName] = useState('');
    const [oldTime, setOldTime] = useState('');
    const [oldActivity, setOldActivity] = useState('');
    const [oldProfessor, setOldProfessor] = useState('');
    const [oldRoomName, setOldRoom] = useState('');
    const [oldStartDate, setOldStartDate] = useState('');
    const fetchDetails = async () => {
        try {
            const response = await axios.post('http://localhost:3333/lesson/get',{
                name: oldName,
                startDate: date,
                time: oldTime
            });
            setOldActivity(response.data.activity);
            setActivity(response.data.activity);
            setOldProfessor(response.data.professor);
            setProfessor(response.data.professor);
            setOldRoom(response.data.room);
            setRoomName(response.data.room);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };

    useEffect(() => {
        if (isOpen && lesson) {
            setOldName(lesson.name);
            setOldTime(lesson.time);
            setOldStartDate(date);
            setName(lesson.name);
            setTime(lesson.time);
            setStartDate(date);
            fetchDetails();
        }
    }, [isOpen, lesson, date]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const updateData = {
            name, time, activity, professor, roomName, startDate
        };
        try {
            await axios.patch(, updateData);
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