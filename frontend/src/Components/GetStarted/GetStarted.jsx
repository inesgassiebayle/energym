import React from 'react';
import './GetStarted.css';
import logo from "../Assets/Logo.png";
import {Link, useNavigate} from "react-router-dom";

const GetStarted = ()=>{
    let navigate = useNavigate();

    return (
        <div className='get-started-container'>
            <div className='logo-get-started'>
                <img src={logo} alt="Logo" style={{width: '150px'}}/>
            </div>
            <div className='get-started-actions'>
                <button className='get-started-button' onClick={() => navigate('/login')}>Login
                </button>
                <button className='get-started-button' onClick={() => navigate('/signup')}>Sign Up
                </button>
            </div>
        </div>
    )
}
export default GetStarted