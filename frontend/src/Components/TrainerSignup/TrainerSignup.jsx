import React from 'react'
import './TrainerSignup.css'
import logo from "../Assets/Logo.png";
import person_icon from "../Assets/person.png";
import email_icon from "../Assets/email.png";
import password_icon from "../Assets/password.png";
import {Link} from "react-router-dom";

const TrainerSignup = ()=>{
    return(
        <div className="container">
            <div className='header'>
                <div className='title'>
                    <div className='text'> Sign Up</div>
                    <div className='underline'></div>
                </div>
                <div className='logo'>
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='inputs'>
                <div className='input'>
                    <img src={person_icon} alt=""/>
                    <input type='first name' placeholder='First name'/>
                </div>
                <div className='input'>
                    <img src={person_icon} alt=""/>
                    <input type='last name' placeholder='Last name'/>
                </div>
                <div className='input'>
                    <img src={person_icon} alt=""/>
                    <input type='username' placeholder='Username'/>
                </div>
                <div className='input'>
                    <img src={email_icon} alt=""/>
                    <input type='email' placeholder='Email'/>
                </div>
                <div className='input'>
                    <img src={password_icon} alt=""/>
                    <input type='password' placeholder='Password'/>
                </div>
                <div className='input'>
                    <img src={password_icon} alt=""/>
                    <input type='key' placeholder='Key'/>
                </div>
            </div>
            <button className='signup-button'>Sign Up</button>
        </div>
    )
}

export default TrainerSignup