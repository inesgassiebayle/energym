import React, { useEffect, useState } from 'react';
import {useNavigate, useSearchParams} from 'react-router-dom';
import './ModifyRoom.css';
import axios from "axios";

const ModifyRoom = () => {
    let navigate = useNavigate();

    const [confirmModify, setConfirmModify] = useState(false);

    const [queryParameters] = useSearchParams()
    const room = queryParameters.get("name");

    const handleSubmit = async () => {
        try {
            const response = await axios.post(`http://localhost:3333/room/modify`, {

            });
            console.log('Room modified:', response.data);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error('Error modifying room:', error);
        }
    };

    return (
        <div className='modify-room-container'>
            <div className="modify-room-header">
                <div className="modify-room-title">
                    <div className="text">Modify Room</div>
                </div>
                <div className="logo">
                </div>
            </div>
            {!confirmModify ? (
                <form onSubmit={() => setConfirmModify(true)}>

                    <div className='form-actions'>
                        <button type='submit'>Confirm</button>
                        <button type='button' onClick={() => navigate('/AdministratorHome')} className='cancel'>Cancel</button>
                    </div>
                </form>
            ) : (
                <div className='confirmation-message'>
                    <p>Are you sure you want to apply changes on the room '{room}'?</p>
                    <div className='confirmation-actions'>
                        <button onClick={handleSubmit}>Yes</button>
                        <button onClick={() => setConfirmModify(false)} className='cancel'>No</button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default ModifyRoom;
