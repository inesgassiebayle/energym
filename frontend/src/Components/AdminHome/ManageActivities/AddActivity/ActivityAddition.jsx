import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ActivityAddition.css';
import logo from "../../../Assets/Logo.png";
import axios from "axios";
import authentication from "../../Hoc/Hoc";

const ActivityAddition = () => {
    const [activityName, setActivityName] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    let navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:3333/activity/add', {
                name: activityName
            });
            console.log(response.data);
            navigate('/AdministratorHome'); // Redirect on success
        } catch (error) {
            console.error('Error sending request:', error);
            setErrorMessage(error.response?.data || 'An unexpected error occurred');
        }
    };

    return (
        <div className='create-activity-container'>
            <div className="create-activity-header">
                <div className="create-activity-title">
                    <div className="text">Add Activity</div>
                </div>
                <div className="logo">
                    <img src={logo} alt="Logo"/>
                </div>
            </div>
            {errorMessage && <p className="error-message">{errorMessage}</p>}
            <form onSubmit={handleSubmit}>
                <input
                    type='text'
                    value={activityName}
                    onChange={(e) => {
                        setActivityName(e.target.value);
                        setErrorMessage('');
                    }}
                    placeholder='Activity Name'
                    required
                />
                <div className='form-actions'>
                    <button type='submit'>Add</button>
                    <button type='button' onClick={() => navigate('/AdministratorHome/ManageActivities')}>Cancel</button>
                </div>
            </form>
        </div>
    );
};

export default authentication(ActivityAddition);
