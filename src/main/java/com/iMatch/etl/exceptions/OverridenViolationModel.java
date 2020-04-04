package com.iMatch.etl.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: shreyas
 * Date: 22/1/15
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class OverridenViolationModel {
    private String index;
    private String violationMessage;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getViolationMessage() {
        return violationMessage;
    }

    public void setViolationMessage(String violationMessage) {
        this.violationMessage = violationMessage;
    }
}
