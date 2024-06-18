import React, { useEffect, useState } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';
import '../../Calendar.css';
import AddLessonModal from './AddLessonModal';
import axios from "axios";
import Modal from "./Modal";
import { Link } from "react-router-dom";

const Calendar = () => {
    const [createModalIsOpen, setCreateModalIsOpen] = useState(false);
    const [modalIsOpen, setModalIsOpen] = useState(false);
    const [selectedDate, setSelectedDate] = useState('');
    const [selectedTime, setSelectedTime] = useState('');
    const [events, setEvents] = useState([]);
    const [lessonId, setLessonId] = useState(null);

    const handleDateClick = (info) => {
        setSelectedDate(info.dateStr);
        setSelectedTime('');
        setCreateModalIsOpen(true);
    };

    const handleSelect = (info) => {
        if (!info.startStr) {
            console.error("info.startStr is undefined");
            return;
        }

        const dateTime = info.startStr;
        const [date, timeWithTimezone] = dateTime.split('T');
        const time = timeWithTimezone ? timeWithTimezone.slice(0, 5) : '';

        setSelectedDate(date);
        setSelectedTime(time);
        setCreateModalIsOpen(true);
    };

    const fetchLessons = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/lessons`);
            const lessons = response.data;

            const formattedEvents = lessons.map(lesson => ({
                id: lesson.id,
                title: lesson.name,
                start: `${lesson.date}T${lesson.time}:00`,
                description: `Profesor: ${lesson.professor}, Sala: ${lesson.room}, Actividad: ${lesson.activity}`,
                classNames: 'blue'
            }));

            setEvents(formattedEvents);
        } catch (error) {
            console.error('Error al obtener las lecciones:', error);
        }
    };

    const closeModalEvent = () => {
        fetchLessons();
       setModalIsOpen(false);
    }

    useEffect(() => {
        fetchLessons();
    }, []);

    const addNewEvent = (lesson) => {
        const newEvent = {
            id: lesson.id,
            title: lesson.name,
            start: `${lesson.startDate}T${lesson.time}:00`,
            description: `Profesor: ${lesson.professor}, Sala: ${lesson.room}, Actividad: ${lesson.activity}`,
            classNames: 'blue'
        };
        setEvents(prevEvents => [...prevEvents, newEvent]);
        fetchLessons();
    };

    const handleEventClick = (clickInfo) => {
        setLessonId(clickInfo.event.id);
        setModalIsOpen(true);
    };

    const updateEvent = (updatedLesson) => {
        setEvents(prevEvents =>
            prevEvents.map(event =>
                event.id === updatedLesson.id
                    ? {
                        ...event,
                        title: updatedLesson.name,
                        start: `${updatedLesson.date}T${updatedLesson.time}:00`,
                        description: `Profesor: ${updatedLesson.professor}, Sala: ${updatedLesson.room}, Actividad: ${updatedLesson.activity}`
                    }
                    : event
            )
        );
    };

    const removeEvent = (lessonId) => {
        setEvents(prevEvents => prevEvents.filter(event => event.id !== lessonId));
    };

    return (
        <div className="my-schedule-container">
            <h2>Manage schedule</h2>
            <div className="calendar-container">
                <FullCalendar
                    plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
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
                    selectable={true}
                    selectMirror={true}
                    dateClick={handleDateClick}
                    select={handleSelect}
                    eventClick={handleEventClick}
                />
                {createModalIsOpen && (
                    <AddLessonModal
                        isOpen={createModalIsOpen}
                        onClose={() => setCreateModalIsOpen(false)}
                        selectedDate={selectedDate}
                        selectedTime={selectedTime}
                        addEvent={addNewEvent}
                    />
                )}
                {modalIsOpen && (
                    <Modal
                        isOpen={modalIsOpen}
                        closeModal={closeModalEvent}
                        lessonId={lessonId}
                        updateEvent={updateEvent}  // Pasa la función de actualización de evento
                        removeEvent={removeEvent}  // Pasa la función de eliminación de evento
                    />
                )}
            </div>
            <Link to={`/AdministratorHome`}>
                <button className='staff-button back'>Home</button>
            </Link>
        </div>
    );
};

const renderEventContent = (eventInfo) => (
    <div style={{display: 'flex', alignItems: 'center', color: eventInfo.event.extendedProps.textColor}}>
        <span className="fc-event-time" style={{marginRight: '2px' }}>{eventInfo.timeText}</span>
        <span className="event-title">{eventInfo.event.title}</span>
    </div>
);

export default Calendar;
