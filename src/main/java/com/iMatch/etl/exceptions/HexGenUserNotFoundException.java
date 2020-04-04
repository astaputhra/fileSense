/**
 * HEXAGON GLOBAL CONFIDENTIAL
 * All right reserved (c) 2012
 */
package com.iMatch.etl.exceptions;

/**
 * @author mayankk
 *
 */
public class HexGenUserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 2207363484795881003L;

    public HexGenUserNotFoundException(String userName) {
	    super(userName);
    }
	
}
