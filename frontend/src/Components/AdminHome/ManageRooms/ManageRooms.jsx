import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './ManageRooms.css';
import ModifyRoomModal from './ModifyRoomModal'; // Import the new modal component

const ManageRooms = () => {
    let navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [roomNames, setRoomNames] = useState([]);
    const [selectedRoom, setSelectedRoom] = useState('');
    const [confirmDelete, setConfirmDelete] = useState(false);
    const [showModifyModal, setShowModifyModal] = useState(false);

    useEffect(() => {
        verifyToken();
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

    const handleDelete = async (roomName) => {
        setSelectedRoom(roomName);
        setConfirmDelete(true);
    };

    const reloadRoomNames = async () => {
        try {
            const response = await axios.get('http://localhost:3333/room/get');
            setRoomNames(response.data);
        } catch (error) {
            console.error('Error fetching room names:', error);
        }
    };

    const handleModify = (roomName) => {
        setSelectedRoom(roomName);
        setShowModifyModal(true);
    };

    const confirmDeleteHandler = async () => {
        try {
            await axios.delete(`http://localhost:3333/room/${selectedRoom}/delete`);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error('Error deleting room:', error);
        }
    };

    return (
        <>
            <div className='manage-rooms-container'>
                <div className="manage-rooms-header">
                    <div className="manage-rooms-title">
                        <div className="text">Manage Rooms</div>
                    </div>
                </div>
                <div className='rooms-actions'>
                    <button className='rooms-button'
                            onClick={() => navigate('/AdministratorHome/ManageRooms/CreateRoom')}>New Room
                    </button>
                    <div className='room-list'>
                        {roomNames.map((roomName, index) => (
                            <div key={index} className='room-item'>
                                <span>{roomName}</span>
                                <button className='modification-button' onClick={() => handleDelete(roomName)}>Delete</button>
                                <button className='modification-button' onClick={() => handleModify(roomName)}>Modify</button>
                            </div>
                        ))}
                    </div>
                    <Link to={"/AdministratorHome"}>
                        <button className='rooms-button back'>Home</button>
                    </Link>
                </div>
                {confirmDelete && (
                    <div className='confirmation-message'>
                        <p>Are you sure you want to delete the room "{selectedRoom}"?</p>
                        <div className='confirmation-actions'>
                            <button onClick={confirmDeleteHandler}>Yes</button>
                            <button onClick={() => setConfirmDelete(false)} className='cancel'>No</button>
                        </div>
                    </div>
                )}
                <ModifyRoomModal
                    isOpen={showModifyModal}
                    onClose={() => setShowModifyModal(false)}
                    roomName={selectedRoom}
                    onSave={reloadRoomNames} // Cambia esto para pasar la función de recarga
                />
            </div>
        </>
    );
}

export default ManageRooms;
