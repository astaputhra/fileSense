package com.iMatch.etl.exceptions;

/**
 * Created by anishjoseph on 03/03/15.
 */
public class ReviewAlreadyActedOnException extends RuntimeException {
    private static final long serialVersionUID = -360477502987449431L;

    public ReviewAlreadyActedOnException(String message) {
        super(message);
    }
}
