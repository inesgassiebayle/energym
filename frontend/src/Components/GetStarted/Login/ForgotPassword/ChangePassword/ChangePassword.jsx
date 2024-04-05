import React from 'react'
import './ChangePassword.css'
import logo from "../../../../Assets/Logo.png";
import password_icon from "../../../../Assets/password.png";

const ChangePassword = ()=>{
    return(
        <div className="change-password-container">
            <div className='change-password-header'>
                <div className='title'>
                    <div className='text-title'>Change Password</div>
                </div>
                <div className='logo-change'>
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='change-password-inputs'>
                <div className='change-password-input'>
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