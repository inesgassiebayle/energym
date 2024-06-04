import React, {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import logo from '../../Assets/Logo.png';
import axios from "axios";
import authentication from "../Hoc/Hoc";
import spinner from "../../Assets/spinning-loading.gif";

const ManageStaff = () => {
    let navigate = useNavigate();
    const [trainerNames, setTrainerNames] = useState([]);
    const [selectedTrainer, setSelectedTrainer] = useState('');
    const [confirmDelete, setConfirmDelete] = useState(false);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchTrainerNames = async () => {
            try {
                setLoading(true);
                const response = await axios.get('http://localhost:3333/professor/get');
                setTrainerNames(response.data);
            } catch (error) {
                console.error('Error fetching trainer names:', error);
            } finally {
                setLoading(false);
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
        <div className='home-components-container'>
            <div className="home-components-header">
                <div className="home-components-title">
                    <div className="home-components-text">Manage Staff</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='home-components-actions'>
                <div className='home-components-subtitle'>
                    {trainerNames && trainerNames.length > 0 &&
                        trainerNames.map((trainer, index) => (
                            <div key={index} className='home-components-item'>
                                <span>{trainer}</span>
                                <button className='home-components-modification-button' onClick={() => handleDelete(trainer)}>Delete</button>
                                <button className='home-components-modification-button' onClick={() => handleView(trainer)}>View</button>
                            </div>
                        ))
                    }
                </div>
                <Link to={"/AdministratorHome"}>
                    <button className='home-components-button back'>Home</button>
                </Link>
            </div>
            {confirmDelete && (
                <div className='modal'>
                    <p>Are you sure you want to delete the trainer "{selectedTrainer}"?</p>
                    <div className='modal-footer'>
                        <button onClick={confirmDeleteHandler} className="modal-button">Yes</button>
                        <button onClick={() => setConfirmDelete(false)} className="modal-button cancel">No</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default authentication(ManageStaff);
