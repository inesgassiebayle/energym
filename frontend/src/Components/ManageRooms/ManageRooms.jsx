import React from 'react';
import { useNavigate } from 'react-router-dom';
import './ManageRooms.css';

const ManageRooms = () => {
    let navigate = useNavigate();

    return (
        <div className='container-manage-rooms'>
            <h1>Manage Rooms</h1>
            <div className='rooms-actions'>
                <button className='rooms-button' onClick={() => navigate('/CreateRoom')}>Create Room</button>
                <button className='rooms-button' onClick={() => navigate('/DeleteRoom')}>Delete Room</button>
            </div>
        </div>
    );
}

export default ManageRooms;
