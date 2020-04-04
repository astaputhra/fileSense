package com.iMatch.etl.datauploader.internal.hexFileSense;

public class TextSignature extends SignatureOfEtlFlow {
    private String containsExpression;
    private int lineNum;
    private String regex;

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public String getContainsExpression() {
        return containsExpression;
    }

    public void setContainsExpression(String containsExpression) {
        this.containsExpression = containsExpression;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
