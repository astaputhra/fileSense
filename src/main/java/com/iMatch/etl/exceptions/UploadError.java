package com.iMatch.etl.exceptions;

import com.iMatch.etl.enums.UploadErrorType;

import java.io.Serializable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: anish
 * Date: 6/28/13
 * Time: 9:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class UploadError implements Serializable{
    private static final long serialVersionUID = 3395868581981204608L;
    private Map<String, String> attrs;
    private UploadErrorType errorType;

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }

    public UploadErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(UploadErrorType errorType) {
        this.errorType = errorType;
    }

    public UploadError(Map<String, String> attrs, UploadErrorType errorType) {
        this.attrs = attrs;
        this.errorType = errorType;
    }
    public UploadError(){}
}
