import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './CreateRoom.css';
import logo from "../../../Assets/Logo.png";
import axios from "axios";
import authentication from "../../Hoc/Hoc";

const CreateRoom = () => {
    let navigate = useNavigate();
    const [className, setClassName] = useState('');
    const [capacity, setCapacity] = useState('');
    const [selectedActivities, setSelectedActivities] = useState({});
    const [activityNames, setActivityNames] = useState([]);
    const [showOptions, setShowOptions] = useState(false); // Estado para mostrar u ocultar las opciones

    useEffect(() => {
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

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const activities = Object.entries(selectedActivities)
                .filter(([activityName, isSelected]) => isSelected)
                .map(([activityName]) => activityName);

            const response = await axios.post('http://localhost:3333/room/create', {
                name: className,
                capacity: capacity,
                activities: activities.join(',')
            });
            console.log(response.data);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error('Error al enviar solicitud:', error);
        }
    };

    const handleSelectChange = (e) => {
        const { name, checked } = e.target;
        setSelectedActivities({ ...selectedActivities, [name]: checked });
    };

    return (
        <div className='create-rooms-container'>
            <div className="create-rooms-header">
                <div className="create-rooms-title">
                    <div className="text">Create Room</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <form onSubmit={handleSubmit}>
                <input
                    type='text'
                    value={className}
                    onChange={(e) => setClassName(e.target.value)}
                    placeholder='Room Name'
                    required
                />
                <div className="select-activity-container">
                    <div className="select-activity" onClick={() => setShowOptions(!showOptions)}>
                        Select Activity
                    </div>
                    {showOptions && (
                        <div className="activity-options">
                            {activityNames.map((activityName, index) => (
                                <div key={index}>
                                    <input
                                        type="checkbox"
                                        name={activityName}
                                        checked={selectedActivities[activityName] || false}
                                        onChange={handleSelectChange}
                                    />
                                    <label>{activityName}</label>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
                <input
                    type='number'
                    value={capacity}
                    onChange={(e) => setCapacity(e.target.value)}
                    placeholder='Capacity'
                    required
                />
                <div className='form-actions'>
                    <button type='submit'>Confirm</button>
                    <button type='button' onClick={() => navigate('/AdministratorHome/ManageRooms')}>Cancel</button>
                </div>
            </form>
        </div>
    );
}

export default authentication(CreateRoom);
