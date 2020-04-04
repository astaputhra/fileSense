package com.iMatch.etl.datauploader.internal.hexFileSense;

import java.util.ArrayList;
import java.util.List;

public class DbfSignature extends SignatureOfEtlFlow {
    private List<String> colNames = new ArrayList<String>();

	public List<String> getColNames() {
        return colNames;
    }

    public void setColNames(List<String> colNames) {
        this.colNames = colNames;
    }

    public void addColName(String colname){
        this.colNames.add(colname);
    }
}
