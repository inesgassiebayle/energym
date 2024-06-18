import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './AddLesson.css'

const AddLessonModal = ({ isOpen, onClose, selectedDate, selectedTime, addEvent }) => {
    const [lessonName, setLessonName] = useState('');
    const [lessonTime, setLessonTime] = useState(selectedTime || '');
    const [activityName, setActivityName] = useState('');
    const [professorName, setProfessorName] = useState('');
    const [roomName, setRoomName] = useState('');
    const [lessonStartDate, setLessonStartDate] = useState(selectedDate || '');
    const [isRecurring, setIsRecurring] = useState(false);
    const [endDate, setEndDate] = useState('');
    const [activities, setActivities] = useState([]);
    const [professors, setProfessors] = useState([]);
    const [rooms, setRooms] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        console.log(selectedDate);
        console.log(selectedTime);
        const fetchData = async () => {
            try {
                const activityResponse = await axios.get('http://localhost:3333/activity/get');
                setActivities(activityResponse.data);
                console.log(activityResponse.data)

                const professorResponse = await axios.get('http://localhost:3333/professor/get');
                setProfessors(professorResponse.data);
                console.log(professorResponse.data)

                const roomResponse = await axios.get('http://localhost:3333/room/get');
                setRooms(roomResponse.data);
                console.log(roomResponse.data)
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        fetchData();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();

        const lessonData = {
            name: lessonName,
            time: lessonTime,
            activity: activityName,
            professor: professorName,
            roomName: roomName,
            startDate: lessonStartDate,
            endDate: isRecurring ? endDate : undefined
        };

        try {
            const endpoint = isRecurring ? 'http://localhost:3333/lesson/addConcurrent' : 'http://localhost:3333/lesson/addSingle';
            await axios.post(endpoint, lessonData);
            if (isRecurring) {
                addRecurringEvents(lessonData);
            } else {
                addEvent(lessonData); // Añade el nuevo evento al calendario
            }
            onClose();
        } catch (error) {
            const errorMsg = error.response?.data || 'An unexpected error occurred.';
            console.error('Error while sending request:', errorMsg);
            setError(errorMsg);
        }
    };

    const addRecurringEvents = (lessonData) => {
        const startDate = new Date(lessonData.startDate);
        const endDate = new Date(lessonData.endDate);
        let currentDate = new Date(startDate);

        while (currentDate <= endDate) {
            const event = {
                id: `${lessonData.name}-${currentDate.toISOString()}`,
                title: lessonData.name,
                start: `${currentDate.toISOString().split('T')[0]}T${lessonData.time}:00`,
                description: `Profesor: ${lessonData.professor}, Sala: ${lessonData.room}, Actividad: ${lessonData.activity}`,
            };
            addEvent(event);

            // Incrementar la fecha en 7 días para la siguiente ocurrencia
            currentDate.setDate(currentDate.getDate() + 7);
        }
    };

    const generateHourOptions = () => {
        let options = [];
        for (let i = 8; i <= 21; i++) {
            options.push(`${i.toString().padStart(2, '0')}:00`);
        }
        return options;
    };

    return (
        <div className={`modal ${isOpen ? 'open' : ''}`}>
            <div className="modal-header">
                <h2>Add Lesson</h2>
            </div>
                <form onSubmit={handleSubmit}>
                    <input
                        type='text'
                        value={lessonName}
                        onChange={(e) => setLessonName(e.target.value)}
                        placeholder='Lesson Name'
                        required
                    />
                    <select
                        value={lessonTime}
                        onChange={(e) => setLessonTime(e.target.value)}
                        required
                    >
                        <option value="">Select Lesson Time</option>
                        {generateHourOptions().map((hour, index) => (
                            <option key={index} value={hour}>{hour}</option>
                        ))}
                    </select>
                    <select
                        value={activityName}
                        onChange={(e) => setActivityName(e.target.value)}
                        required
                    >
                        <option value="">Select Activity</option>
                        {activities.map((activity, index) => (
                            <option key={index} value={activity}>{activity}</option>
                        ))}
                    </select>
                    <select
                        value={professorName}
                        onChange={(e) => setProfessorName(e.target.value)}
                        required
                    >
                        <option value="">Select Professor</option>
                        {professors.map((professor, index) => (
                            <option key={index} value={professor}>{professor}</option>
                        ))}
                    </select>
                    <select
                        value={roomName}
                        onChange={(e) => setRoomName(e.target.value)}
                        required
                    >
                        <option value="">Select Room</option>
                        {rooms.map((room, index) => (
                            <option key={index} value={room}>{room}</option>
                        ))}
                    </select>
                    <input
                        type='date'
                        value={lessonStartDate}
                        onChange={(e) => setLessonStartDate(e.target.value)}
                        placeholder='Lesson Start Date'
                        required
                    />
                    {isRecurring && (
                        <input
                            type='date'
                            value={endDate}
                            onChange={(e) => setEndDate(e.target.value)}
                            placeholder='End Date'
                            required
                        />
                    )}
                    <label>
                        Recurring Lesson:
                        <input
                            type="checkbox"
                            checked={isRecurring}
                            onChange={() => setIsRecurring(!isRecurring)}
                        />
                    </label>
                    {error && <div className="error-message">{error}</div>}
                    <div className='form-actions'>
                        <button type='submit'>Add</button>
                        <button type='button' onClick={onClose}>Cancel</button>
                    </div>
                </form>
        </div>
    );
};

export default AddLessonModal;
