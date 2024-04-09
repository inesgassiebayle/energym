import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './ManageRoom.css';
import logo from "../../../Assets/Logo.png";
import axios from "axios";

const ManageRoom = (room) => {
    let navigate = useNavigate();

    const [roomNames, setRoomNames] = useState([]);
    const [selectedRoom, setSelectedRoom] = useState('');
    const [confirmDelete, setConfirmDelete] = useState(false);
    const [isEditing, setIsEditing] = useState(false);

    useEffect(() => {
        const fetchRoomNames = async () => {
            try {
                const response = await axios.get('http://localhost:3333/room/get');
                setRoomNames(response.data);
            } catch (error) {
                console.error('Error fetching room names:', error);
            }
        };
        fetchRoomNames();
    }, []);

    const handleSubmit = async () => {
        try {
            const response = await axios.delete(`http://localhost:3333/room/${selectedRoom}/delete`);
            console.log('Room deleted:', response.data);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error('Error deleting room:', error);
        }
    };

    return (
        <div className='delete-room-container'>
            <div className="delete-rooms-header">
                <div className="delete-rooms-title">
                    <div className="text">Manage Room</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            {!confirmDelete ? (
                <form onSubmit={() => setConfirmDelete(true)}>
                    <select value={selectedRoom} onChange={(e) => setSelectedRoom(e.target.value)} required>
                        <option value=''>Select Room</option>
                        {roomNames.map((roomName, index) => (
                            <option key={index} value={roomName}>{roomName}</option>
                        ))}
                    </select>
                    <div className='form-actions'>
                        <button type='submit'>Delete</button>
                        { isEditing &&
                        <div>
                        <ManageRoom room={selectedRoom}/>
                        </div>

                        }
                        <button type='button' onClick={() => navigate(`/AdministratorHome/ManageRooms/ManageRoom/ModifyRoom/${selectedRoom}`)} className='cancel'>Modify Room</button>
                    </div>
                </form>
            ) : (
                <div className='confirmation-message'>
                    <p>Are you sure you want to delete the room "{selectedRoom}"?</p>
                    <div className='confirmation-actions'>
                        <button onClick={handleSubmit}>Yes</button>
                        <button onClick={() => setConfirmDelete(false)} className='cancel'>No</button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default ManageRoom;
