import React, { useEffect, useState } from 'react';
import axios from "axios";
import spinner from "../../Assets/spinning-loading.gif";
import authentication from "../Common/Hoc/Authentication";
import './CheckList.css';

const StudentsModal = ({ isOpen, onClose, lessonName, date, time, username }) => {
    const [students, setStudents] = useState([]);
    const [loadingStudents, setLoadingStudents] = useState(false);

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
                    students.length === 0 ? (
                        <p>No students have enrolled yet</p>
                    ) : (
                        <ul className="students-list">
                            {students.map((student, index) => (
                                <li key={index}>
                                    <label>{student.username}</label>
                                </li>
                            ))}
                        </ul>
                    )
                )}
            </div>

            <div className="modal-footer">
                <button className="cancel" onClick={onClose}>Close</button>
            </div>
        </div>
    );
};

export default authentication(StudentsModal);
