import React, { useEffect, useState } from 'react';
import axios from "axios";
import {useNavigate} from "react-router-dom";

const MoreModal = ({ isOpen, onClose, lesson }) => {
    let navigate = useNavigate(); // Added useNavigate hook
    const [username, setUsername] = useState('');

    useEffect(() => {
        if (!isOpen) return;

    }, [isOpen, navigate]);

    if (!isOpen) return null;

    return (
        <div className="modal" tabIndex="-1" role="dialog">
            <div className="modal-dialog" role="document">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Details for "{lesson}"</h5> {/* Changed from Modify Room */}
                    </div>
                    <div className="modal-body">
                        {/* Additional lesson details can go here */}
                        <p>Start Date: {lesson}</p>
                        {/* Other lesson details */}
                    </div>
                    <div className="modal-footer">
                        <button className="cancel" onClick={onClose}>Close</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MoreModal;
