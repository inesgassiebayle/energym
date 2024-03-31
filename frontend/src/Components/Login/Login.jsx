import logo from "../Assets/Logo.png";
import {Link} from "react-router-dom";
import {useState} from "react";
import React from "react";
import './Login.css'
import person_icon from "../Assets/person.png";
import email_icon from "../Assets/email.png";
import password_icon from "../Assets/password.png";
import axios from "axios";

const Login = ()=>{
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const handleLogin = async () => {
        try {
            const response = await axios.get(`http://localhost:3333/user/login`, {
                params: {
                    username: username,
                    password: password,
                },
            });
            // Manejar la respuesta del backend aquí
            console.log(response.data);
        } catch (error) {
            console.error("Error de inicio de sesión:", error);
        }
    };

    return (
        <div className="container-login">

            <div className='header'>
                <div className='title'>
                    <div className='text'>Login</div>
                    <div className='underline'></div>
                </div>
                <div className='logo'>
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='inputs'>
                <div className='input'>
                    <img src={person_icon} alt=""/>
                    <input type='text' placeholder='Username' value={username} onChange={(e) => setUsername(e.target.value)}/>
                </div>
                <div className='input'>
                    <img src={password_icon} alt=""/>
                    <input type='password' placeholder='Password' value={password} onChange={(e) => setPassword(e.target.value)}/>
                </div>
            </div>
            <div className='forgot-password'>Forgot your password? <Link to={"/Login/RestorePassword"}><button>Click Here!</button></Link></div>
            <button className='login-button' onClick={handleLogin}>Login</button>
        </div>
    )
}
export default Login