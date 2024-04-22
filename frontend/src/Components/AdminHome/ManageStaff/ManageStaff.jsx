import './ManageStaff.css';
import React, {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import logo from '../../Assets/Logo.png';
import person_icon from "../../Assets/person.png";
import axios from "axios";
import authentication from "../Hoc/Hoc";

const ManageStaff = () => {
    let navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [trainerNames, setTrainerNames] = useState([]);
    const [selectedTrainer, setSelectedTrainer] = useState('');
    const [confirmDelete, setConfirmDelete] = useState(false);

    useEffect(() => {
        const fetchTrainerNames = async () => {
            try {
                const response = await axios.get('http://localhost:3333/professor/get');
                setTrainerNames(response.data);
            } catch (error) {
                console.error('Error fetching trainer names:', error);
            }
        };
        fetchTrainerNames();
    }, []);

    const confirmDeleteHandler = async () => {
        try {
            await axios.delete(`http://localhost:3333/user/${selectedTrainer}/delete`);
            navigate('/AdministratorHome');
        } catch (error) {
            console.error('Error deleting trainer:', error);
        }
    };

    const handleDelete = async (username) => {
        setSelectedTrainer(username);
        setConfirmDelete(true);
    };

    const handleView = async (username) => {
        setSelectedTrainer(username);
        navigate(`/AdministratorHome/staff/${username}`);
    };

    return (
        <div className='manage-staff-container'>
            <div className="manage-staff-header">
                <div className="manage-staff-title">
                    <div className="text">Manage Staff</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='staff-actions'>
                <div className='room-list'>
                    {trainerNames && trainerNames.length > 0 &&
                        trainerNames.map((trainer, index) => (
                            <div key={index} className='staff-item'>
                                <span>{trainer}</span>
                                <button className='modification-button' onClick={() => handleDelete(trainer)}>Delete</button>
                                <button className='modification-button' onClick={() => handleView(trainer)}>View</button>
                            </div>
                        ))
                    }
                </div>
                <Link to={"/AdministratorHome"}>
                    <button className='staff-button back'>Home</button>
                </Link>
            </div>
            {confirmDelete && (
                <div className='confirmation-message'>
                    <p>Are you sure you want to delete the trainer "{selectedTrainer}"?</p>
                    <div className='confirmation-actions'>
                        <button onClick={confirmDeleteHandler}>Yes</button>
                        <button onClick={() => setConfirmDelete(false)} className='cancel'>No</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default authentication(ManageStaff);
