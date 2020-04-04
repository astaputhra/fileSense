package com.iMatch.etl.exceptions;

import com.iMatch.etl.enums.MessageType;

/**
 * Hexagon Global IT Services (ALL RIGHTS RESERVED) Created with IntelliJ IDEA.
 * User: mayankk Date: 23/11/12, 11:33 PM
 */
public abstract class AbstractHexGenError extends RuntimeException {

    private static final long serialVersionUID = 7735077249520146557L;

    /**
	 * private default constructur
	 */
	protected AbstractHexGenError() {

	}

    protected AbstractHexGenError(String message) {
        super(message);
    }

	protected AbstractHexGenError(String message, Throwable cause) {
		super(message, cause);
	}


	private MessageType _messageType;
	private String _messageCode;
	private String _message;

	protected AbstractHexGenError(MessageType messageType, String messageCode, String message) {
		_messageType = messageType;
		_messageCode = messageCode;
		_message = message;
	}

	public MessageType getMessageType() {
		return _messageType;
	}

	public String getMessageCode() {
		return _messageCode;
	}

	@Override
	public String getMessage() {
		return _message;
	}
}
