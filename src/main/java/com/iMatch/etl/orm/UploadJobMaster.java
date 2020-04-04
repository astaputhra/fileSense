package com.iMatch.etl.orm;

import com.iMatch.etl.EtlDefinition;
import com.iMatch.etl.enums.Channel;
import com.iMatch.etl.helperClasses.BigDecimalArrayToJsonMessageConverter;
import com.iMatch.etl.helperClasses.ReviewListToJsonMessageConverter;
import com.iMatch.etl.helperClasses.ReviewMapToJsonMessageConverter;
import com.iMatch.etl.helperClasses.ReviewSetToJsonMessageConverter;
import com.iMatch.etl.internal.ReviewDetails;
import com.iMatch.etl.enums.UploadErrorType;
import com.iMatch.etl.enums.NotificationResponseType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Configurable
@Entity
@Table(name = "UPLOAD_JOB_MASTER")
@NamedQueries(
        @NamedQuery(name = "UploadJobMaster.checkApprovalPending", query = " select count(1) from UploadJobMaster as ujm "
                + " where ujm.company       = ?1  "
                + " and   ujm.division      = ?2  "
                + " and   ujm.etlFlowName   like 'MARKET_PRICE%' "
                + " and   ujm.status        in (?3) ")
)
public class UploadJobMaster extends AbstractCommonsEntity {

    private static final long serialVersionUID = -6543360452725826933L;
    @Column(name = "COMPANY", length = 15)
    protected String company;

    @Column(name = "DIVISION", length = 6)
    protected String division;

    @Column(name = "UPLOAD_ID", length = 100)
	private String uploadId;

    @Column(name = "JOB_ID")
	private BigDecimal jobId;

    @Column(name = "P1_JOB_ID")
	private BigDecimal p1JobId;

	@Column(name = "STATUS", length = 40)
	@NotNull
	private String status = "SENT_TO_P1_FOR_PROCESSING";

	@Column(name = "USER_ID", length = 9)
    @NotNull
	private String userId;

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

	@Column(name = "ORIGINAL_FILENAME", length = 200)
	private String originalFilename;

	@Column(name = "ETL_FLOW_NAME", length = 100)
	private String etlFlowName;

	@Column(name = "UPLOAD_TEMP_TABLE", length = 100)
	private String uploadTempTable;

	@Column(name = "ADDITIONAL_TABLES", length = 200)
	private String additionalTables;

	@Column(name = "UPLOAD_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "M-")
	@Type(type = "java.util.Date")
    @NotNull
	private LocalDate uploadDate;

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

	@Column(name = "SPLIT", length = 1)
	private boolean split;

	@Column(name = "INITIAL_SPLIT_SELF_APPROVALS", length = 1)
	private boolean initialSplitSelfApprovalsAllDone;

	@Column(name = "COMPLETE", length = 1)
	private boolean complete = false;

	@Column(name = "ETL_PASSED", length = 1)
	private boolean etlPassed = true;

	@Column(name = "ROWS_UNDER_REVIEW")
	private int rowsUnderReview = 0;

	@Column(name = "SPLIT_COUNT")
	private int splitCount = 0;

	@Column(name = "ROWS_UNDER_SELF_REVIEW")
	private int rowsUnderSelfReview;

	@Column(name = "ROWS_COMPLETED")
	private int rowsCompleted = 0;

	@Column(name = "ETL_DURATION")
	private int eTLDuration = 0;

	@Column(name = "BUSINESS_PROCESS_DURATION")
	private long businessProcessDuration = 0;

//	@Column(name = "CURRENT_STATUS", length = 50)
//	private String currentStatus = "PROCESSING";

	@Column(name = "REJECTED_BY", length = 50)
	private String rejectedBy;

	@Column(name = "ESTIMATED_END_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	@Type(type = "java.util.Date")
	@DateTimeFormat(style = "M-")
	private DateTime estimatedEndTime;

	@Column(name = "END_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	@Type(type = "java.util.Date")
	@DateTimeFormat(style = "M-")
	private DateTime endTime;

	@Column(name = "START_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	@Type(type = "java.util.Date")
	@DateTimeFormat(style = "M-")
	private DateTime startTime;

	@Column(name = "COMPANY_DATE")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(style = "M-")
	@Type(type = "java.util.Date")
	private LocalDate companyDate;

	@Column(name = "ROWS_WITH_SYSTEM_ERRORS", length = 4000)
	@Convert(converter = BigDecimalArrayToJsonMessageConverter.class)
	private List<BigDecimal> rowsWithSystemErrors = new ArrayList<>();

	@Column(name = "ROWS_UNDER_REVIEW_INI_SPLIT", length = 4000)
	@Convert(converter = BigDecimalArrayToJsonMessageConverter.class)
	private List<BigDecimal> rowsUnderReviewBySelfOnInitialSplit = new ArrayList<>();

	@Column(name = "ROWS_REJECTED", length = 4000)
	@Convert(converter = BigDecimalArrayToJsonMessageConverter.class)
	private List<BigDecimal> rowsRejected  = new ArrayList<>();

	@Column(name = "ROWS_WITH_ERROR", length = 4000)
	@Convert(converter = BigDecimalArrayToJsonMessageConverter.class)
	private List<BigDecimal> rowsWithError = new ArrayList<>();

	@Column(name = "ROWS_WITH_HARD_BREACHES", length = 4000)
	@Convert(converter = BigDecimalArrayToJsonMessageConverter.class)
	private List<BigDecimal> rowsWithHardBreaches = new ArrayList<>();

	@Column(name = "UPLOAD_ETA_STATUSMAP", length = 4000)
	@Convert(converter = ReviewMapToJsonMessageConverter.class)
	private Map<BigDecimal,List<ReviewDetails>> uploadEtaStatusesMap = new HashMap<BigDecimal, List<ReviewDetails>>();

	@Column(name = "REVIEW_DETAILSLIST", length = 4000)
	@Convert(converter = ReviewListToJsonMessageConverter.class)
	private List<ReviewDetails> reviewDetailsList = new ArrayList<>();

	@Column(name = "REVIEWERS", length = 4000)
	@Convert(converter = ReviewSetToJsonMessageConverter.class)
	private Set<String> reviewers = new HashSet<>();

	transient
	ReentrantLock lock = new ReentrantLock();

	@Column(name = "UPLOAD_ERROR_TYPE", length = 200)
	@Enumerated(value = EnumType.STRING)
	private UploadErrorType uploadErrorType;

    @Column(name = "DUMMY_COLUMN")
    private int dummyColumn = 0;

    public BigDecimal getP1JobId() {
        return p1JobId;
    }

    public void setP1JobId(BigDecimal p1JobId) {
        this.p1JobId = p1JobId;
    }

    public BigDecimal getJobId() {
        return jobId;
    }

    public void setJobId(BigDecimal jobId) {
        this.jobId = jobId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

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
		this.userId = userId;
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


	public LocalDate getUploadDate() {
		return uploadDate;
	}


	public void setUploadDate(LocalDate uploadDate) {
		this.uploadDate = uploadDate;
	}


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

	public boolean wasUploadedFileSplit(){
		return this.p1JobId == null;
	}

	public boolean isSplit() {
		return split;
	}

	public void setSplit(boolean split) {
		this.split = split;
	}

	public int getRowsUnderReview() {
		return rowsUnderReview;
	}

	public void setRowsUnderReview(int rowsUnderReview) {
		this.rowsUnderReview = rowsUnderReview;
	}

	public List<BigDecimal> getRowsWithSystemErrors() {
		return rowsWithSystemErrors;
	}

	public void setRowsWithSystemErrors(List<BigDecimal> rowsWithSystemErrors) {
		this.rowsWithSystemErrors = rowsWithSystemErrors;
	}

	public Set<String> getReviewers() {
		return reviewers;
	}

	public void setReviewers(Set<String> reviewers) {
		this.reviewers = reviewers;
	}

	public boolean isInitialSplitSelfApprovalsAllDone() {
		return initialSplitSelfApprovalsAllDone;
	}

	public void setInitialSplitSelfApprovalsAllDone(boolean initialSplitSelfApprovalsAllDone) {
		this.initialSplitSelfApprovalsAllDone = initialSplitSelfApprovalsAllDone;
	}

	public DateTime getEstimatedEndTime() {
		return estimatedEndTime;
	}

	public void setEstimatedEndTime(DateTime estimatedEndTime) {
		this.estimatedEndTime = estimatedEndTime;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public List<BigDecimal> getRowsUnderReviewBySelfOnInitialSplit() {
		return rowsUnderReviewBySelfOnInitialSplit;
	}

	public void setRowsUnderReviewBySelfOnInitialSplit(List<BigDecimal> rowsUnderReviewBySelfOnInitialSplit) {
		this.rowsUnderReviewBySelfOnInitialSplit = rowsUnderReviewBySelfOnInitialSplit;
	}

	public String getRejectedBy() {
		return rejectedBy;
	}

	public void setRejectedBy(String rejectedBy) {
		this.rejectedBy = rejectedBy;
	}

	public List<BigDecimal> getRowsRejected() {
		return rowsRejected;
	}

	public void setRowsRejected(List<BigDecimal> rowsRejected) {
		this.rowsRejected = rowsRejected;
	}

	public List<BigDecimal> getRowsWithError() {
		return rowsWithError;
	}

	public void setRowsWithError(List<BigDecimal> rowsWithError) {
		this.rowsWithError = rowsWithError;
	}

	public List<BigDecimal> getRowsWithHardBreaches() {
		return rowsWithHardBreaches;
	}

	public void setRowsWithHardBreaches(List<BigDecimal> rowsWithHardBreaches) {
		this.rowsWithHardBreaches = rowsWithHardBreaches;
	}

	public int getRowsUnderSelfReview() {
		return rowsUnderSelfReview;
	}

	public void setRowsUnderSelfReview(int rowsUnderSelfReview) {
		this.rowsUnderSelfReview = rowsUnderSelfReview;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDate getCompanyDate() {
		return companyDate;
	}

	public void setCompanyDate(LocalDate companyDate) {
		this.companyDate = companyDate;
	}

	public Map<BigDecimal, List<ReviewDetails>> getUploadEtaStatusesMap() {
		return uploadEtaStatusesMap;
	}

	public void setUploadEtaStatusesMap(Map<BigDecimal, List<ReviewDetails>> uploadEtaStatusesMap) {
		this.uploadEtaStatusesMap = uploadEtaStatusesMap;
	}

	public boolean isEtlPassed() {
		return etlPassed;
	}

	public void setEtlPassed(boolean etlPassed) {
		this.etlPassed = etlPassed;
	}

	public List<ReviewDetails> getReviewDetailsList() {
		return reviewDetailsList;
	}

	public void setReviewDetailsList(List<ReviewDetails> reviewDetailsList) {
		this.reviewDetailsList = reviewDetailsList;
	}

	public UploadErrorType getUploadErrorType() {
		return uploadErrorType;
	}

	public void setUploadErrorType(UploadErrorType uploadErrorType) {
		this.uploadErrorType = uploadErrorType;
	}

	public int getRowsCompleted() {
		return rowsCompleted;
	}

	public void setRowsCompleted(int rowsCompleted) {
		this.rowsCompleted = rowsCompleted;
	}

	public int geteTLDuration() {
		return eTLDuration;
	}

	public void seteTLDuration(int eTLDuration) {
		this.eTLDuration = eTLDuration;
	}

	public long getBusinessProcessDuration() {
		return businessProcessDuration;
	}

	public void setBusinessProcessDuration(long businessProcessDuration) {
		this.businessProcessDuration = businessProcessDuration;
	}

    public int getDummyColumn() {
        return dummyColumn;
    }

    public void setDummyColumn(int dummyColumn) {
        this.dummyColumn = dummyColumn;
    }

    public void lock(){
		this.lock.lock();
	}
	public void unlock(){
		this.lock.unlock();
	}
	public void incrementRowsCompleted(){
		synchronized (this){
			rowsCompleted++;
		}
	}

	public int getSplitCount() {
		return splitCount;
	}

	public void setSplitCount(int splitCount) {
		this.splitCount = splitCount;
	}

	public void handleCompletion(boolean transactionSuccess){
		try {
			this.lock();
			Set<String> list = new HashSet<>(1);
			list.add(userId);
			if (this.isSplit() && !isInitialSplitSelfApprovalsAllDone()) {
              /*  *//**//* Consider the scenario : Split transactions are uploaded and now one of the split transactions is complete. However one or more of the transaction is
                   still pending approval by the uploader - in this case the status is still under review
                 *//**//**/
				this.reviewers = list;
				return;
			}
			if(!isSplit() && !getRowsUnderReviewBySelfOnInitialSplit().isEmpty()) return;
			this.complete = true;
//			this.currentStatus = transactionSuccess ? "Complete" : "Error";
			this.status = transactionSuccess ? "BUSINESS_PROCESS_COMPLETED" : "BUSINESS_PROCESS_ABORTED";
			this.estimatedEndTime = null;
			this.endTime = new DateTime();
			this.reviewers = new HashSet<>();
		}finally {
			this.unlock();
		}
	}

	public void handleReject(BigDecimal updRecordNumber, String userId) {
		if(isSplit()) {
			getRowsRejected().add(updRecordNumber);
			decrementRowsUnderReview();
		}
		setRejectedBy(userId);
	}

	public void decrementRowsUnderReview(){
		lock();
		rowsUnderReview--;
		unlock();
	}

	public void handleSystemError(BigDecimal updRecordNumber) {
		if(!isSplit()){
			this.setEndTime(new DateTime());
			this.estimatedEndTime = null;
            this.setReviewers(new HashSet<String>());
            this.setStatus("SYSTEM_ERROR_IN_P1");
		}
		this.getRowsWithSystemErrors().add(updRecordNumber);
	}

	public void handleAllRowsSplitAndSentDownP0() {
		if(this.getSplitCount() == this.getRowsWithError().size() + this.getRowsRejected().size() + this.getRowsWithHardBreaches().size() + this.getRowsWithSystemErrors().size()){
			this.setEstimatedEndTime(null);
			this.setEndTime(new DateTime());
			this.setComplete(true);
			this.setReviewers(new HashSet<String>());
		};
	}

	public void handleHardbreach(BigDecimal updRecordNumber) {
		if(!isSplit()){
			this.setEndTime(new DateTime());
		}
		this.setEstimatedEndTime(null);
		this.status = "COMPLIANCE_HARD_BREACH";
		this.getRowsWithHardBreaches().add(updRecordNumber);
	}

	public void handleReviewBySelf(String userId, BigDecimal updRecordNumber) {
		Set<String> list = new HashSet<>(1);
		list.add(userId);
		this.setReviewers(list);
		this.setEstimatedEndTime(null);
//		this.setCurrentStatus("Pending Review");
		this.status = "PENDING_REVIEW";
		this.setEndTime(null);
		this.getRowsUnderReviewBySelfOnInitialSplit().add(updRecordNumber);
		this.rowsUnderSelfReview++;
	}

	public void handleSplitSelfReject() {
		this.getRowsRejected().addAll(rowsUnderReviewBySelfOnInitialSplit);
		rowsUnderReviewBySelfOnInitialSplit.clear();
	}

	public void handleReviewComplete(BigDecimal recordNumber,NotificationResponseType notificationResponseType, String reviewer, String recRemarks) {
		ReviewDetails reviewDetails = new ReviewDetails(notificationResponseType, reviewer, recRemarks);
		List<ReviewDetails> details = uploadEtaStatusesMap.get(recordNumber);
		if(details == null){
			details = new ArrayList<>();
			uploadEtaStatusesMap.put(recordNumber, details);
		}
		details.add(reviewDetails);
	}
	public void handleReviewCompleteNonSplit(NotificationResponseType notificationResponseType, String reviewer, String recRemarks) {
		ReviewDetails reviewDetails = new ReviewDetails(notificationResponseType, reviewer, recRemarks);
		reviewDetailsList.add(reviewDetails);
		if(!isSplit() && NotificationResponseType.REJECT.equals(notificationResponseType)){
			this.status =  "USER_REJECTED_AT_REVIEW";
		}
	}

	public void handleError(BigDecimal updRecordNumber) {
		if(!isSplit()){
			this.setEndTime(new DateTime());
			this.estimatedEndTime = null;
		}
		this.getRowsWithError().add(updRecordNumber);
	}

	public void handleEtlFailure(UploadErrorType uploadErrorType){
		this.complete = true;
//		this.currentStatus = "ETL_ERROR";
//		this.status = "ETL_ERROR";
		this.endTime = new DateTime();
		etlPassed = false;
		this.estimatedEndTime = null;
		this.uploadErrorType = uploadErrorType;
	}


	public String getAdditionalTables() {
		return additionalTables;
	}


	public void setAdditionalTables(String additionalTables) {
		this.additionalTables = additionalTables;
	}


	public UploadJobMaster populateUploadJobMaster(EtlDefinition etlDefn, String name, String company, String division, String checksum) {
		UploadJobMaster jobMasterEntry = new UploadJobMaster();
		jobMasterEntry.setName(name);
		jobMasterEntry.setFilename(name);
		jobMasterEntry.setUploadDate((new LocalDate()));
		jobMasterEntry.setChannel(Channel.FILESENSE);
		jobMasterEntry.setExtEmailId(null);
		jobMasterEntry.setZipFilename(null);
		jobMasterEntry.setCompany(company);
		jobMasterEntry.setDivision(division);
		jobMasterEntry.setOriginalFilename(name);
		jobMasterEntry.setUserId(etlDefn.getUser());
		jobMasterEntry.setStatus(String.valueOf(etlDefn.getErrorType()));
		jobMasterEntry.setUploadGenericType(etlDefn.getGenericType());
		jobMasterEntry.setEtlFlowName(etlDefn.getEtlFlow());
		jobMasterEntry.setUploadErrorType(etlDefn.getErrorType());
		jobMasterEntry.setChecksum(checksum);
		return jobMasterEntry;
	}
}
