import React, {useState} from 'react'
import './LoginSignup.css'

import email_icon from '../Assets/email.png'
import password_icon from '../Assets/password.png'
import person_icon from '../Assets/person.png'
import logo from '../Assets/Logo.png'



const LoginSignup = ()=>{
    const [action, setAction] = useState("Login");
    return(
        <div className="container">

            <div className='header'>
                <div className='title'>
                    {action === "Login" ? <div className='text'>Login</div> : <div className='text'> Sign Up </div>}
                    <div className='underline'></div>
                </div>
                <div className='logo'>
                    <img src={logo} alt=""/>
                </div>
            </div>
            <div className='inputs'>
                {action === "Login" ? <div></div> :
                    <div className='input'>
                        <img src={person_icon} alt=""/>
                        <input type='first name' placeholder='First name'/>
                    </div>}
                {action === "Login" ? <div></div> :
                    <div className='input'>
                        <img src={person_icon} alt=""/>
                        <input type='last name' placeholder='Last name'/>
                    </div>}

                <div className='input'>
                    <img src={person_icon} alt=""/>
                    <input type='username' placeholder='Username'/>
                </div>
                {action === "Login" ? <div></div> :
                    <div className='input'>
                        <img src={email_icon} alt=""/>
                        <input type='email' placeholder='Email'/>
                    </div>}
                <div className='input'>
                    <img src={password_icon} alt=""/>
                    <input type='password' placeholder='Password'/>
                </div>
            </div>
            {action === "Sign Up" ? <div></div> :
                <div className='forgot-password'>Forgot your password? <span>Click Here</span></div>}
            {action === "Login" ? <div></div> :
                <div className='trainer-signup'>Are you a trainer? <span>Sign Up Here</span></div>}

            <div className='submit-container'>
                <div className={action === "Login" ? "submit gray" : "submit"} onClick={() => {
                    setAction("Sign Up")
                }}>Sign Up
                </div>
                <div className={action === "Sign Up" ? "submit gray" : "submit"} onClick={() => {
                    setAction("Login")
                }}>Login
                </div>
            </div>
        </div>
    )
}

export default LoginSignup