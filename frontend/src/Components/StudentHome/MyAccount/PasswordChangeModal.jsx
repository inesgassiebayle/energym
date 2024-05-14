import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
// import password_icon from '../Assets/password.png';
import axios from 'axios';

const PasswordChangeModal = ({ onClose }) => { // Ajusta las props para recibir onClose
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmation, setConfirmation] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const navigate = useNavigate();

    const getUsername = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            navigate('/login');
        }
        try {
            const response = await axios.get('http://localhost:3333/user/verify', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            if (response.data.type !== 'STUDENT') {
                console.log('User is not an administrator, redirecting to login.');
                navigate('/login');
                return;
            }
            setUsername(response.data.username);
        } catch (error) {
            console.error('User not logged in:', error);
            navigate('/login');
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
                passwordConfirmation: confirmation,
            });
            setSuccessMessage('Password changed successfully.');
            console.log(response.data);
            onClose(); // Cierra el modal cuando el cambio de contraseña es exitoso
        } catch (error) {
            setErrorMessage(error.response?.data || 'Error changing password.');
            console.error('Password change error:', error);
        }
    };

    useEffect(() => {
        getUsername();
    }, []);

    return (
        <div className="change-password-container">
            {errorMessage && <div className="error-message">{errorMessage}</div>}
            {successMessage && <div className="success-message">{successMessage}</div>}
            <div className="inputs">
                <div className="password-input">
                    {/*<img src={password_icon} alt="Password" />*/}
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="New Password" />
                </div>
                <div className="password-input">
                    {/*<img src={password_icon} alt="Confirm" />*/}
                    <input type="password" value={confirmation} onChange={(e) => setConfirmation(e.target.value)} placeholder="Confirm Password" />
                </div>
            </div>
            <button className="change" onClick={handleChange}>
                Change Password
            </button>
            <button className="cancel" onClick={onClose}>
                Cancel
            </button>
        </div>
    );
};

export default PasswordChangeModal;
