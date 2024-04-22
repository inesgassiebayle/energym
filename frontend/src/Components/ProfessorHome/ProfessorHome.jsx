
import React from 'react';
import { useNavigate } from 'react-router-dom';
import './ProfessorHome.css';
import logo from "../Assets/Logo.png";

const ProfessorHome = () => {
    let navigate = useNavigate();

    return (
        <div className='professor-home-container'>
            <div className='title'>Professor Home</div>
            <div className='logo-professor'>
                <img src={logo} alt="Logo" style={{width: '150px'}}/>
            </div>
            <div className='professor-actions'>
                <button className='professor-button' onClick={() => navigate('/ProfessorHome/MyRatings')}>My Ratings</button>
                <button className='professor-button' onClick={() => navigate('/ProfessorHome/MySchedule')}>My Schedule</button>
                <button className='professor-button' onClick={() => navigate('/my-account')}>My Account</button>
                <button className='professor-button logout' onClick={() => navigate('/')}>Log Out</button>
            </div>
        </div>
    );
}

export default ProfessorHome;
