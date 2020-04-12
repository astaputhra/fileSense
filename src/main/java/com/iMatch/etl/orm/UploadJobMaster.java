package com.iMatch.etl.orm;

import com.iMatch.etl.EtlDefinition;
import com.iMatch.etl.enums.Channel;
import com.iMatch.etl.enums.UploadErrorType;
import com.iMatch.etl.internal.UploadStatus;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Configurable
@Entity
@Table(name = "UPLOAD_JOB_MASTER")
@NamedQueries(
        @NamedQuery(name = "UploadJobMaster.checkApprovalPending", query = " select count(1) from UploadJobMaster as ujm "
                + " where ujm.etlFlowName   like 'MARKET_PRICE%' "
                + " and   ujm.status        in (?3) ")
)
public class UploadJobMaster extends AbstractCommonsEntity {

    private static final long serialVersionUID = -6543360452725826933L;

    @Column(name = "UPLOAD_ID", length = 100)
	private String uploadId;

	@Column(name = "STATUS", length = 40)
	@NotNull
	private String status = "SENT_TO_P1_FOR_PROCESSING";

	@Column(name = "USER_ID", length = 9)
    @NotNull
	private String userId="VARA";//FIXME

    @Column(name = "UPLOAD_GENERIC_TYPE", length = 100)
	private String uploadGenericType;

	@Column(name = "NUMBER_OF_ERRORS")
	private BigDecimal numberOfErrors = BigDecimal.ZERO;

	@Column(name = "NUMBER_OF_LINES_INPUT")
	private BigDecimal numberOfLinesInput = BigDecimal.ZERO;

	@Column(name = "NUMBER_OF_REJECTED")
	private BigDecimal numberOfRejected = BigDecimal.ZERO;

	@Column(name = "NUMBER_OF_LINES_OUTPUT")
	private BigDecimal numberOfLinesOutput = BigDecimal.ZERO;

	@Column(name = "FILENAME", length = 2048)
	private String filename;

//	@Column(name = "ORIGINAL_FILENAME", length = 200)
//	private String originalFilename;

	@Column(name = "ETL_FLOW_NAME", length = 100)
	private String etlFlowName;

	@Column(name = "UPLOAD_TEMP_TABLE", length = 100)
	private String uploadTempTable;

//	@Column(name = "ADDITIONAL_TABLES", length = 200)
//	private String additionalTables;

//	@Column(name = "UPLOAD_DATE")
//	@Temporal(TemporalType.TIMESTAMP)
//	@DateTimeFormat(style = "M-")
//	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
//    @NotNull
//	private LocalDate uploadDate;

	@Column(name = "NAME", length = 60)
	private String name;

	@Column(name = "CHECKSUM", length = 40)
	private String checksum;

	@Column(name = "ARCHIVE_FILENAME", length = 2048)
	private String archiveFilename;

	@Column(name = "UPLOAD_SOURCE", length = 12)
	@Enumerated(value = EnumType.STRING)
    @NotNull
	private Channel channel;

	@Column(name = "EXT_EMAIL_ID", length = 50)
	private String extEmailId;

    @Column(name = "ZIP_FILENAME", length = 50)
    private String zipFilename;

    @Column(name = "DATA_EMAIL_ID_OUT", length = 50)
    private String dataEmailIdOut;

    @Column(name = "CUSTOM_PARAM", length = 40)
    private String customParam;

	@Column(name = "UPLOAD_ERROR_TYPE", length = 200)
	@Enumerated(value = EnumType.STRING)
	private UploadErrorType uploadErrorType;

    @Column(name = "DUMMY_COLUMN")
    private int dummyColumn = 0;

    public String getCustomParam() {
        return customParam;
    }

    public void setCustomParam(String source) {
        this.customParam = source;
    }

	public String getDataEmailIdOut() {
        return dataEmailIdOut;
    }

	public void setDataEmailIdOut(String dataEmailIdOut) {
        this.dataEmailIdOut = dataEmailIdOut;
    }

	public String getZipFilename() {
        return zipFilename;
    }

	public void setZipFilename(String zipFilename) {
        this.zipFilename = zipFilename;
    }

	public String getExtEmailId() {
		return extEmailId;
	}

	public void setExtEmailId(String extEmailId) {
		this.extEmailId = extEmailId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public String getStatus() {
		return status;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = "VARA";
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUploadGenericType() {
		return uploadGenericType;
	}

	public void setUploadGenericType(String uploadGenericType) {
		this.uploadGenericType = uploadGenericType;
	}

	public BigDecimal getNumberOfErrors() {
		return numberOfErrors;
	}

	public void setNumberOfErrors(BigDecimal numberOfErrors) {
		this.numberOfErrors = numberOfErrors;
	}

	public BigDecimal getNumberOfLinesInput() {
		return numberOfLinesInput;
	}


	public void setNumberOfLinesInput(BigDecimal numberOfLinesInput) {
		this.numberOfLinesInput = numberOfLinesInput;
	}


	public BigDecimal getNumberOfRejected() {
		return numberOfRejected;
	}


	public void setNumberOfRejected(BigDecimal numberOfRejected) {
		this.numberOfRejected = numberOfRejected;
	}


	public BigDecimal getNumberOfLinesOutput() {
		return numberOfLinesOutput;
	}


	public void setNumberOfLinesOutput(BigDecimal numberOfLinesOutput) {
		this.numberOfLinesOutput = numberOfLinesOutput;
	}


	public String getFilename() {
		return filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}


	public String getEtlFlowName() {
		return etlFlowName;
	}


	public void setEtlFlowName(String etlFlowName) {
		this.etlFlowName = etlFlowName;
	}


	public String getUploadTempTable() {
		return uploadTempTable;
	}


	public void setUploadTempTable(String uploadTempTable) {
		this.uploadTempTable = uploadTempTable;
	}


//	public LocalDate getUploadDate() {
//		return uploadDate;
//	}


//	public void setUploadDate(LocalDate uploadDate) {
//		this.uploadDate = uploadDate;
//	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getChecksum() {
		return checksum;
	}


	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}


	public String getArchiveFilename() {
		return archiveFilename;
	}


	public void setArchiveFilename(String archiveFilename) {
		this.archiveFilename = archiveFilename;
	}

//	public LocalDate getCompanyDate() {
//		return companyDate;
//	}
//
//	public void setCompanyDate(LocalDate companyDate) {
//		this.companyDate = companyDate;
//	}
//

	public UploadErrorType getUploadErrorType() {
		return uploadErrorType;
	}

	public void setUploadErrorType(UploadErrorType uploadErrorType) {
		this.uploadErrorType = uploadErrorType;
	}

//	public long getBusinessProcessDuration() {
//		return businessProcessDuration;
//	}
//
//	public void setBusinessProcessDuration(long businessProcessDuration) {
//		this.businessProcessDuration = businessProcessDuration;
//	}

    public int getDummyColumn() {
        return dummyColumn;
    }

    public void setDummyColumn(int dummyColumn) {
        this.dummyColumn = dummyColumn;
    }


	public UploadJobMaster populateUploadJobMaster(EtlDefinition etlDefn, String name, String company, String division, String checksum) {
		UploadJobMaster jobMasterEntry = new UploadJobMaster();
		jobMasterEntry.setName(name);
		jobMasterEntry.setFilename(name);
//		jobMasterEntry.setUploadDate((new LocalDate()));
		jobMasterEntry.setChannel(Channel.FILESENSE);
		jobMasterEntry.setExtEmailId(null);
		jobMasterEntry.setZipFilename(null);
		jobMasterEntry.setStatus(UploadStatus.FILE_RECEIVED.toString());
//		jobMasterEntry.setCompany(company);
//		jobMasterEntry.setDivision(division);
//		jobMasterEntry.setOriginalFilename(name);
		jobMasterEntry.setUserId(etlDefn.getUser());
		jobMasterEntry.setStatus(String.valueOf(etlDefn.getErrorType()));
		jobMasterEntry.setUploadGenericType(etlDefn.getGenericType());
		jobMasterEntry.setEtlFlowName(etlDefn.getEtlFlow());
		jobMasterEntry.setUploadErrorType(etlDefn.getErrorType());
		jobMasterEntry.setChecksum(checksum);
		return jobMasterEntry;
	}
}
