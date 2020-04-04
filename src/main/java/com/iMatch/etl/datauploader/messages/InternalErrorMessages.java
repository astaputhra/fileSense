package com.iMatch.etl.datauploader.messages;

import com.iMatch.etl.enums.MessageType;

/**
 * Hexagon Global IT Services (ALL RIGHTS RESERVED)
 * Created with IntelliJ IDEA.
 * User: mayankk
 * Date: 17/12/12, 3:15 PM
 */
public enum InternalErrorMessages {

	INTERNAL_ERROR_OCCURED(1, MessageType.ERROR,"core.error");


	private MessageType _messageType;
	private String _messageKey;
	private String _messageCode;

	private static final String MESSAGE_CODE_PREFIX = "HEX-I";
	private static final String MESSAGE_KEY_PREFIX = "internal";

	private InternalErrorMessages(long messageCode, MessageType messageType, String messageKey) {
		_messageCode = String.format("%s%05d",MESSAGE_CODE_PREFIX, messageCode);
		_messageType = messageType;
		_messageKey = String.format("%s.%05d.%s",MESSAGE_KEY_PREFIX,messageCode, messageKey);
	}

	public MessageType getMessageType() {
		return _messageType;
	}

	public String getMessageKey() {
		return _messageKey;
	}

	public String getMessageCode() {
		return _messageCode;
	}
}
