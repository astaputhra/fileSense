package com.iMatch.etl.datauploader.internal.hexFileSense;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class HexDBFParser extends AbstractHexParser{
    private static final int RECORD_LENGTH = 32;
    private static final Logger logger = LoggerFactory.getLogger(HexTextParser.class);


    private List<String> getFileSignature(String fileName)
    {

        List<String> colNames = new ArrayList<String>();
        String colName;
        File f = new File(fileName);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(f);
            byte[] col = new byte[RECORD_LENGTH];
            inputStream.read(col);
            while(true){
                inputStream.read(col);
                if(col[0] == 0x0D){
                    return colNames;
                }
                colName = convertBytesToString(col);
                colNames.add(colName.toUpperCase());
            }
        } catch (Exception e){
            e.printStackTrace();
            logger.error("Error while reading DBF file {} - error is {}", fileName, e.getMessage());
        } finally {
            if(inputStream != null) IOUtils.closeQuietly(inputStream);
        }
        return null;
    }

    public String convertBytesToString(byte[] arr)
    {
        int i;
        for (i = 0; i < arr.length && arr[i] != 0; i++) { }
        return new String(arr, 0, i);
    }
	
    @Override
    public List<SignatureOfEtlFlow> getMatchingFlows(String filename, List<SignatureOfEtlFlow> flows, String expectedFlowname, List<String> passwords) {
        List<SignatureOfEtlFlow> matchedList = new ArrayList<SignatureOfEtlFlow>();
        List<String> colnamesInFile = getFileSignature(filename);
        for(SignatureOfEtlFlow flow: flows){
            boolean isDebug = false;
            if(expectedFlowname != null && expectedFlowname.equals(flow.getEtlFlowName()))isDebug = true;
        	if(flow.getFileNamePattern() != null){
              	if(!checkFileNamePat(flow.getFileNamePattern(), filename, isDebug, expectedFlowname)) continue;
              	matchedList.add(flow);
              	continue;
            }
        	
            DbfSignature dbfSig = (DbfSignature) flow;
            List<String> colNamesInFlow = dbfSig.getColNames();
            if(doColNamesMatch(colnamesInFile, colNamesInFlow, isDebug, expectedFlowname)){
                matchedList.add(dbfSig);
            }
        }
        return matchedList;
    }
    private boolean doColNamesMatch(List<String> colNamesInFile, List<String> colNamesInFlow, boolean isDebug, String expectedFlowname){
        if(colNamesInFile.size() != colNamesInFlow.size()){
            if(expectedFlowname != null){
                logger.error("ETL_DEBUG: # of columns in expected flow {} is {} while the number of cols in the input file is {}", new String[]{expectedFlowname, colNamesInFlow.size()+"", colNamesInFile.size()+""});
            }
            return false;
        }
        for(int i = 0; i < colNamesInFile.size(); i++){
            if(!colNamesInFlow.get(i).equalsIgnoreCase(colNamesInFile.get(i))){
                if(isDebug){
                    logger.error("ETL_DEBUG: Col name mismatch in col {} - flow {} expected col name as {} but file has col name as {} ", new String[]{(i+1)+"", expectedFlowname, colNamesInFlow.get(i), colNamesInFile.get(i)});
                }
                return false;
            }
        }
        return true;
    }
}
