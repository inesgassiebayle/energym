import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import '../Home.css';
import logo from "../Assets/Logo.png";
import authentication from "./Common/Hoc/Authentication";
import axios from "axios";
import ChangeProfessorPasswordModal from "./ChangeProfessorPasswordModal";
import deleteIcon from "../Assets/person.png"


const ProfessorHome = () => {
    const {username} = useParams();
    const navigate = useNavigate();
    const [showChangePasswordModal, setShowChangePasswordModal] = useState(false);

    const handleLogout = async () => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                await axios.post('http://localhost:3333/user/logout', {}, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                localStorage.removeItem('token');
                navigate('/login');
            } catch (error) {
                console.error('Failed to invalidate the token on the server:', error);
            }
        }
    };

    const handleDeleteAccountClick = () => {
        setShowChangePasswordModal(true);
    };

    return (
        <div className='home-container'>
            <div className='home-logo'>
                <img src={logo} alt="Logo" style={{width: '150px'}}/>
            </div>
            <div className='home-actions'>
                <button className='button' onClick={() => navigate(`/trainer/${username}/schedule`)}>Quick view
                </button>

                <button className='button' onClick={() => navigate(`/trainer/${username}/calendar`)}>My
                    Calendar
                </button>

                {showChangePasswordModal && (
                    <div className="modal-overlay">
                        <ChangeProfessorPasswordModal
                            onClose={() => setShowChangePasswordModal(false)}
                        />
                    </div>
                )}
                <button className='button' onClick={handleDeleteAccountClick}>
                    <img src={deleteIcon} alt="Delete account"/>
                </button>
                <button className='button logout' onClick={handleLogout}>Log Out</button>
            </div>
        </div>
    );
}

export default authentication(ProfessorHome);