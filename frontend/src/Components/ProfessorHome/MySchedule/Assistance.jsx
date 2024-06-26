import React, { useEffect, useState } from 'react';
import axios from "axios";
import { useNavigate } from "react-router-dom";
import spinner from "../../Assets/spinning-loading.gif";
import authentication from "../Common/Hoc/Authentication";
import './CheckList.css'

const Assistance = ({ isOpen, onClose, lessonName, date, time, username }) => {
    let navigate = useNavigate();
    const [students, setStudents] = useState([]);
    const [loadingStudents, setLoadingStudents] = useState(false);
    const [selectedStudents, setSelectedStudents] = useState([]);

    useEffect(() => {
        const fetchStudents = async () => {
            try {
                setLoadingStudents(true);
                const response = await axios.get('http://localhost:3333/lesson/students', {
                    params: {
                        username: username,
                        startDate: date,
                        time: time
                    }
                });

                if (Array.isArray(response.data)) {
                    setStudents(response.data);
                } else {
                    const studentsArray = Object.keys(response.data).map(key => ({
                        username: key,
                        hasAssisted: response.data[key]
                    }));
                    setStudents(studentsArray);
                    setSelectedStudents(studentsArray.filter(s => s.hasAssisted).map(s => s.username));
                    console.log(studentsArray);
                }
            } catch (error) {
                console.error('Error fetching students enrolled:', error);
            } finally {
                setLoadingStudents(false);
            }
        };

        if (isOpen) {
            fetchStudents();
        }
    }, [isOpen, username, date, time]);


    const handleSelect = (event, studentName) => {
        if (event.target.checked) {
            setSelectedStudents(prevSelected => [...prevSelected, studentName]);
        } else {
            setSelectedStudents(prevSelected => prevSelected.filter(s => s !== studentName));
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
                                    value={student.username}
                                    checked={selectedStudents.includes(student.username)}
                                    onChange={(e) => handleSelect(e, student.username)}
                                />
                                <label>{student.username}</label>
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
