import React from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminHome.css';
import logo from "../Assets/Logo.png";

const AdminHome = () => {
    let navigate = useNavigate();

    return (
        <div className='container-admin-home'>
            <div className='logo-admin'>
                <img src={logo} alt="Logo" style={{width: '150px'}}/>
            </div>
            <div className='admin-actions'>
                <button className='admin-button' onClick={() => navigate('/ManageStaff')}>Manage Staff</button>
                <button className='admin-button' onClick={() => navigate('/ManageSchedule')}>Manage Schedule</button>
                <button className='admin-button' onClick={() => navigate('/ManageRooms')}>Manage Rooms</button>
                <button className='admin-button logout' onClick={() => navigate('/')}>Log Out</button>
            </div>
        </div>
    );
}

export default AdminHome;


// import {Link} from "react-router-dom";
// import React from "react";
// import './AdminHome.css';
// import logo from "../Assets/Logo.png"; // Asegúrate de tener el logo en la carpeta correspondiente
//
// const AdminHome = () => {
//     return (
//         <div className='container-admin-home'>
//             <div className='logo-admin'>
//                 <img src={logo} alt="Logo" style={{width: '150px'}}/>
//             </div>
//             <div className='admin-actions'>
//                 <Link to={"/ManageStaff"}><button className='admin-button'>Manage Staff</button></Link>
//                 <Link to={"/ManageSchedule"}><button className='admin-button'>Manage Schedule</button></Link>
//                 <Link to={"/ManageRooms"}><button className='admin-button'>Manage Rooms</button></Link>
//                 <Link to={"/"}><button className='admin-button logout'>Log Out</button></Link>
//             </div>
//         </div>
//     )
// }
//
// export default AdminHome;
