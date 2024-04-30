import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from 'react-router-dom';
import axios from "axios";
const authentication = WrappedComponent => {

    return (props) => {
        const {username} = useParams();
        const navigate = useNavigate();

        useEffect(() => {
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

                    if (response.data.type !== 'PROFESSOR') {
                        console.log('User is not an administrator, redirecting to login.');
                        navigate('/login');
                        return;
                    }

                    if(response.data.username !== username){
                        console.log('User is not the same as the one logged in, redirecting to login.');
                        navigate('/login');
                        return;
                    }

                } catch (error) {
                    console.error('Token validation failed:', error);
                    navigate('/login');
                }
            }

            verifyToken();
            }, []);
        return <WrappedComponent {...props} />;
    };
};
export default authentication;