package com.iMatch.etl.models;


import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: akash
 * Date: 10/6/13
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdError implements Serializable {
    private int rowNum;
    private String message;
    private String field;

    public UpdError(int rowNum, String message, String field) {
        this.rowNum = rowNum;
        this.message = message;
        this.field = field;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
