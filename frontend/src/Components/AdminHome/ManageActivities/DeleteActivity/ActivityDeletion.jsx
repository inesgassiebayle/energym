import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './ActivityDeletion.css';
import logo from "../../../Assets/Logo.png";
import axios from 'axios';
import authentication from "../../Hoc/Hoc";

const ActivityDeletion = () => {
    const navigate = useNavigate();
    const [activityNames, setActivityNames] = useState([]);
    const [selectedActivity, setSelectedActivity] = useState('');
    const [confirmDelete, setConfirmDelete] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    useEffect(() => {
        const fetchActivityNames = async () => {
            try {
                const response = await axios.get('http://localhost:3333/activity/get');
                setActivityNames(response.data);
            } catch (error) {
                console.error('Error fetching activity names:', error);
                setErrorMessage('Failed to fetch activity names.');
            }
        };
        fetchActivityNames();
    }, []);

    const handleDeleteConfirmation = () => {
        setConfirmDelete(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrorMessage('');
        setSuccessMessage('');

        if (confirmDelete && selectedActivity) {
            try {
                const response = await axios.post('http://localhost:3333/activity/delete', {
                    name: selectedActivity
                });
                console.log('Activity deleted:', response.data);
                setSuccessMessage(`Activity '${selectedActivity}' was successfully deleted.`);
                navigate('/AdministratorHome');
            } catch (error) {
                console.error('Error deleting activity:', error);
                setErrorMessage('Failed to delete activity.');
            }
        }
    };

    return (
        <div className='delete-activity-container'>
            <div className="delete-activity-header">
                <div className="delete-activity-title">
                    <div className="text">Delete Activity</div>
                </div>
                <div className="logo">
                    <img src={logo} alt="Energy Gym logo"/>
                </div>
            </div>
            <form onSubmit={handleSubmit}>
                {!confirmDelete ? (
                    <>
                        <select value={selectedActivity} onChange={(e) => setSelectedActivity(e.target.value)} required>
                            <option value=''>Select Activity</option>
                            {activityNames.map((activityName, index) => (
                                <option key={index} value={activityName}>{activityName}</option>
                            ))}
                        </select>
                        <div className='form-actions'>
                            <button type='button' onClick={handleDeleteConfirmation}>Confirm</button>
                            <button type='button' onClick={() => navigate('/AdministratorHome/ManageActivities')} className='cancel'>Cancel</button>
                        </div>
                    </>
                ) : (
                    <div className='confirmation-message'>
                        <p>Are you sure you want to delete the activity "{selectedActivity}"?</p>
                        <div className='confirmation-actions'>
                            <button type='submit'>Yes</button>
                            <button type='button' onClick={() => setConfirmDelete(false)} className='cancel'>No</button>
                        </div>
                    </div>
                )}
            </form>
            {errorMessage && <p className="error-message">{errorMessage}</p>}
            {successMessage && <p className="success-message">{successMessage}</p>}
        </div>
    );
}

export default authentication(ActivityDeletion);
