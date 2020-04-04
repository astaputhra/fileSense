package com.iMatch.etl.datauploader.internal.hexFileSense;

import com.iMatch.etl.datauploader.ETLServiceProvider;
import com.iMatch.etl.datauploader.internal.SupportedUploadFileTypes;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class HexFileSense {

    @Autowired
    private HexExcelParser excelParser;
    @Autowired
    private HexDBFParser dbfParser;
    @Autowired
    private HexTextParser textParser;
    @Autowired
    private ETLServiceProvider etlService;

    private List<byte[]> excel2003magicList = new ArrayList<byte[]>();

    private List<byte[]> excel2007magicList = new ArrayList<byte[]>();

    private static Logger _logger = LoggerFactory.getLogger(HexFileSense.class);

    public HexFileSense(){
        byte[] xlsSigType1 = new byte[]{0x09, 0x08, 0x10, 0x00, 0x00, 0x06, 0x05, 0x00};
        byte[] xlsSigType2 = new byte[]{(byte)0xFD, (byte)0xFF, (byte)0xFF, (byte)0xFF};

        byte[] xlsxSig1 = new byte[]{0x50, 0x4B, 0x03, 0x04, 0x14, 0x00, 0x06, 0x00};
        byte[] xlsxSig2 = new byte[]{0x50, 0x4B, 0x03, 0x04, 0x14, 0x00, 0x08, 0x08};
//        short[] xlsSigType2 = new short[]{0xFD, 0xFF, 0xFF, 0xFF, 0x20, 0x00, 0x00, 0x00};
//        short[] xlsSigType3 = new short[]{0xFD, 0xFF, 0xFF, 0xFF, 0x10, 0x02};
//        short[] xlsSigType4 = new short[]{0xFD, 0xFF, 0xFF, 0xFF, 0x1F, 0x02};
//        short[] xlsSigType5 = new short[]{0xFD, 0xFF, 0xFF, 0xFF, 0x22, 0x02};
//        short[] xlsSigType6 = new short[]{0xFD, 0xFF, 0xFF, 0xFF, 0x23, 0x02};
//        short[] xlsSigType7 = new short[]{0xFD, 0xFF, 0xFF, 0xFF, 0x28, 0x02};
//        short[] xlsSigType8 = new short[]{0xFD, 0xFF, 0xFF, 0xFF, 0x29, 0x02};
        excel2003magicList.add(xlsSigType1);
        excel2003magicList.add(xlsSigType2);
        excel2007magicList.add(xlsxSig1);
        excel2007magicList.add(xlsxSig2);
    }

    public List<SignatureOfEtlFlow> senseFileType(String filename, String expectedFlowname, List<String> passwords) {
        IHexParser hexParser = null;
        SupportedUploadFileTypes fileType = getMimeType(filename);
        switch (fileType) {
            case XBaseInput:
                hexParser = dbfParser;
                break;
            case EXCEL:
//            case ExcelInput_2007Plus:
//            case ExcelInput_PRE_2007:
                hexParser = excelParser;
                break;
            case TextInput:
                hexParser = textParser;
                break;
        }
        /* For the file type get all the flows in the ETL sub-system that accepts this kind of a file */
        List<SignatureOfEtlFlow> etlSignatures = etlService.getEtlFlowsSignaturesForFileType(fileType);
        if(expectedFlowname != null){
            SignatureOfEtlFlow signatureForFlow = etlService.getSignatureForFlow(expectedFlowname);
            if(signatureForFlow == null){
                _logger.error("ETL_DEBUG:Expected flow '{}' does not exist", expectedFlowname);
            } else {
                if(signatureForFlow.getClass().equals(DbfSignature.class)){
                    if(fileType != SupportedUploadFileTypes.XBaseInput) _logger.error("ETL_DEBUG:Flow '{}' expects a file of type DBF but the file is of type '{}' ", expectedFlowname, fileType);
                }
                else if(signatureForFlow.getClass().equals(ExcelSignature.class)){
                    if(fileType != SupportedUploadFileTypes.EXCEL) _logger.error("ETL_DEBUG:Flow '{}' expects a file of type EXCEL but the file is of type '{}' ", expectedFlowname, fileType);
                }
                else if(signatureForFlow.getClass().equals(TextSignature.class)){
                    if(fileType != SupportedUploadFileTypes.TextInput) _logger.error("ETL_DEBUG:Flow '{}' expects a file of type TEXT but the file is of type '{}' ", expectedFlowname, fileType);
                }
            }
        }

        List<SignatureOfEtlFlow> matchingEtlFlows = hexParser.getMatchingFlows(filename, etlSignatures, expectedFlowname, passwords);

        if (matchingEtlFlows.size() == 0) {
            _logger.error("Unable to find a transformation for file '{}', which is determined to be of type '{}", filename, fileType);
        } else {
            if(_logger.isDebugEnabled()){
                _logger.debug("Got {} match(es) with hexFileSense for file {} :", matchingEtlFlows.size(), filename);
                for (SignatureOfEtlFlow matchingEtlFlow : matchingEtlFlows) {
                    _logger.debug("Type is {} :: Flow is {} ", matchingEtlFlow.getEtlType(), matchingEtlFlow.getEtlFlowName());
                }
            }
        }
        return matchingEtlFlows;
    }

    private SupportedUploadFileTypes getMimeType(String filename)
    {
        if(isExcel2007(filename))return SupportedUploadFileTypes.EXCEL;
        if(isExcel93_2003(filename))return SupportedUploadFileTypes.EXCEL;
        if(isSortOfExcel(filename))return SupportedUploadFileTypes.EXCEL;
        if(isDBF(filename))return SupportedUploadFileTypes.XBaseInput;
        return SupportedUploadFileTypes.TextInput;
    }

    private boolean isDBF(String filename)
    {
        String extension = FilenameUtils.getExtension(filename);
        if (extension.equalsIgnoreCase("dbf")) return true;
        return false;
    }
    private boolean isExcel2007(String filename)
    {
    	for(byte[] magic: excel2007magicList)
        {
            boolean matched = matchMagic(filename, magic, 0);
            if(matched) return true;
        }
        return false;
    }
    private boolean isExcel93_2003(String filename)
    {

        for(byte[] magic: excel2003magicList)
        {
            boolean matched = matchMagic(filename, magic, 512);
            if(matched) return true;
        }
        return false;

    }
    private boolean matchMagic(String filename, byte[] magic, int offset)
    {
        File f = new File(filename);
        FileInputStream inputStream = null;
        try {

            inputStream = new FileInputStream(f);
            byte[] col = new byte[offset + magic.length];
            inputStream.read(col);
            int i = 0;
            for(i = 0; i < magic.length; i++){
                if(!(magic[i] == col[i+offset]))break;
            }
            if(i == magic.length) return true;
        }catch (Exception e){
            return false;
        } finally {
            if(inputStream != null) IOUtils.closeQuietly(inputStream);
        }
        return false;
    }

    /**
     * This a big big workaround - if the filename ends with xls or xlsx we return type EXCEL - but before we
     * do that we do a sanity check on the 1st four bytes of the magic
     * @param filename
     * @return true is EXCEL, else false
     */
    private boolean isSortOfExcel(String filename) {
        byte[] xl = new byte[]{0x50, 0x4B, 0x03, 0x04};
        byte[] cdf = new byte[]{(byte) 0xD0, (byte)0xCF, 0x11, (byte) 0xE0,(byte) 0xA1, (byte)0xB1, (byte) 0x1A, (byte) 0xE1};
        if(!matchMagic(filename, xl, 0) && !matchMagic(filename, cdf, 0))return false;
        String extension = FilenameUtils.getExtension(filename);
        if("xls".equalsIgnoreCase(extension)) return true;
        if("xlsx".equalsIgnoreCase(extension)) return true;
        return false;
    }
    public boolean isSheetNonEmpty(Sheet sheet){
        if(sheet.getLastRowNum() == 0 && sheet.getPhysicalNumberOfRows() == 0) return false;
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(i);
                if(cell.getCellType() != Cell.CELL_TYPE_BLANK){
                    /* Non blank cell - sheet has data */
                    return true;
                }
            }
        }
        return false;
    }
    public boolean validateMaxSheets(String filename, int maxDataSheets){
        SupportedUploadFileTypes mimeType = getMimeType(filename);
        if(!SupportedUploadFileTypes.EXCEL.equals(mimeType)) return true;
        try (Workbook wb = WorkbookFactory.create(new File(filename))) {
            int sheetsWithData = 0;
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                if(isSheetNonEmpty(sheet)){
                    /* This sheet has data */
                    sheetsWithData++;
                    if(sheetsWithData > maxDataSheets){
                        /* We have exceeded the max number of sheets with data for this file */
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
    }
}