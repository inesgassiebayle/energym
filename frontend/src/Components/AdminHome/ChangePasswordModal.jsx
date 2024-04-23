import React, {useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import password_icon from "../Assets/password.png";
import axios from "axios";
import './ChangePasswordModal.css'

const ChangePasswordModal = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmation, setConfirmation] = useState('');

    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    let navigate = useNavigate();

    const getUsername = async () => {
        const token = localStorage.getItem('token');

        if(!token){
            navigate('/login');
        }
        try{
            const response = await axios.get('http://localhost:3333/user/verify', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });
            if(response.data.type !== 'ADMINISTRATOR'){
                console.log('User is not an administrator, redirecting to login.');
                navigate('/login');
                return;
            }
            setUsername(response.data.username);
        }
        catch (error) {
            console.error("User not logged in:", error);
            navigate("/login");
        }
    }

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
    }

    const handleDelete = async () => {
        const confirmation = window.confirm('Are you sure you want to delete your account? This action cannot be undone.');

        if (confirmation) {
            const token = localStorage.getItem('token');
            if (token) {
                try {
                    await axios.delete('http://localhost:3333/user/delete', {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });
                    localStorage.removeItem('token');
                    navigate('/login');
                } catch (error) {
                    console.error('Failed to delete the account:', error);
                }
            }
        }
    };

    useEffect(() => {
        getUsername();
    }, []);

    return (
        <div className="change-password-container">
            {errorMessage && <div className="error-message">{errorMessage}</div>}
            {successMessage && <div className="success-message">{successMessage}</div>}
            <div className='inputs'>
                <div className='password-input'>
                    <img src={password_icon} alt="Password"/>
                    <input type='password' value={password} onChange={(e) => setPassword(e.target.value)} placeholder='New Password'/>
                </div>
                <div className='password-input'>
                    <img src={password_icon} alt="Confirm"/>
                    <input type='password' value={confirmation} onChange={(e) => setConfirmation(e.target.value)} placeholder='Confirm Password'/>
                </div>
            </div>
            <button className='change' onClick={handleChange}>Change Password</button>
            <button className='delete' onClick={handleDelete}>Delete account</button>
        </div>
    )
}

export default ChangePasswordModal;
