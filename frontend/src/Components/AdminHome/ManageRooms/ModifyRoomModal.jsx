import React from 'react';

const ModifyRoomModal = ({ isOpen, onClose, roomName, onSave }) => {
    if (!isOpen) return null;

    const handleSubmit = (event) => {
        event.preventDefault();
        onSave(roomName);
    };

    return (
        <div className="modal" tabIndex="-1" role="dialog">
            <div className="modal-dialog" role="document">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Modify Room "{roomName}"</h5>
                    </div>
                    <div className="modal-body">
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label htmlFor="roomName">Room Name:</label>
                                <input type="text" className="form-control" id="roomName" value={roomName || ''} onChange={(e) => onSave(e.target.value)} />
                            </div>
                            <button type="submit" className="btn btn-primary">Save changes</button>
                        </form>
                    </div>
                    <div className="modal-footer">
                        <button type="button" className="btn btn-secondary" onClick={onClose}>Close</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ModifyRoomModal;
