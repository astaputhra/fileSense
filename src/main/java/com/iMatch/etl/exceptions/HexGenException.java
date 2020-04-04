/**
 * HEXAGON GLOBAL CONFIDENTIAL
 * All right reserved (c) 2012
 */
package com.iMatch.etl.exceptions;

import com.iMatch.etl.IHexGenExceptionMessage;

/**
 * @author mayankk
 * 
 */
public class HexGenException extends Exception {

    private static final long serialVersionUID = -1273652960421119108L;
    private IHexGenExceptionMessage message;

	/**
	 * private constructor so that users are forced to create this exception
	 * with the correct error message.
	 */
	private HexGenException() {

	}

	public HexGenException(IHexGenExceptionMessage message) {
		super(message.getMessage());
		this.message = message;
	}

}
