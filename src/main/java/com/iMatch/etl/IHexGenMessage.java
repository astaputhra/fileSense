package com.iMatch.etl;

import com.iMatch.etl.enums.MessageType;

import java.io.Serializable;

/**
 * Hexagon Global IT Services (ALL RIGHTS RESERVED) Created with IntelliJ IDEA.
 * User: mayankk Date: 23/11/12, 11:28 PM
 */
public interface IHexGenMessage extends Serializable {

	public MessageType getMessageType();

	public String getMessageCode();

	public String getMessage();

}
