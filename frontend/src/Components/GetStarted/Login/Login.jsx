import logo from "../../Assets/Logo.png";
import {Link, useNavigate} from "react-router-dom";
import {useState} from "react";
import React from "react";
import './Login.css'
import person_icon from "../../Assets/person.png";
import password_icon from "../../Assets/password.png";
import axios from "axios";

const Login = ()=>{
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const [loginError, setLoginError] = useState('');


    const login = async () => {
        try {
            const response = await axios.post(`http://localhost:3333/user/login`, {
                username: username,
                password: password
            });

            localStorage.setItem('token', response.data.token);
            console.log("Inicio de sesión exitoso:", response.data.token);

            const userData = response.data;

            if (userData.type === 'STUDENT') {
                navigate('/StudentHome');
            } else if (userData.type === 'ADMINISTRATOR') {
                navigate('/AdministratorHome');
            } else if(userData.type === 'PROFESSOR'){
                navigate('/ProfessorHome')
            }
            else {
                console.log(userData);
            }
        } catch (error) {
            const errorMsg = error.response?.data || 'An unexpected error occurred.';
            console.error('Error while sending request:', errorMsg);
            setLoginError('');
            if (errorMsg.includes("User does not exist")|| errorMsg.includes("User not found")) {
                setLoginError("User or password incorrect");
            }
        }
    };

    return (
        <div className="login-container">
            <div className='login-header'>
                <div className='login-title'>
                    <div className='text'>Login</div>
                </div>
                <div className='logo'>
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='login-inputs'>

                <div className='login-input'>
                    <img src={person_icon} alt=""/>
                    <input type='text' placeholder='Username' value={username} onChange={(e) => {
                        setUsername(e.target.value)
                        setLoginError('');
                    }}/>
                </div>
                <div className='login-input'>
                    <img src={password_icon} alt=""/>
                    <input type='password' placeholder='Password' value={password} onChange={(e) => {
                        setPassword(e.target.value)
                        setLoginError('');
                    }}
                    />

                </div>
                {loginError && <div className="error-message" style={{ color: 'red', textAlign: 'center' }}>{loginError}</div>}

            </div>
            <div className='forgot-password'>Forgot your password? <Link to={"/Login/RestorePassword"}><button>Click Here!</button></Link></div>
            <button className='login-button' onClick={login}>Login</button>

        </div>
    )
}
export default Login