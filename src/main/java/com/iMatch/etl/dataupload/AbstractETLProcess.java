package com.iMatch.etl.dataupload;

import com.iMatch.etl.models.EtlCompletedDO;
import com.iMatch.etl.models.OverwriteFileResponseDO;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: akash
 * Date: 22/5/13
 * Time: 1:11 AM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public abstract class AbstractETLProcess {

    private final Logger logger = LoggerFactory.getLogger(AbstractETLProcess.class);

    private Map<String, Map<String,String>> errorList = new LinkedHashMap<String, Map<String,String>>();
    private int numberOfErrors = 0;

    public abstract ETLProcessCompleteDO process(EtlCompletedDO etlCompletedDO) throws DuplicateFileExistsException;

    public ETLProcessCompleteDO overwriteAcceptProcess(OverwriteFileResponseDO overwiteFileResponseDO) {
    	//Method STUB
    	ETLProcessCompleteDO etlProcessCompleteDO = new ETLProcessCompleteDO();
		etlProcessCompleteDO.setProcessStatus(ProcessStatus.PROCEED_WITH_CLEANUP);
		return etlProcessCompleteDO;
    }

    public ETLProcessCompleteDO overwriteRejectProcess(OverwriteFileResponseDO overwiteFileResponseDO) {
    	//Method STUB
    	ETLProcessCompleteDO etlProcessCompleteDO = new ETLProcessCompleteDO();
		etlProcessCompleteDO.setProcessStatus(ProcessStatus.PROCEED_WITH_CLEANUP);
		return etlProcessCompleteDO;
    }

    protected void addError(BigDecimal rowNumber, String columnName, String errorMsg) {
        String row = rowNumber.toString();
        addError(row, columnName, errorMsg);
    }

    protected void addError(int rowNumber, String columnName, String errorMsg) {
        String row = Integer.toString(rowNumber);
        addError(row, columnName, errorMsg);
    }

    public Map<String, Map<String,String>> getErrorList() {
        return errorList;
    }

    public BigDecimal getNumberOfErrors() {
//        return (new BigDecimal(errorList.size()));
        return (new BigDecimal(numberOfErrors));
    }

    private void addError(String row, String columnName, String errorMsg) {
        logger.trace("Added error, row : {}, column : {}, message : {}", (new String[]{row, columnName, errorMsg}));
        Map<String,String> error = new LinkedHashMap<String, String>();
        if(errorList.containsKey(row)) {
            error = errorList.get(row);
        }
        error.put(columnName, errorMsg);
        errorList.put(row, error);
        numberOfErrors++;
    }

    protected void sendDuplicateFileNotification(String source, LocalDate uploadDate) throws DuplicateFileExistsException {
    	throw (new DuplicateFileExistsException(source, uploadDate));
    }

}
