package com.iMatch.etl.datauploader.internal.hexFileSense;

import org.pentaho.di.trans.steps.excelinput.SpreadSheetType;

import java.util.ArrayList;
import java.util.List;

public class ExcelSignature extends SignatureOfEtlFlow {
    private String patternFromFLow;
    private SpreadSheetType spreadSheetType;
    private List<SearchPatterns> patterns = new ArrayList<SearchPatterns>();
    private String sheetname;
    private boolean isPatternRegex = false;

    public boolean isPatternRegex() {
        return isPatternRegex;
    }

    public void setPatternRegex(boolean patternRegex) {
        isPatternRegex = patternRegex;
    }

    public String getSheetname() {
        return sheetname;
    }

    public void setSheetname(String sheetname) {
        this.sheetname = sheetname;
    }

    public void addPattern(int row, int col, String regex)
    {
        this.patterns.add(new SearchPatterns(row, col, regex));
    }
    public List<SearchPatterns> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<SearchPatterns> patterns) {
        this.patterns = patterns;
    }

    public SpreadSheetType getSpreadSheetType() {
        return spreadSheetType;
    }

    public void setSpreadSheetType(SpreadSheetType spreadSheetType) {
        this.spreadSheetType = spreadSheetType;
    }

    public String getPatternFromFLow() {
        return patternFromFLow;
    }

    public void setPatternFromFLow(String patternFromFLow) {
        this.patternFromFLow = patternFromFLow;
    }
    public class SearchPatterns{
        private int row;
        private int col;
        private String regex;

        public SearchPatterns(){}
        public SearchPatterns(int row, int col, String regex)
        {
            this.col = col;
            this.row = row;
            this.regex = regex;
        }
        public String getRegex() {
            return regex;
        }

        public void setRegex(String regex) {
            this.regex = regex;
        }
        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

    }

    public String toString(){
        return this.getEtlFlowName() + "::" + this.getEtlType() + "::" + this.getFileNamePattern() + "::" + this.getDisplayName() + "::" + patternFromFLow + "::" + isPatternRegex + "::" + patterns.size() + "::" + printPatterns();
    }

    private String printPatterns(){
        StringBuilder sb = new StringBuilder();
        for (SearchPatterns pattern : patterns) {
            sb.append(pattern.getRegex() + ",");
        }
        return sb.toString();


    }
}
