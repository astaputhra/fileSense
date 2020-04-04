package com.iMatch.etl.dataupload;

import com.iMatch.etl.models.DuplicateFileReceivedForUploadDO;
import org.joda.time.LocalDate;

public class DuplicateFileExistsException extends Exception {
	private String source;
    private LocalDate uploadDate;

    public DuplicateFileExistsException(String source, LocalDate uploadDate) {
    	this.source = source;
    	this.uploadDate = uploadDate;
    }
    
	public DuplicateFileReceivedForUploadDO getPayload() {
		DuplicateFileReceivedForUploadDO duplicateFileReceivedForUploadDO = new DuplicateFileReceivedForUploadDO();
		duplicateFileReceivedForUploadDO.setSource(source);
		duplicateFileReceivedForUploadDO.setUploadDate(uploadDate);
		return duplicateFileReceivedForUploadDO;
	}
}
