import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import logo from "../../Assets/Logo.png";
import axios from "axios";
import './ManageRooms.css';

const ManageRooms = () => {
    let navigate = useNavigate();

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

    const handleModify = (roomName) => {
        setSelectedRoom(roomName);
        setShowModifyModal(true);
    };

    const confirmDeleteHandler = async () => {
        try {
            const response = await axios.delete(`http://localhost:3333/room/${selectedRoom}/delete`);
            console.log('Room deleted:', response.data);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error('Error deleting room:', error);
        }
    };

    return (
        <div className='manage-rooms-container'>
            <div className="manage-rooms-header">
                <div className="manage-rooms-title">
                    <div className="text">Manage Rooms</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
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
                            <button onClick={() => handleDelete(roomName)}>Delete</button>
                            <button onClick={() => handleModify(roomName)}>Modify</button>
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

            {showModifyModal && (
                <div className="modal" tabIndex="-1" role="dialog">
                    <div className="modal-dialog" role="document">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">Modify Room "{selectedRoom}"</h5>
                                <button type="button" className="close" onClick={() => setShowModifyModal(false)} aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div className="modal-body">
                                {/* Add your form or content for modifying the room here */}
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-primary">Save changes</button>
                                <button type="button" className="btn btn-secondary" onClick={() => setShowModifyModal(false)}>Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}

        </div>
    );
}

export default ManageRooms;
