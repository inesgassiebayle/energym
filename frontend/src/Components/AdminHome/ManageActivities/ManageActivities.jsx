import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../../HomeComponents.css';
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
        <div className='home-components-container'>
            <div className="home-components-header">
                <div className="home-components-title">
                    <div className="home-components-text">Manage Activities</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='home-components-actions'>
                <button className='home-components-button'
                        onClick={() => navigate('/AdministratorHome/ManageActivities/AddActivity')}>Add Activity
                </button>
                {activityNames.map((activity, index) => (
                    <div key={index} className="home-components-subtitle">
                        {activity}
                        <button className='home-components-modification-button'
                                onClick={() => handleDelete(activity)}>Delete</button>
                    </div>
                ))}
                <Link to={"/AdministratorHome"}><button className='button logout'>Home</button></Link>
            </div>
            {confirmDelete && (
                <div className='modal'>
                    <p>Are you sure you want to delete the activity : "{selectedActivity}"?</p>
                    <div className='modal-footer'>
                        <button onClick={confirmDeleteHandler} className="modal-button">Yes</button>
                        <button onClick={() => setConfirmDelete(false)} className='modal-button cancel'>No</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default authentication(ManageActivities);
