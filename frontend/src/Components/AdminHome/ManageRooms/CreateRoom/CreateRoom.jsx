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

    const [roomError, setRoomNameInvalid] = useState('');
    const [capacityError, setCapacityInvalid] = useState('');
    const [activityError, setActivityInvalid] = useState('');



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
            const errorMsg = error.response?.data || 'An unexpected error occurred.';
            console.error('Error al enviar solicitud:', error);
            setRoomNameInvalid('');
            setCapacityInvalid('');
            setActivityInvalid('');
            if (errorMsg.includes("Room already exists")){
                setRoomNameInvalid("Room name already exists");
            }else if (errorMsg.includes("Invalid capacity")) {
                setCapacityInvalid("Invalid capacity");
            }else if (errorMsg.includes("Activity does not exist")) {
                setActivityInvalid("No activities selected")
            }
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
                    onChange={(e) => {
                        setClassName(e.target.value)
                        setRoomNameInvalid('');
                    }}
                    placeholder='Room Name'

                />
                {roomError && <div className="error-message" style={{ color: 'red', textAlign: 'center' }}>{roomError}</div>}

                <div className="select-activity-container">

                    <div className="select-activity" onClick={() => {
                        setShowOptions(!showOptions)
                        setActivityInvalid('')
                    }}>
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
                                        required
                                    />
                                    <label>{activityName}</label>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
                {activityError && <div className="error-message" style={{ color: 'red', textAlign: 'center' }}>{activityError}</div>}

                <input
                    type='number'
                    value={capacity}
                    onChange={(e) => {
                        setCapacity(e.target.value)
                        setCapacityInvalid("");
                    }}
                    placeholder='Capacity'
                    required
                />

                {capacityError && <div className="error-message" style={{ color: 'red', textAlign: 'center' }}>{capacityError}</div>}

                <div className='form-actions'>
                    <button type='submit'>Confirm</button>
                    <button type='button' onClick={() => navigate('/AdministratorHome/ManageRooms')}>Cancel</button>
                </div>
            </form>
        </div>
    );
}

export default authentication(CreateRoom);
