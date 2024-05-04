import React, { useEffect, useState } from 'react';
import axios from "axios";
import star from '../../Assets/star2.png';
import {useNavigate} from "react-router-dom";
import spinner from "../../Assets/spinning-loading.gif";

const ClassInfoModal = ({ isOpen, onClose, lessonName, date, time, username}) => {
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

    const handleSelect = (event) => {
        const selectedOptions = Array.from(event.target.selectedOptions, option => option.value);
        setSelectedStudents(selectedOptions);
    };

    useEffect(() => {
        if (!isOpen) return;

        fetchStudents();
    }, [isOpen, navigate]);

    if (!isOpen) return null;

    return (
        <div className="modal" tabIndex="-1" role="dialog">
            <div className="modal-header">
                <h5 className="modal-title">Assistance for "{lessonName}"</h5>
            </div>

            <div className="modal-body">
                <select multiple={true} value={selectedStudents} onChange={handleSelect}>
                    {students.map((student, index) => (
                        <option key={index} value={student}>{student}</option>
                    ))}
                </select>

            </div>

            <div className="modal-footer">
                <button className="cancel" onClick={onClose}>Close</button>
            </div>
        </div>
    );
};

export default ClassInfoModal;
