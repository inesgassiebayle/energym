import React from 'react'
import './ForgotPassword.css'
import logo from "../Assets/Logo.png";
import email_icon from "../Assets/email.png";
import {Link} from "react-router-dom";

const ForgotPassword = ()=>{
    return(
        <div className="container-forgotpassword">
            <div className='header'>
                <div className='title'>
                    <div className='text-title'>Restore Password</div>
                    <div className='underline'></div>
                </div>
                <div className='logo-change'>
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='inputs'>
                <div className='input'>
                    <img src={email_icon} alt=""/>
                    <input type='email' placeholder='Email'/>
                </div>
            </div>
            <Link to='/Login/RestorePassword/ChangePassword'><button className='change-button'>Change Password</button></Link>
        </div>
    )
}
export default ForgotPassword