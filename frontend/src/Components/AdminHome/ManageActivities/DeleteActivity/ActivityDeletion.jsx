import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './ActivityDeletion.css';
import logo from "../../../Assets/Logo.png";
import axios from 'axios';

const ActivityDeletion = () => {
    const navigate = useNavigate();
    const [activityNames, setActivityNames] = useState([]);
    const [selectedActivity, setSelectedActivity] = useState('');
    const [confirmDelete, setConfirmDelete] = useState(false);
    const [username, setUsername] = useState('');

    // Function to verify token validity and user role
    const verifyToken = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            console.log('No token found, redirecting to login.');
            navigate('/Login');
            return;
        }

        try {
            const response = await axios.get('http://localhost:3333/user/verify', {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            // Check if the user is an administrator
            if (response.data.type !== 'ADMINISTRATOR') {
                console.log('User is not an administrator, redirecting to login.');
                navigate('/Login');
                return;
            }

            setUsername(response.data.username);
        } catch (error) {
            console.error('Token validation failed:', error);
            navigate('/Login');
        }
    };


    useEffect(() => {
        verifyToken();
        const fetchActivityNames = async () => {
            try {
                const response = await axios.get('http://localhost:3333/activity/get');
                setActivityNames(response.data);
            } catch (error) {
                console.error('Error fetching activity names:', error);
            }
        };
        fetchActivityNames();
    }, []);

    const handleSubmit = async () => {
        try {
            const response = await axios.post('http://localhost:3333/activity/delete', {
                name: selectedActivity
            });
            console.log('Activity deleted:', response.data);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error('Error deleting activity:', error);
        }
    };

    return (
        <div className='delete-activity-container'>
            <div className="delete-activity-header">
                <div className="delete-activity-title">
                    <div className="text">Delete Activity</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            {!confirmDelete ? (
                <form onSubmit={() => setConfirmDelete(true)}>
                    <select value={selectedActivity} onChange={(e) => setSelectedActivity(e.target.value)} required>
                        <option value=''>Select Activity</option>
                        {activityNames.map((activityName, index) => (
                            <option key={index} value={activityName}>{activityName}</option>
                        ))}
                    </select>
                    <div className='form-actions'>
                        <button type='submit'>Confirm</button>
                        <button type='button' onClick={() => navigate('/AdministratorHome/ManageActivities')} className='cancel'>Cancel</button>
                    </div>
                </form>
            ) : (
                <div className='confirmation-message'>
                    <p>Are you sure you want to delete the activity "{selectedActivity}"?</p>
                    <div className='confirmation-actions'>
                        <button onClick={handleSubmit}>Yes</button>
                        <button onClick={() => setConfirmDelete(false)} className='cancel'>No</button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default ActivityDeletion;
