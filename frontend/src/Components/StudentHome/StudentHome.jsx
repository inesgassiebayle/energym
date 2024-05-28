import React, {useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import logo from "../Assets/Logo.png";
import '../Home.css';
import axios from "axios";
import authentication from "./Common/Hoc/Authentication";
import deleteIcon from "../Assets/person.png";
import ChangeStudentPasswordModal from "./ChangeStudentPasswordModal";

const StudentHome = () => {
    const {username} = useParams();
    const navigate = useNavigate();
    const [showChangePasswordModal, setShowChangePasswordModal] = useState(false);

    const handleDeleteAccountClick = () => {
        setShowChangePasswordModal(true);
    };

    console.log(username);
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

    return (
        <div className='home-container'>
            <div className='home-logo'>
                <img src={logo} alt="Logo" style={{width: '150px'}}/>
            </div>
            <div className='home-actions'>
                <button className='button' onClick={() => navigate(`/student/${username}/schedule`)}>My Schedule
                </button>
                {showChangePasswordModal && (
                    <div className="modal-overlay">
                            <ChangeStudentPasswordModal
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


export default authentication(StudentHome);