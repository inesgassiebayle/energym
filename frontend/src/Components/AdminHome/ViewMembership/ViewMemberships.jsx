import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../../HomeComponents.css';
import authentication from "../Hoc/Hoc";
import logo from "../../Assets/Logo.png";

const ViewMemberships = () => {
    let navigate = useNavigate();
    const [memberships, setMemberships] = useState([]);
    const [errorMessage, setErrorMessage] = useState('');

    useEffect(() => {
        fetchMemberships();
    }, []);

    const fetchMemberships = async () => {
        try {
            const response = await axios.get('http://localhost:3333/student/membership');
            setMemberships(response.data);
        } catch (error) {
            console.error('Error fetching memberships:', error);
            setErrorMessage('Failed to fetch memberships.');
        }
    };

    return (
        <div className='home-components-container'>
            <div className="home-components-header">
                <div className="home-components-title">
                    <div className="home-components-text">View Memberships</div>
                </div>
                <div className="logo">
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='home-components-actions'>
                {errorMessage && <div className="error-message">{errorMessage}</div>}
                {memberships.length > 0 ? (
                    memberships.map((membership, index) => (
                        <div key={index} className="membership-entry">
                            <div>Username: {membership.username}</div>
                            <div>Membership Status: {membership.expiration ? `Expires on ${membership.expiration}` : 'Unsubscribed/Unpaid'}
                            </div>
                        </div>
                    ))
                ) : (
                    <div>No memberships found.</div>
                )}
                <Link to={"/AdministratorHome"}><button className='button logout'>Home</button></Link>
            </div>
        </div>
    );
};


export default authentication(ViewMemberships);
