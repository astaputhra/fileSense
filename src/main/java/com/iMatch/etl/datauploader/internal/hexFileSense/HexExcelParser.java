package com.iMatch.etl.datauploader.internal.hexFileSense;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class HexExcelParser extends AbstractHexParser{
    private static final Logger logger = LoggerFactory.getLogger(HexExcelParser.class);

    private static final String EXCEL_2003 = "2003format";
    private static final String EXCEL_2007 = "2007format";

    private boolean isMatchingETLFlow(String filename, SignatureOfEtlFlow sigEtlFlow, boolean isDebug, String expectedFlowname, Map<String, Workbook> cache) {
        ExcelSignature signature = (ExcelSignature) sigEtlFlow;
        List<String> cellValues = null;
        List<ExcelSignature.SearchPatterns> patterns = signature.getPatterns();

        if(patterns == null)return false;
        try{
            cellValues = getCellValueAtExcel93_2003(filename, patterns, signature.getSheetname(), cache);
        }catch (Exception e){
            logger.trace("File {} - unable to read as Excel 93-2003 file. Exception - ",filename, e.getMessage());
        }
        try{
            if(cellValues == null || cellValues.isEmpty() || cellValues.size() != patterns.size())   {
                cellValues = getCellValueAtExcel2007(filename, patterns, signature.getSheetname(), cache);
            }
        }catch (Exception e){
            logger.trace("File {} - unable to read as Excel 2007+ file. Exception - ", filename, e.getMessage());
        }

        if(cellValues == null || cellValues.isEmpty() || cellValues.size() != patterns.size()){
            if(isDebug){
                if(cellValues == null || cellValues.isEmpty() ){
                    logger.error("ETL_DEBUG: Unable to read cell values at specified locations");
                }else{
                    logger.error("ETL_DEBUG: # of columns in expected flow {} is {} while the number of cols in the input file is {}", new String[]{expectedFlowname, patterns.size()+"", cellValues.size()+""});
                }
            }
            return false;
        }

        if(signature.isPatternRegex()){
            for(int i =0; i < cellValues.size(); i++){
                if(!cellValues.get(i).matches(patterns.get(i).getRegex())){
                    if(isDebug){
                        logger.error("ETL_DEBUG: Col name mismatch in col {} - flow {} expected col name REGEX as {} but file has col name as {} ", new String[]{(i+1)+"", expectedFlowname, patterns.get(i).getRegex(), cellValues.get(i)});
                    }
                    return false;
                }
            }
        } else {
            for(int i =0; i < cellValues.size(); i++){
                if(!cellValues.get(i).replace("\u00A0","").trim().equalsIgnoreCase(patterns.get(i).getRegex().trim())){
                    if(isDebug){
                        logger.error("ETL_DEBUG: Col name mismatch in col {} - flow {} expected col name as {} but file has col name as {} ", new String[]{(i+1)+"", expectedFlowname, patterns.get(i).getRegex().trim(), cellValues.get(i).trim()});
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private List<String> getCellValueAtExcel2007(String filename, List<ExcelSignature.SearchPatterns> patterns, String sheetName, Map<String, Workbook> cache){
        XSSFWorkbook wb;
        List<String> values = new ArrayList<String>();

        try {
            if (cache.containsKey(EXCEL_2007)) {
                wb = (XSSFWorkbook) cache.get(EXCEL_2007);
            } else {
                wb = new XSSFWorkbook(new File(filename));
                cache.put(EXCEL_2007, wb);
            }
        }catch (IOException e){
            return values;
        }catch(Exception e){
            return values;
        }
        for(ExcelSignature.SearchPatterns pattern: patterns){
            try {
                XSSFCell cell;
                if(sheetName == null){
                    cell = wb.getSheetAt(0).getRow(pattern.getRow()).getCell(pattern.getCol());
                }else {
                    cell = wb.getSheet(sheetName).getRow(pattern.getRow()).getCell(pattern.getCol());
                }
                switch(cell.getCellType()){
                    case XSSFCell.CELL_TYPE_NUMERIC:
                        values.add("HEX_NUMERIC");
                        break;
                    default:
                        values.add(cell.getStringCellValue());
                        break;
                }
            } catch (Exception e) {
                logger.trace("Excel 2007 : Error in reading cell in file {} - row {}, col {}. Error is {}", new String[]{filename, pattern.getRow() + "", pattern.getCol() + "", e.getMessage()});
                values.add("HEX_NULL");
//                return values;
            }
        }
        return values;
    }
    private List<String> getCellValueAtExcel93_2003(String filename, List<ExcelSignature.SearchPatterns> patterns, String sheetName, Map<String, Workbook> cache){
        FileInputStream inputStream = null;
        HSSFWorkbook wb;
        List<String> values = new ArrayList<String>();

        try {
            if (cache.containsKey(EXCEL_2003)) {
                wb = (HSSFWorkbook) cache.get(EXCEL_2003);
            } else {
                inputStream = new FileInputStream(filename);
                wb = new HSSFWorkbook(inputStream);
                cache.put(EXCEL_2003, wb);
            }
        }catch (IOException e){
            IOUtils.closeQuietly(inputStream);
            inputStream = null;
            return values;
        } catch (Exception e){
            return values;
        } finally {
            if(inputStream != null) IOUtils.closeQuietly(inputStream);
        }
        for(ExcelSignature.SearchPatterns pattern: patterns){
            try {
                HSSFCell cell ;
                if(sheetName == null){
                    cell = wb.getSheetAt(0).getRow(pattern.getRow()).getCell(pattern.getCol());
                }else {
                    cell = wb.getSheet(sheetName).getRow(pattern.getRow()).getCell(pattern.getCol());
                }
                switch(cell.getCellType()){
                    case HSSFCell.CELL_TYPE_NUMERIC:
                        values.add("HEX_NUMERIC");
                        break;
                    default:
                        values.add(cell.getStringCellValue());
                        break;
                }
            } catch (Exception e) {
                logger.trace("Excel 97-2003 : Error in reading cell in file {} - row {}, col {}. Error is {}", new String[]{filename, pattern.getRow() + "", pattern.getCol() + "", e.getMessage()});
                values.add("HEX_NULL");
//                return values;
            }
        }
        return values;
    }

    @Override
    public List<SignatureOfEtlFlow> getMatchingFlows(String filename, List<SignatureOfEtlFlow> flows, String expectedFlowname, List<String> passwords) {
        unprotect(filename, passwords);
        List<SignatureOfEtlFlow> matchedList = new ArrayList<SignatureOfEtlFlow>();
        Map<String, Workbook> cache = new HashMap<String, Workbook>();
        for(SignatureOfEtlFlow flow: flows) {
            logger.trace("Attempting to match file {} with flow {}", filename, flow.getEtlFlowName());
            boolean isDebug = false;
            if(expectedFlowname != null && expectedFlowname.equals(flow.getEtlFlowName()))isDebug = true;
            if(flow.getFileNamePattern() != null){
                if(!checkFileNamePat(flow.getFileNamePattern(), filename, isDebug, expectedFlowname)) continue;
                matchedList.add(flow);
            }else {
                if(isMatchingETLFlow(filename, flow, isDebug, expectedFlowname, cache))
                    matchedList.add(flow);
            }
        }
//        cleanCache(cache);
        for(SignatureOfEtlFlow signatureOfEtlFlow : matchedList){
            if(!(StringUtils.isEmpty(signatureOfEtlFlow.getFileNamePattern())) && filename.contains(signatureOfEtlFlow.getFileNamePattern())){
                return Arrays.asList(signatureOfEtlFlow);
            }
        }
        return matchedList;
    }
    private void cleanCache(Map<String, Workbook> cache){
        HSSFWorkbook wb = (HSSFWorkbook) cache.get(EXCEL_2003);
        if(wb != null){
            try {
                wb.close();
            }catch (Exception e){
                logger.debug("Unable to close HSSF workbook - {}", e.getMessage());
            }
        }
        XSSFWorkbook xssfwb = (XSSFWorkbook) cache.get(EXCEL_2007);
        if(xssfwb != null){
            try {
                xssfwb.close();
            }catch (Exception e){
                logger.debug("Unable to close XSSF workbook - {}", e.getMessage());
            }
        }

    }
    private void unprotectAndCopy(String filename, String password){
        FileOutputStream fi = null;
        File temp = null;
        Workbook wb = null;
        try {
            temp = File.createTempFile("temp-file-name", "." + FilenameUtils.getExtension(filename));
            fi = new FileOutputStream(temp);
            wb = HexWorkbookFactory.create(new File(filename), password, true); //throws exception if it can't open
            wb.write(fi);
            wb.close();
            fi.close();
            FileUtils.copyFile(temp, new File(filename));
        } catch (Exception e){
            if(e instanceof RuntimeException) throw (RuntimeException)e; throw new RuntimeException(e);
        }  finally {
            IOUtils.closeQuietly(fi);
            IOUtils.closeQuietly(wb);
            try {
                if(temp != null) temp.delete();
            } catch (Exception e) {
                //
            }
        }
    }

    private void unprotect(String filename, List<String> passwords){
        Biff8EncryptionKey.setCurrentUserPassword(null);
        try {
            Workbook wb = HexWorkbookFactory.create(new File(filename), null, true);
            wb.close();
            return;
        }catch (EncryptedDocumentException e){
            /* do nothing - the loop below will handle it */
        } catch (Exception e){
            if(e instanceof RuntimeException) throw (RuntimeException)e; throw new RuntimeException(e);
        }

        for (String password : passwords) {
            try{
                Biff8EncryptionKey.setCurrentUserPassword(null);
                unprotectAndCopy(filename, password);
                return;
            }catch (Exception e){
                //Log debug that password did not match
            }
        }
        return;
    }


    private boolean isNonOOXMLPasswordProtected(String filename) {
        try {
            HSSFWorkbook sheets = new HSSFWorkbook(new FileInputStream(filename));
            sheets.close();
            return false;
        }catch (EncryptedDocumentException e){
            return true;
        } catch (Exception e){
            if(e instanceof RuntimeException) throw (RuntimeException)e; throw new RuntimeException(e);
		}
    }

    private boolean isOOXMLPasswordProtected(String filename){
        try {
            XSSFWorkbook sheets = new XSSFWorkbook(new File(filename));
            sheets.close();
            return false;
        }catch (EncryptedDocumentException e){
            return true;
        } catch (Exception e){
            if(e instanceof RuntimeException)
                throw (RuntimeException)e; throw new RuntimeException(e);
        }
    }

}
