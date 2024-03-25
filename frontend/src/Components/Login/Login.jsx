import logo from "../Assets/Logo.png";
import {Link} from "react-router-dom";
import React from "react";
import './Login.css'
import person_icon from "../Assets/person.png";
import email_icon from "../Assets/email.png";
import password_icon from "../Assets/password.png";

const Login = ()=>{
    return (
        <div className="container-login">

            <div className='header'>
                <div className='title'>
                    <div className='text'>Login</div>
                    <div className='underline'></div>
                </div>
                <div className='logo'>
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='inputs'>
                <div className='input'>
                    <img src={person_icon} alt=""/>
                    <input type='username' placeholder='Username'/>
                </div>
                <div className='input'>
                    <img src={password_icon} alt=""/>
                    <input type='password' placeholder='Password'/>
                </div>
            </div>
            <div className='forgot-password'>Forgot your password? <Link to={"/Login/RestorePassword"}><button>Click Here!</button></Link></div>
            <button className='login-button'>Login</button>
        </div>
    )
}
export default Login