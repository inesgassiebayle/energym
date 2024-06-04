import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../../HomeComponents.css';
import ModifyRoomModal from './ModifyRoomModal';
import authentication from "../Hoc/Hoc";

const ManageRooms = () => {
    let navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [roomNames, setRoomNames] = useState([]);
    const [selectedRoom, setSelectedRoom] = useState('');
    const [confirmDelete, setConfirmDelete] = useState(false);
    const [showModifyModal, setShowModifyModal] = useState(false);

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

    const closeModal = () => {
        setShowModifyModal(false);
        reloadRoomNames();
    }

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
            <div className='home-components-container'>
                <div className="home-components-header">
                    <div className="home-components-title">
                        <div className="home-components-text">Manage Rooms</div>
                    </div>
                </div>
                <div className='home-components-actions'>
                    <button className='home-components-button'
                            onClick={() => navigate('/AdministratorHome/ManageRooms/CreateRoom')}>New Room
                    </button>
                    <div className='home-components-subtitle'>
                        {roomNames.map((roomName, index) => (
                            <div key={index} className='home-components-item'>
                                <span>{roomName}</span>
                                <button className='home-components-modification-button' onClick={() => handleDelete(roomName)}>Delete</button>
                                <button className='home-components-modification-button' onClick={() => handleModify(roomName)}>Modify</button>
                            </div>
                        ))}
                    </div>
                    <Link to={"/AdministratorHome"}>
                        <button className='home-components-button back'>Home</button>
                    </Link>
                </div>
                {confirmDelete && (
                    <div className='home-components-confirmation-message'>
                        <p>Are you sure you want to delete the room "{selectedRoom}"?</p>
                        <div className='home-components-confirmation-actions'>
                            <button onClick={confirmDeleteHandler} className='modal-button cancel'>Yes</button>
                            <button onClick={() => setConfirmDelete(false)} className='modal-button'>No</button>
                        </div>
                    </div>
                )}
                <ModifyRoomModal
                    isOpen={showModifyModal}
                    onClose={closeModal}
                    roomName={selectedRoom}
                    onSave={reloadRoomNames}
                />
            </div>
        </>
    );
}

export default authentication(ManageRooms);
