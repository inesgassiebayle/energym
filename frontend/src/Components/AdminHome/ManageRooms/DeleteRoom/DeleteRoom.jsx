import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './DeleteRoom.css';

const DeleteRoom = () => {
    let navigate = useNavigate();
    const [roomName, setRoomName] = useState('');
    const [confirmDelete, setConfirmDelete] = useState(false);

    const handleSubmit = (e) => {
        e.preventDefault();
        // Aquí implementarías la lógica para eliminar la sala
        console.log(`Deleting room: ${roomName}`);
        setConfirmDelete(false); // Restablecer el estado después de la eliminación
        navigate('/AdminHome'); // Vuelve a AdminHome después de eliminar la sala
    };

    return (
        <div className='container-delete-room'>
            <h1>Delete Room</h1>
            {!confirmDelete ? (
                <form onSubmit={() => setConfirmDelete(true)}>
                    <input
                        type='text'
                        value={roomName}
                        onChange={(e) => setRoomName(e.target.value)}
                        placeholder='Room Name'
                        required
                    />
                    <div className='form-actions'>
                        <button type='submit'>Confirm</button>
                        <button type='button' onClick={() => navigate('/AdminHome')} className='cancel'>Cancel</button>
                    </div>
                </form>
            ) : (
                <div className='confirmation-message'>
                    <p>Are you sure you want to delete the room "{roomName}"?</p>
                    <div className='confirmation-actions'>
                        <button onClick={handleSubmit}>Yes</button>
                        <button onClick={() => setConfirmDelete(false)} className='cancel'>No</button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default DeleteRoom;
