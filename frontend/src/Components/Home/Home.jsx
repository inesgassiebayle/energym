import React from 'react'
import './Home.css'
import logo from "../Assets/Logo.png";
import {Link} from "react-router-dom";

const Home = ()=>{
    return (
        <div className='container'>
            <div className='logo'>
                <img src={logo} alt="" style={{width: '300px'}}/>
            </div>
            <Link to={"/Login"}>
                <button className='home-button'>Login</button></Link>
            <Link to={"/SignUp"}><button className='home-button'>Sign Up</button></Link>
        </div>
    )
}
export default Home