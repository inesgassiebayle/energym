import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './CreateRoom.css';

const CreateRoom = () => {
    let navigate = useNavigate();
    const [className, setClassName] = useState('');
    const [activity, setActivity] = useState('');
    const [capacity, setCapacity] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        // Aquí implementarías la lógica para crear la clase
        console.log(`Creating class: ${className}, Activity: ${activity}, Capacity: ${capacity}`);
        navigate('/ManageRooms'); // Vuelve a ManageRooms después de crear la clase
    };

    return (
        <div className='container-create-class'>
            <h1>Create Room</h1>
            <form onSubmit={handleSubmit}>
                <input
                    type='text'
                    value={className}
                    onChange={(e) => setClassName(e.target.value)}
                    placeholder='Room Name'
                    required
                />
                <select value={activity} onChange={(e) => setActivity(e.target.value)} required>
                    <option value=''>Select Activity</option>
                    <option value='Spinning'>Spinning</option>
                    <option value='Pilates'>Pilates</option>
                    {/* Añade más opciones según sea necesario */}
                </select>
                <input
                    type='number'
                    value={capacity}
                    onChange={(e) => setCapacity(e.target.value)}
                    placeholder='Capacity'
                    required
                />
                <div className='form-actions'>
                    <button type='submit'>Confirm</button>
                    <button type='button' onClick={() => navigate('/ManageRooms')}>Cancel</button>
                </div>
            </form>
        </div>
    );
}

export default CreateRoom;
