import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './ManageActivities.css';
import authentication from "../Hoc/Hoc";
import logo from "../../Assets/Logo.png";

const ManageActivities = () => {
    let navigate = useNavigate();
    const [activityNames, setActivityNames] = useState([]);
    const [errorMessage, setErrorMessage] = useState('');
    const [selectedActivity, setSelectedActivity] = useState(null);
    const [showModal, setShowModal] = useState(false);
    const [confirmDelete, setConfirmDelete] = useState(false);

    useEffect(() => {
        fetchActivities();
    }, []);

    const fetchActivities = async () => {
        try {
            const response = await axios.get('http://localhost:3333/activity/get');
            setActivityNames(response.data);
        } catch (error) {
            console.error('Error fetching activity names:', error);
            setErrorMessage('Failed to fetch activity names.');
        }
    };

    const handleDelete = async (activityName) => {
        setSelectedActivity(activityName);
        setConfirmDelete(true);
    }

    const openModal = (activity) => {
        setSelectedActivity(activity);
        setShowModal(true);
    };

    const closeModal = () => {
        setShowModal(false);
        setSelectedActivity(null);
        fetchActivities();
    };

    const confirmDeleteHandler = async () => {
        try{
            await axios.delete(`http://localhost:3333/activity/${selectedActivity}/delete `);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error('Error deleting room:', error);

        }
    }

    return (
        <div className='manage-activities-container'>
            <div className="manage-activities-header">
                <div className="manage-activities-title">
                    <div className="text">Manage Activities</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='activities-actions'>
                <button className='activities-button'
                        onClick={() => navigate('/AdministratorHome/ManageActivities/AddActivity')}>Add Activity
                </button>
                {activityNames.map((activity, index) => (
                    <div key={index} className="activity-item">
                        {activity}
                        <button className='modification-button'
                                onClick={() => handleDelete(activity)}>Delete</button>
                    </div>
                ))}
                <Link to={"/AdministratorHome"}><button className='activities-button back'>Home</button></Link>
            </div>
            {confirmDelete && (
                <div className='confirmation-message'>
                    <p>Are you sure you want to delete the activity : "{selectedActivity}"?</p>
                    <div className='confirmation-actions'>
                        <button onClick={confirmDeleteHandler}>Yes</button>
                        <button onClick={() => setConfirmDelete(false)} className='cancel'>No</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default authentication(ManageActivities);
