package com.iMatch.etl.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: anish
 * Date: 9/5/13
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvalidTokenException extends Exception {
    private static final long serialVersionUID = -6181556284425246273L;

    public InvalidTokenException(String errMsg){
        super(errMsg);
    }
}
