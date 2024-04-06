import {Link, useNavigate} from "react-router-dom";
import logo from "../../Assets/Logo.png";
import React from "react";
import './ManageActivities.css'

const ManageActivities = () => {
    let navigate = useNavigate();

    return (
        <div className='manage-activities-container'>
            <div className="manage-activities-header">
                <div className="manage-activities-title">
                    <div className="text">Manage Activities</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='activities-actions'>
                <button className='activities-button'
                        onClick={() => navigate('/AdministratorHome/ManageActivities/AddActivity')}>Add Activity
                </button>
                <button className='activities-button'
                        onClick={() => navigate('/AdministratorHome/ManageActivities/DeleteActivity')}>Delete Activity
                </button>
                <Link to={"/AdministratorHome"}><button className='activities-button back'>Home</button></Link>
            </div>
        </div>
    );
}
export default ManageActivities;
