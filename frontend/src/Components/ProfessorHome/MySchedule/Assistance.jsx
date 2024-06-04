import React, { useEffect, useState } from 'react';
import axios from "axios";
import {useNavigate} from "react-router-dom";
import spinner from "../../Assets/spinning-loading.gif";
import authentication from "../Common/Hoc/Authentication";
import './CheckList.css'

const Assistance = ({ isOpen, onClose, lessonName, date, time, username}) => {
    let navigate = useNavigate(); // Added useNavigate hook
    const [students, setStudents] = useState([]);
    const [loadingStudents, setLoadingStudents] = useState(false);
    const [selectedStudents, setSelectedStudents] = useState([]);

    const fetchStudents = async () => {
        try {
            setLoadingStudents(true)
            const response = await axios.get('http://localhost:3333/lesson/students', {
                params: {
                    username: username,
                    startDate: date,
                    time: time
                }
            })

            console.log(response.data);
            setStudents(response.data);
        } catch (error) {
            console.error('Error fetching students enrolled:', error);
        } finally {
            setLoadingStudents(false);
        }
    };



    const handleSelect = (event, student) => {
        if (event.target.checked) {
            setSelectedStudents(prevSelected => [...prevSelected, student]);
        } else {
            setSelectedStudents(prevSelected => prevSelected.filter(s => s !== student));
        }
    };

    const handleConfirm = async () => {
        try {
            await axios.post('http://localhost:3333/lesson/assistance', {
                date: date,
                time: time,
                professor: username,
                students: selectedStudents.join(',')
            });
            onClose();
        } catch (error) {
            console.error('Error sending assistance:', error);
        }
    };

    useEffect(() => {
        if (!isOpen) return;

        fetchStudents();
    }, [isOpen, navigate]);

    if (!isOpen) return null;

    return (
        <div className="modal" tabIndex="-1" role="dialog">
            <div className="modal-header">
                <h5 className="modal-title">Assistance for {lessonName}</h5>
            </div>

            <div className="modal-body">
                {loadingStudents ? (
                    <img src={spinner} alt="Loading..." />
                ) : (
                    <ul className="students-list">
                        {students.map((student, index) => (
                            <li key={index}>
                                <input
                                    type="checkbox"
                                    value={student}
                                    checked={selectedStudents.includes(student)}
                                    onChange={(e) => handleSelect(e, student)}
                                />
                                <label>{student}</label>
                            </li>
                        ))}
                    </ul>
                )}

            </div>

            <div className="modal-footer">
                <button className="cancel" onClick={onClose}>Close</button>
                <button className="submit" onClick={handleConfirm}>Confirm</button>
            </div>
        </div>
    );
};

export default authentication(Assistance);
