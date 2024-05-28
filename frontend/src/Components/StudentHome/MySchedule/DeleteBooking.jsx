import React, { useState } from 'react';
import axios from 'axios';
import spinner from "../../Assets/spinner.svg";

const Booking = ({isOpen, onClose, username, lessonName, lessonProfessor, lessonTime, lessonDate, concurrency, day, startDay, endDay}) => {
    const [isRecurring, setIsRecurring] = useState(false);
    const [validDates, setValidDates] = useState([]);
    const [selectedStartDate, setSelectedStartDate] = useState('');
    const [selectedEndDate, setSelectedEndDate] = useState('');
    const [loading, setLoading] = useState(false); // New loading state

    const [dateError, setDateError] = useState('');

    if (!isOpen) return null;

    const handleClose = () => {
        onClose();
    };

    const getValidDates = (start, end, targetDay) => {
        const dates = [];
        let current = new Date(`${start.year}-${start.month}-${start.day}`);
        const last = new Date(`${end.year}-${end.month}-${end.day}`);
        const targetDayIndex = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'].indexOf(targetDay.toUpperCase());
        console.log('targetDayIndex:', targetDayIndex);
        while (current <= last) {
            if (current.getDay() === targetDayIndex) {
                dates.push(current.toISOString().split('T')[0]);
            }
            current.setDate(current.getDate() + 1);
        }
        console.log(dates);
        return dates;
    };

    const handleDelete = async () => {
        try {
            setLoading(true);
            if (isRecurring && concurrency) {
                const response =  await axios.delete('http://localhost:3333/student/booking', {
                    params: {
                        professor: lessonProfessor,
                        student: username,
                        startDate: selectedStartDate,
                        endDate: selectedEndDate,
                        time: lessonTime
                    }
                });
                console.log(response.data);
                onClose();
            }
            else {
                const response =  await axios.delete('http://localhost:3333/student/booking', {
                    params: {
                        professor: lessonProfessor,
                        student: username,
                        startDate: lessonDate,
                        endDate: lessonDate,
                        time: lessonTime
                    }
                });
                console.log(response.data);
                onClose();
            }
        } catch (error) {
            const errorMsg = error.response?.data || 'An unexpected error occurred.';
            console.error('Error booking lesson:', error);
            setDateError('')
            if (errorMsg.includes("Invalid input")){
                setDateError("End date must be after start date")
            }
        }
        finally {
            setLoading(false);
        }
    };

    const handleRecurring = () => {
        setIsRecurring(!isRecurring);
        if (!isRecurring) {
            setValidDates(getValidDates(startDay, endDay, day));
        }
    };

    const validateDate = (selectedDate) => {
        return validDates.includes(selectedDate);
    };

    const handleStartDateChange = (e) => {
        const selectedDate = e.target.value;
        if (validateDate(selectedDate)) {
            setSelectedStartDate(selectedDate);
            setDateError('');
        } else {
            setSelectedStartDate(''); // Reset if invalid
        }
    };

    const handleEndDateChange = (e) => {
        const selectedDate = e.target.value;
        if (validateDate(selectedDate)) {
            setSelectedEndDate(selectedDate);
            setDateError('');
        } else {
            setSelectedEndDate(''); // Reset if invalid
        }
    };

    return (
        <div className="modal">
            <div className="modal-header">
                <h5 className="modal-title">Book {lessonName}</h5>
                <button onClick={handleClose} className="modal-close-button">&times;</button>
            </div>
            <div className="modal-body">
                <label className="modal-label">
                    Cancel booking for more than one week:
                    <input type="checkbox" checked={isRecurring} onChange={handleRecurring}
                           className="modal-checkbox"/>
                </label>

                {isRecurring && concurrency && (
                    <>
                        <div className="date-container">
                            <label className="modal-label">
                                Start Date:
                                <input
                                    type="date"
                                    value={selectedStartDate}
                                    onChange={handleStartDateChange}
                                    min={`${startDay.year}-${String(startDay.month).padStart(2, '0')}-${String(startDay.day).padStart(2, '0')}`}
                                    max={`${endDay.year}-${String(endDay.month).padStart(2, '0')}-${String(endDay.day).padStart(2, '0')}`}
                                    list="validDates"
                                    className="date-input"
                                />
                            </label>
                            <label className="label">
                                End Date:
                                <input
                                    type="date"
                                    value={selectedEndDate}
                                    onChange={handleEndDateChange}
                                    min={`${startDay.year}-${String(startDay.month).padStart(2, '0')}-${String(startDay.day).padStart(2, '0')}`}
                                    max={`${endDay.year}-${String(endDay.month).padStart(2, '0')}-${String(endDay.day).padStart(2, '0')}`}
                                    list="validDates"
                                    className="date-input"
                                />
                            </label>
                            <datalist id="validDates">
                                {validDates.map(date => (
                                    <option key={date} value={date}/>
                                ))}
                            </datalist>
                        </div>
                    </>
                )}

                {isRecurring && !concurrency && (
                    <p>Lesson is not booked concurrently</p>
                )}
            </div>
            <div className="modal-footer">
                <button type="button" className="cancel" onClick={handleClose}>Cancel</button>
                {loading ? (
                    <img src={spinner} alt="Loading..." style={{width: '50px'}}/>
                ) : (
                    <button type="submit" className="cancel" onClick={handleDelete}>Delete</button>
                )}
            </div>
            {dateError && <div className="error-message" style={{ color: 'red', textAlign: 'center' }}>{dateError}</div>}
        </div>
    );
};


export default Booking;
