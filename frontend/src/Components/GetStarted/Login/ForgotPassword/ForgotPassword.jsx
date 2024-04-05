import React from 'react'
import './ForgotPassword.css'
import logo from "../../../Assets/Logo.png";
import email_icon from "../../../Assets/email.png";
import {Link} from "react-router-dom";

const ForgotPassword = ()=>{
    return(
        <div className="forgot-password-container">
            <div className='forgot-password-header'>
                <div className='forgot-password-title'>
                    <div className='text-title'>Restore Password</div>
                </div>
                <div className='logo-change'>
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='forgot-pasword-inputs'>
                <div className='forgot-password-input'>
                    <img src={email_icon} alt=""/>
                    <input type='email' placeholder='Email'/>
                </div>
            </div>
            <Link to='/Login/RestorePassword/ChangePassword'><button className='forgot-button'>Restore Password</button></Link>
        </div>
    )
}
export default ForgotPassword