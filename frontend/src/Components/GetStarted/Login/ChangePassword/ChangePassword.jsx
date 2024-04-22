import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ChangePassword.css';
import logo from "../../../Assets/Logo.png";
import password_icon from "../../../Assets/password.png";
import person_icon from "../../../Assets/person.png";
import axios from "axios";

const ChangePassword = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmation, setConfirmation] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    let navigate = useNavigate();

    const handleChange = async () => {
        if (password !== confirmation) {
            setErrorMessage("Passwords don't match.");
            return;
        }
        try {
            const response = await axios.patch(`http://localhost:3333/user/change-password`, {
                username,
                password,
                passwordConfirmation: confirmation
            });
            navigate("/login");
        } catch (error) {
            setErrorMessage(error.response?.data || "Error changing password.");
            console.error("Password change error:", error);
        }
    }

    return (
        <div className="change-password-container">
            <div className='change-password-header'>
                <div className='title'>
                    <div className='text-title'>Change Password</div>
                </div>
                <div className='logo-change'>
                    <img src={logo} alt="Logo"/>
                </div>
            </div>
            {errorMessage && <div className="error-message">{errorMessage}</div>}
            <div className='change-password-inputs'>
                <div className='change-password-input'>
                    <img src={person_icon} alt="User"/>
                    <input type='text' value={username} onChange={(e) => setUsername(e.target.value)} placeholder='Username'/>
                </div>
                <div className='change-password-input'>
                    <img src={password_icon} alt="Password"/>
                    <input type='password' value={password} onChange={(e) => setPassword(e.target.value)} placeholder='New Password'/>
                </div>
                <div className='change-password-input'>
                    <img src={password_icon} alt="Confirm"/>
                    <input type='password' value={confirmation} onChange={(e) => setConfirmation(e.target.value)} placeholder='Confirm Password'/>
                </div>
            </div>
            <button className='change-button' onClick={handleChange}>Change Password</button>
        </div>
    )
}

export default ChangePassword;
