import React, { useEffect, useState } from 'react';
import axios from "axios";

const ModifyRoomModal = ({ isOpen, onClose, roomName, onSave }) => {
    const [selectedActivities, setSelectedActivities] = useState({});
    const [activityNames, setActivityNames] = useState([]);
    const [roomActivities, setRoomActivities] = useState([]);
    const [showOptions, setShowOptions] = useState(false);
    const [name, setName] = useState('');
    const [capacity, setCapacity] = useState('');
    const [oldCapacity, setOldCapacity] = useState('');
    const [oldRoomActivities, setOldActivities] = useState('');


    useEffect(() => {
        if (!isOpen) return;

        const fetchRoomDetails = async () => {
            try {
                const activitiesresponse = await axios.get('http://localhost:3333/activity/get');
                setActivityNames(activitiesresponse.data);

                const capacityResponse = await axios.get(`http://localhost:3333/room/${roomName}/getCapacity`);
                setCapacity(capacityResponse.data);
                setOldCapacity(capacityResponse.data);

                const roomActivitiesResponse = await axios.get(`http://localhost:3333/room/${roomName}/getActivities`);
                setOldActivities(roomActivitiesResponse.data);

                const activitiesState = {};
                roomActivitiesResponse.data.forEach(activity => {
                    activitiesState[activity] = true;  // Asume que el nombre de la actividad es un string único
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
        <div className="modal" tabIndex="-1" role="dialog">
            <div className="modal-dialog" role="document">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Modify Room "{roomName}"</h5>
                    </div>
                    <div className="modal-body">
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <input
                                    type='text'
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
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
                            </div>
                            <button className="apply-changes">Save changes</button>
                        </form>
                    </div>
                    <div className="modal-footer">
                        <button className="cancel" onClick={onClose}>Cancel</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ModifyRoomModal;
