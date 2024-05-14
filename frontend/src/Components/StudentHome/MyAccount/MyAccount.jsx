import React, { useState } from 'react';
import PasswordChangeModal from './PasswordChangeModal';
import './MyAccount.css';
import {Link, useParams} from 'react-router-dom';
import studentHome from "../StudentHome";

const MyAccountPage = () => {
    const [isChangePasswordModalOpen, setChangePasswordModalOpen] = useState(false);
    const { username } = useParams();


    const openChangePasswordModal = () => {
        setChangePasswordModalOpen(true);
    };

    const closeChangePasswordModal = () => {
        setChangePasswordModalOpen(false);
    };

    return (
        <div className="my-account-container">
            <h2>My Account</h2>
            <button className={"button"} onClick={openChangePasswordModal}>Change Password</button>
            {isChangePasswordModalOpen && (
                <PasswordChangeModal onClose={closeChangePasswordModal} />
            )}
            <Link to={`/student/${username}`}>
                <button className="staff-button back">Home</button>
            </Link>
        </div>
    );
};


export default MyAccountPage;
