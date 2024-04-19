import './ProfessorView.css';
import React, {useEffect, useState} from 'react';
import {Link, useNavigate, useParams} from 'react-router-dom';
import logo from '../../Assets/Logo.png';
import axios from "axios";
import MoreModal from "./MoreModal";
import authentication from "../Hoc/Hoc";

const ProfessorView = () => {
    const { trainer } = useParams();
    let navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [lessons, setLessons] = useState([]);
    const [fullname, setFullname] = useState({firstName: '', lastName: ''});
    const [selectedLesson, setSelectedLesson] = useState('');
    const [showModifyModal, setShowModifyModal] = useState(false);

    const trainerLessons = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/professor/${trainer}/lessons`);
            setLessons(response.data);
        } catch (error){
            console.error('Failed to fetch lessons:', error);
        }
    };

    const trainerFullname = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/professor/${trainer}/fullname`);
            setFullname(response.data); // Save the fullname in the state
        } catch (error){
            console.error('Failed to fetch fullname:', error);
        }
    }

    const handleInformation = (lesson) => {
        setSelectedLesson(lesson);
        setShowModifyModal(true);
    };

    useEffect(() => {
        trainerLessons();
        trainerFullname();
        console.log('Viewing details for trainer:', trainer);
    }, [trainer]);

    return (
        <div className='manage-staff-container'>
            <div className="manage-staff-header">
                <div className="manage-staff-title">
                    <div className="text">Trainer: {fullname.firstName} {fullname.lastName}</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='staff-actions'>
                <div className='lessons'>
                    {lessons.map((lesson, index) => (
                        <div key={index}>
                            <h2>{lesson.name}</h2>
                            <p>{lesson.startDate}</p>
                            <button className='more' onClick={() => handleInformation(lesson.name)}>More</button>
                        </div>
                        ))}
                </div>
                <div className=''>

                </div>
                <Link to={"/AdministratorHome"}>
                    <button className='staff-button back'>Home</button>
                </Link>
            </div>
            <MoreModal
                isOpen={showModifyModal}
                onClose={() => setShowModifyModal(false)}
                lesson={selectedLesson}
            />
        </div>
    );
};

export default authentication(ProfessorView);
