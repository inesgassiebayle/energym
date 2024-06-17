import React, {useEffect} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import axios from "axios";
const authentication = WrappedComponent => {

    return (props) => {
        const {username} = useParams();
        const navigate = useNavigate();

        useEffect(() => {

            console.log(username);
            const token = localStorage.getItem('token');

            if (!token) {
                navigate('/login');
            }

            const verifyToken = async () => {
                try {
                    const response = await axios.get('http://localhost:3333/user/verify', {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });

                    if (response.data.type !== 'STUDENT') {
                        console.log('User is not a student, redirecting to login.');
                        navigate('/login');
                        return;
                    }

                    if(response.data.username !== username){
                        console.log(response.data.username);
                        console.log(username);
                        console.log('User is not the same as the one logged in, redirecting to login.');
                        navigate('/login');
                        return;
                    }

                } catch (error) {
                    console.error('Token validation failed:', error);
                    navigate('/login');
                }
            }


            const verifyMembership = async () => {
                try {
                    const response = await axios.get(`http://localhost:3333/membership/${username}`);

                    if (response.data === 'True') {
                        console.log('User is a member');
                    }

                    if (response.data === 'False') {
                        console.log('User is not a member, redirecting to payment.');
                        navigate(`/student/${username}/payment`);
                    }

                } catch (error) {
                    console.error('Token validation failed:', error);
                    navigate('/login');
                }
            }

            verifyToken();
            verifyMembership();
        }, []);
        return <WrappedComponent {...props} />;
    };
};
export default authentication;