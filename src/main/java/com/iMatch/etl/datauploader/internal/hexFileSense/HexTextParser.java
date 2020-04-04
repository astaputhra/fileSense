package com.iMatch.etl.datauploader.internal.hexFileSense;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HexTextParser extends AbstractHexParser{
    private static final Logger logger = LoggerFactory.getLogger(HexTextParser.class);

    @Value("#{appProp[maxLineNum]}")
    private int maxLineNum;

    private boolean isMatchingETLFlow(String filename, SignatureOfEtlFlow sigEtlFlow, boolean isDebug, String expectedFlowname){
        TextSignature signature = (TextSignature)sigEtlFlow;
        if(signature.getContainsExpression() != null)return checkMatch(filename, signature.getLineNum(), signature.getContainsExpression(), false, isDebug, expectedFlowname);
        if(signature.getRegex() != null)return checkMatch(filename, signature.getLineNum(), signature.getRegex(), true, isDebug, expectedFlowname);
        return false;
    }

	private boolean checkMatch(String filename, int lineNum, String pattern, boolean isRegex, boolean isDebug, String expectedFlowname){
        if(isRegex && FilenameUtils.getExtension(filename).equalsIgnoreCase("xml")){
            pattern = StringEscapeUtils.unescapeXml(pattern);
        }
        boolean found = false;
		String line = null;
        File file = new File(filename);
        FileInputStream fs = null;
        BufferedReader br = null;
        try {
            fs = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(fs));
            if(lineNum == -1){
              int checkLine= 0;
                while((line = br.readLine()) != null &&  checkLine++ != maxLineNum){
                    if(!isRegex){
                        if( line.contains(pattern)) {
                            br.close();
                            fs.close();
                            found = true;
                            break;
                        }
                    }else {
                        if( line.matches(pattern)) {
                            br.close();
                            fs.close();
                            found = true;
                            break;
                        }
                    }
                }
            }else {
                int readLines = 0;
                do {
                    line = br.readLine();
                    readLines++;
                }while(readLines != lineNum);

                if(line == null) return false;

                if(!isRegex && line.contains(pattern)){
                    return true;
                }
                if(line.matches(pattern)) return true;
            }
        } catch (IOException e) {
            logger.error("Error reading download file {} - cannot continue. Error is {}", filename, e.getMessage());
        } finally {
            if(br != null) IOUtils.closeQuietly(br);
            if(fs != null) IOUtils.closeQuietly(fs);
        }
        return found;
	}
    @Override
    public List<SignatureOfEtlFlow> getMatchingFlows(String filename, List<SignatureOfEtlFlow> flows, String expectedFlowname, List<String> passwords) {
        List<SignatureOfEtlFlow> matchedList = new ArrayList<SignatureOfEtlFlow>();

        for(SignatureOfEtlFlow flow: flows) {
            boolean isDebug = false;
            if(expectedFlowname != null && expectedFlowname.equals(flow.getEtlFlowName()))isDebug = true;

            if(flow.getFileNamePattern() != null){
			  	if(!checkFileNamePat(flow.getFileNamePattern(), filename, isDebug, expectedFlowname)) continue;
			  	matchedList.add(flow);
			  	continue;
			}
            if(isMatchingETLFlow(filename, flow, isDebug, expectedFlowname))matchedList.add(flow);
        }
        return matchedList;
    }

}
