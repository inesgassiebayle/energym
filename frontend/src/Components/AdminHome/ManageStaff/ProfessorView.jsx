import React, {useEffect, useState} from 'react';
import {Link, useParams} from 'react-router-dom';
import logo from '../../Assets/Logo.png';
import axios from "axios";
import MoreModal from "./MoreModal";
import authentication from "../Hoc/Hoc";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import AddLessonModal from "../ManageSchelude/AddLessonModal";
import Modal from "../ManageSchelude/Modal";

const ProfessorView = () => {
    const { trainer } = useParams();
    const [createModalIsOpen, setCreateModalIsOpen] = useState(false);
    const [modalIsOpen, setModalIsOpen] = useState(false);
    const [selectedDate, setSelectedDate] = useState('');
    const [selectedTime, setSelectedTime] = useState('');
    const [events, setEvents] = useState([]);
    const [lessonId, setLessonId] = useState(null);

    const fetchLessons = async () => {
        try {

            const response = await axios.get(`http://localhost:3333/professor/getAllLessons/${trainer}`);
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

    const handleEventClick = (clickInfo) => {
        setLessonId(clickInfo.event.id);
        setModalIsOpen(true);
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
                    eventClick={handleEventClick}
                />
                {modalIsOpen && (
                    <MoreModal
                        isOpen={modalIsOpen}
                        closeModal={closeModalEvent}
                        lessonId={lessonId}
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

export default authentication(ProfessorView);
