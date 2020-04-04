package com.iMatch.etl.exceptions;


import com.iMatch.etl.IHexGenInternalErrorMessage;

/**
 * Hexagon Global IT Services (ALL RIGHTS RESERVED)
 * Created with IntelliJ IDEA.
 * User: mayankk
 * Date: 23/11/12
 * Time: 11:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class HexGenInternalError extends AbstractHexGenError {

    private static final long serialVersionUID = 5900831823616729124L;
    /**
     * private constructor so that users are forced to create this exception with the correct error message.
     */

    IHexGenInternalErrorMessage hexGenInternalErrorMessage;

    private HexGenInternalError() {

    }

    public HexGenInternalError(IHexGenInternalErrorMessage message) {
        super(message.getMessage());
        this.hexGenInternalErrorMessage = message;
    }

    public HexGenInternalError(IHexGenInternalErrorMessage message, Throwable cause) {
        super(message.getMessage(), cause);
        this.hexGenInternalErrorMessage = message;
    }

    public IHexGenInternalErrorMessage getHexGenInternalErrorMessage() {
        return hexGenInternalErrorMessage;
    }

    public void setHexGenInternalErrorMessage(IHexGenInternalErrorMessage hexGenInternalErrorMessage) {
        this.hexGenInternalErrorMessage = hexGenInternalErrorMessage;
    }
}
