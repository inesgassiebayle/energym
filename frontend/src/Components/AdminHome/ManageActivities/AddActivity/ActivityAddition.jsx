import React, {useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import './ActivityAddition.css';
import logo from "../../../Assets/Logo.png";
import axios from "axios";

const ActivityAddition = () => {
    const [activityName, setActivityName] = useState('');
    let navigate = useNavigate();


    const handleSubmit = async (e) => {
        e.preventDefault(); // Evitar el envío automático del formulario
        try {
            const response = await axios.post('http://localhost:3333/activity/add', {
                name: activityName
            });
            console.log(response.data);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error('Error al enviar solicitud:', error);
        }
    };

    return (
        <div className='create-activity-container'>
            <div className="create-activity-header">
                <div className="create-activity-title">
                    <div className="text">Add Activity</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <form onSubmit={handleSubmit}>
                <input type='text' value={activityName} onChange={(e) => setActivityName(e.target.value)}
                       placeholder='Activity Name' required/>
                <div className='form-actions'>
                    <button type='submit'>Add</button>
                    <button type='button' onClick={() => navigate('/AdministratorHome/ManageActivities')}>Cancel
                    </button>
                </div>
            </form>
        </div>
    );
}

export default ActivityAddition;
