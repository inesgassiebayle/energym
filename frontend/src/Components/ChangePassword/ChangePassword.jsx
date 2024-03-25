import React from 'react'
import './ChangePassword.css'
import logo from "../Assets/Logo.png";
import password_icon from "../Assets/password.png";

const ChangePassword = ()=>{
    return(
        <div className="container">
            <div className='header'>
                <div className='title'>
                    <div className='text-title'>Change Password</div>
                    <div className='underline'></div>
                </div>
                <div className='logo-change'>
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='inputs'>
                <div className='input'>
                    <img src={password_icon} alt=""/>
                    <input type='password' placeholder='Password'/>
                </div>
                <div className='input'>
                    <img src={password_icon} alt=""/>
                    <input type='password' placeholder='Confirm Password'/>
                </div>
            </div>
            <button className='change-button'>Change Password</button>
        </div>
    )
}
export default ChangePassword