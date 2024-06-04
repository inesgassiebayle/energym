import React from 'react';
import { useNavigate } from 'react-router-dom';
import axios from "axios";
import '../Modal.css';
import authentication from "./Common/Hoc/Authentication";


const DeleteConfirmationModal = ({ isOpen, onClose }) => {
    let navigate = useNavigate();

    const handleDelete = async () => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                await axios.delete('http://localhost:3333/user/delete', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                localStorage.removeItem('token');
                navigate('/login');
            } catch (error) {
                console.error('Failed to delete the account:', error);
            }
        }
    };

    if (!isOpen) return null;

    return (
        <div className="modal">
            <div className="modal-content">
                <div className="modal-header">
                    <h5 className="modal-title">Confirm Deletion</h5>
                </div>
                <div className="modal-body">
                    <p>Are you sure you want to delete your account? This action cannot be undone.</p>
                </div>
                <div className="modal-footer">
                    <button onClick={onClose} className="cancel">Cancel</button>
                    <button onClick={handleDelete} className="confirm">Delete</button>
                </div>
            </div>
        </div>
    );
};

export default authentication(DeleteConfirmationModal);
