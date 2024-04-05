import React from 'react';
import {Link, useNavigate} from 'react-router-dom';
import './ManageRooms.css';
import logo from "../../Assets/Logo.png";

const ManageRooms = () => {
    let navigate = useNavigate();

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
                        onClick={() => navigate('/AdministratorHome/ManageRooms/CreateRoom')}>Create Room
                </button>
                <button className='rooms-button'
                        onClick={() => navigate('/AdministratorHome/ManageRooms/DeleteRoom')}>Delete Room
                </button>
                <Link to={"/AdministratorHome"}><button className='schedule-button back'>Home</button></Link>
            </div>
        </div>
    );
}

export default ManageRooms;
