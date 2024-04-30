import React, {useEffect} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import './ProfessorHome.css';
import logo from "../Assets/Logo.png";
import authentication from "./Common/Hoc/Authentication";

const ProfessorHome = () => {
    const {username} = useParams();
    const navigate = useNavigate();

    return (
        <div className='professor-home-container'>
            <div className='title'>Professor Home</div>
            <div className='logo-professor'>
                <img src={logo} alt="Logo" style={{width: '150px'}}/>
            </div>
            <div className='professor-actions'>
                <button className='professor-button' onClick={() => navigate(`/trainer/${username}/schedule`)}>My Schedule</button>
                <button className='professor-button' onClick={() => navigate('/my-account')}>My Account</button>
                <button className='professor-button logout' onClick={() => navigate('/')}>Log Out</button>
            </div>
        </div>
    );
}

export default authentication(ProfessorHome);