import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ChangePassword.css';
import logo from "../../../Assets/Logo.png";
import person_icon from "../../../Assets/person.png";
import axios from "axios";

const ChangePassword = () => {
    const [mail, setMail] = useState("");
    let navigate = useNavigate();

    const handleChange = async () => {
        try {
            const response = await axios.patch('http://localhost:3333/user/forgot-password', { mail });
            console.log(response.data);
            navigate("/login");
        } catch (error) {
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
            <div className='change-password-inputs'>
                <div className='change-password-input'>
                    <img src={person_icon} alt="Email"/>
                    <input type='text' value={mail} onChange={(e) => setMail(e.target.value)} placeholder='Email'/>
                </div>
            </div>
            <button className='change-button' onClick={handleChange}>Send temporary key</button>
        </div>
    )
}

export default ChangePassword;
