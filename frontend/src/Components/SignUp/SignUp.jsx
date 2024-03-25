import React, {useState} from 'react'
import './SignUp.css'
import email_icon from '../Assets/email.png'
import password_icon from '../Assets/password.png'
import person_icon from '../Assets/person.png'
import logo from '../Assets/Logo.png'
import {Link} from "react-router-dom";

const SignUp = ()=>{
    return(
        <div className="container">
            <div className='header'>
                <div className='title'>
                    <div className='text'> Sign Up </div>
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
            </div>
            <div className='trainer-signup'>Are you a trainer? <Link to={"/SignUp/TrainerSignUp"}><button>Sign Up here</button></Link></div>
            <button className='signup-button'>Sign Up</button>
        </div>
    )
}

export default SignUp