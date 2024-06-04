import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import password_icon from "../Assets/password.png";
import axios from "axios";
import '../../Components/AdminHome/ManagePassword.css';
import DeleteConfirmationModal from './DeleteConfirmationModal';

const ChangeStudentPasswordModal = ({ onClose }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmation, setConfirmation] = useState('');
    const [showDeleteModal, setShowDeleteModal] = useState(false); // State to control delete modal visibility

    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    let navigate = useNavigate();

    const getUsername = async () => {
        const token = localStorage.getItem('token');

        if (!token) {
            navigate('/login');
        }
        try {
            const response = await axios.get('http://localhost:3333/user/verify', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if (response.data.type !== 'STUDENT') {
                console.log('User is not a student, redirecting to login.');
                navigate('/login');
                return;
            }
            setUsername(response.data.username);
        } catch (error) {
            console.error("User not logged in:", error);
            navigate("/login");
        }
    };

    const handleChange = async () => {
        setErrorMessage('');
        setSuccessMessage('');
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
            setSuccessMessage("Password changed successfully.");
            console.log(response.data);
        } catch (error) {
            setErrorMessage(error.response?.data || "Error changing password.");
            console.error("Password change error:", error);
        }
    };

    useEffect(() => {
        getUsername();
    }, []);

    return (
        <div>
            <div className="modal">
                {errorMessage && <div className="error-message">{errorMessage}</div>}
                {successMessage && <div className="success-message">{successMessage}</div>}
                <div className="modal-header">
                    <h5 className="modal-title">Manage Account</h5>
                    <button onClick={onClose} className="modal-close-button">&times;</button>
                </div>
                <div className="modal-body">
                    <form className="modal-form">
                        <div className="password-input">
                            <img src={password_icon} alt="Password" />
                            <input
                                type="password"
                                name="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="Password"
                            />
                        </div>
                        <div className="password-input">
                            <img src={password_icon} alt="Password" />
                            <input
                                type="password"
                                name="password"
                                value={confirmation}
                                onChange={(e) => setConfirmation(e.target.value)}
                                placeholder='Confirm Password'
                            />
                        </div>
                    </form>
                </div>
                <div className="modal-footer">
                    <button type="submit" className="submit" onClick={handleChange}>Change Password</button>
                    <button type="button" className="cancel" onClick={() => setShowDeleteModal(true)}>Delete account</button>
                </div>
            </div>
            <DeleteConfirmationModal
                isOpen={showDeleteModal}
                onClose={() => setShowDeleteModal(false)}
            />
        </div>
    )
}

export default ChangeStudentPasswordModal;
