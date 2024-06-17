import React, { useEffect, useState } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import axios from 'axios';
import './Calendar.css';
import { Link, useParams } from 'react-router-dom';
import Modal from './Modal';

const Calendar = () => {
    const { username } = useParams();
    const [events, setEvents] = useState([]);
    const [modalIsOpen, setModalIsOpen] = useState(false);
    const [selectedLessonId, setSelectedLessonId] = useState(null);

    const fetchLessons = async () => {
        try {
            const response = await axios.get('http://localhost:3333/lessons');
            const lessons = response.data;

            const studentResponse = await axios.get(`http://localhost:3333/student/lessons/${username}`);
            const studentLessons = studentResponse.data;

            const studentLessonIds = new Set(studentLessons.map(lesson => lesson.id));

            const formattedEvents = lessons.map(lesson => {
                const isStudentLesson = studentLessonIds.has(lesson.id);
                return {
                    id: lesson.id,
                    title: lesson.name,
                    start: `${lesson.date}T${lesson.time}:00`,
                    description: `Profesor: ${lesson.professor}, Sala: ${lesson.room}, Actividad: ${lesson.activity}`,
                    classNames: isStudentLesson ? 'red' : 'blue',
                };
            });

            setEvents(formattedEvents);
        } catch (error) {
            console.error('Error al obtener las lecciones:', error);
        }
    };

    useEffect(() => {
        fetchLessons();
    }, [username]);

    const handleEventClick = (clickInfo) => {
        setSelectedLessonId(clickInfo.event.id);
        setModalIsOpen(true);
    };

    const closeModal = () => {
        setModalIsOpen(false);
        setSelectedLessonId(null);
        fetchLessons();
    };

    return (
        <div className="my-schedule-container">
            <h2>Manage your schedule</h2>
            <div className="calendar-container">
                <FullCalendar
                    plugins={[dayGridPlugin, timeGridPlugin]}
                    initialView="dayGridMonth"
                    headerToolbar={{
                        left: 'prev,next today',
                        center: 'title',
                        right: 'dayGridMonth,timeGridWeek,timeGridDay'
                    }}
                    events={events}
                    eventTimeFormat={{
                        hour: '2-digit',
                        minute: '2-digit',
                        meridiem: false
                    }}
                    eventContent={renderEventContent}
                    eventClick={handleEventClick}
                />

                {modalIsOpen && (
                    <Modal
                        lessonId={selectedLessonId}
                        closeModal={closeModal}
                    />
                )}
            </div>
            <Link to={`/student/${username}`}>
                <button className='staff-button back'>Home</button>
            </Link>
        </div>
    );
};

const renderEventContent = (eventInfo) => (
    <div style={{ display: 'flex', alignItems: 'center', color: eventInfo.event.extendedProps.textColor }}>
        <span className="fc-event-time" style={{ marginRight: '2px' }}>{eventInfo.timeText}</span>
        <span className="event-title">{eventInfo.event.title}</span>
    </div>
);

export default Calendar;
