import React, { useState} from 'react';
import axios from 'axios';
import './SignUp.css';
import email_icon from '../../Assets/email.png';
import password_icon from '../../Assets/password.png';
import person_icon from '../../Assets/person.png';
import logo from '../../Assets/Logo.png';
import { Link, useNavigate } from 'react-router-dom';

const SignUp = () => {
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const [emailError, setEmailError] = useState('');
    const [passwordError, setPasswordError] = useState('');
    const [generalError, setGeneralError] = useState('');

    const handleSignUp = async () => {
        try {
            const response = await axios.post('http://localhost:3333/user/administrator-signup', {
                firstName: firstName,
                lastName: lastName,
                username: username,
                email: email,
                password: password
            });
            console.log(response.data);
            navigate('/login');
        } catch (error) {
            const errorMsg = error.response?.data || 'An unexpected error occurred.';
            console.error('Error while sending request:', errorMsg);
            setEmailError('');
            setPasswordError('');
            setGeneralError('');
            if (errorMsg.includes("Invalid email")) {
                setEmailError('Please enter a valid email address.');
            } else if (errorMsg.includes("Invalid password")) {
                setPasswordError('Password must contain at least one number and one uppercase letter.');
            } else if (errorMsg.includes("Username or email already registered")) {
                setGeneralError(errorMsg); // This will show a general error message
            }
        }
    };


    return (
        <div className="signup-container">
            <div className="signup-header">
                <div className="signup-title">
                    <div className="text"> Sign Up </div>
                </div>
                <div className="logo">
                    <img src={logo} alt="" />
                </div>
            </div>
            <div className="signup-inputs">
                <div className="signup-input">
                    <img src={person_icon} alt="" />
                    <input type="text" name="firstName" value={firstName} onChange={(e) => setFirstName(e.target.value)} placeholder="First name" />
                </div>
                <div className="signup-input">
                    <img src={person_icon} alt="" />
                    <input type="text" name="lastName" value={lastName} onChange={(e) => setLastName(e.target.value)} placeholder="Last name" />
                </div>
                <div className="signup-input">
                    <img src={person_icon} alt="" />
                    <input type="text" name="username" value={username} onChange={(e) => setUsername(e.target.value)} placeholder="Username" />
                </div>
                <div className="signup-input">
                    <img src={email_icon} alt="Email"/>
                    <input
                        type="email"
                        name="email"
                        value={email}
                        onChange={(e) => {
                            setEmail(e.target.value);
                            setEmailError(''); // Clear error on change
                        }}
                        placeholder="Email"
                    />
                    {emailError && <div className="error-message">{emailError}</div>}
                </div>

                <div className="signup-input">
                    <img src={password_icon} alt="Password"/>
                    <input
                        type="password"
                        name="password"
                        value={password}
                        onChange={(e) => {
                            setPassword(e.target.value);
                            setPasswordError('');
                        }}
                        placeholder="Password"
                    />
                    {passwordError && <div className="error-message">{passwordError}</div>}
                </div>
            </div>

            {generalError && <div className="general-error-message">{generalError}</div>}

            <button className="signup-button" onClick={handleSignUp}>Sign Up</button>
        </div>
    );
};

export default SignUp;
