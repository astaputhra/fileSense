package com.iMatch.etl.orm;

import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Map;

@Configurable
@Entity
@Table(name = "UPLOAD_ERRORS")
public class UploadErrors extends AbstractCommonsEntity  {

    private static final long serialVersionUID = 4208510215882718007L;
    @Column(name = "UPD_IDENTIFIER", length = 100)
	private String uploadId;

	@Column(name = "UPD_RECORD_NUMBER", precision = 9, scale = 0)
	private BigDecimal updRecordNumber = BigDecimal.ZERO;

	@Column(name = "UPD_ERROR_MESSAGE", length = 1000)
	private String updErrorMessage;

	@Column(name = "UPD_COLUMN_NAME", length = 60)
	private String updColumnName;

	public UploadErrors(String rowNo, Map<String, String> stringMapMap, String uploadId) {
		setUpdColumnName(stringMapMap.keySet().toArray()[0].toString());
		setUpdErrorMessage(stringMapMap.values().toArray()[0].toString());
		setUploadId(uploadId);
		setUpdRecordNumber(BigDecimal.valueOf(Long.parseLong(rowNo)));
	}

	public UploadErrors() {

	}

	
	public String getUploadId() {
		return uploadId;
	}


	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}


	public BigDecimal getUpdRecordNumber() {
		return updRecordNumber;
	}


	public void setUpdRecordNumber(BigDecimal updRecordNumber) {
		this.updRecordNumber = updRecordNumber;
	}


	public String getUpdErrorMessage() {
		return updErrorMessage;
	}


	public void setUpdErrorMessage(String updErrorMessage) {
		this.updErrorMessage = updErrorMessage;
	}


	public String getUpdColumnName() {
		return updColumnName;
	}


	public void setUpdColumnName(String updColumnName) {
		this.updColumnName = updColumnName;
	}

}
