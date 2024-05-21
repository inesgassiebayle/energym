import React, { useEffect, useState } from 'react';
import axios from "axios";
import {useNavigate} from "react-router-dom";
import authentication from "../Hoc/Hoc";
import "../../Modal.css"
import "./ActivitySelection.css"

const ModifyRoomModal = ({ isOpen, onClose, roomName, onSave }) => {
    let navigate = useNavigate();
    const [selectedActivities, setSelectedActivities] = useState({});
    const [activityNames, setActivityNames] = useState([]);
    const [showOptions, setShowOptions] = useState(false);
    const [name, setName] = useState('');
    const [capacity, setCapacity] = useState('');


    useEffect(() => {
        if (!isOpen) return;

        setName(roomName);

        const fetchRoomDetails = async () => {
            try {
                const activitiesResponse = await axios.get('http://localhost:3333/activity/get');
                setActivityNames(activitiesResponse.data);

                const capacityResponse = await axios.get(`http://localhost:3333/room/${roomName}/getCapacity`);
                setCapacity(capacityResponse.data);

                const roomActivitiesResponse = await axios.get(`http://localhost:3333/room/${roomName}/getActivities`);

                const activitiesState = {};
                roomActivitiesResponse.data.forEach(activity => {
                    activitiesState[activity] = true;
                });
                setSelectedActivities(activitiesState);

            } catch (error) {
                console.error('Error fetching room information:', error);
            }
        };

        fetchRoomDetails();
    }, [isOpen, roomName]);


    const handleSelectChange = (e) => {
        const { name, checked } = e.target;
        setSelectedActivities({ ...selectedActivities, [name]: checked });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const activities = Object.entries(selectedActivities)
                .filter(([activityName, isSelected]) => isSelected)
                .map(([activityName]) => activityName);

            const response = await axios.patch('http://localhost:3333/room/modify', {
                name: roomName,
                newName: name,
                capacity: capacity,
                activities: activities.join(',')
            });
            console.log(response.data);
            onClose(true);
            onSave();
        } catch (error) {
            console.error('Error al enviar solicitud:', error);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="modal">
                <div className="modal-header">
                    <h5 className="modal-title">Modify Room {roomName}</h5>
                    <button onClick={onClose} className="modal-close-button">&times;</button>
                </div>
                <div className="modal-body">
                    <form onSubmit={handleSubmit} className="modal-form">
                        <input className="modal-input" type="text" value={name} placeholder="Room Name"
                               onChange={(e) => setName(e.target.value)} required/>
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
                            className="modal-input"
                            type='number'
                            value={capacity}
                            onChange={(e) => setCapacity(e.target.value)}
                            placeholder='Capacity'
                            required
                        />
                    </form>
                </div>
                <div className="modal-footer">
                    <button type="submit" className="submit" onClick={handleSubmit}>Save changes</button>
                    <button type="button" className="cancel" onClick={onClose}>Cancel</button>
                </div>
        </div>

    );
};


export default authentication(ModifyRoomModal);
