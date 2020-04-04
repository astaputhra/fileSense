package com.iMatch.etl.models;

import com.iMatch.etl.enums.NotificationResponseType;
import com.iMatch.etl.enums.ReviewNotificationStatus;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public class OverwriteFileResponseDO implements IFileUploaderDO {

    private static final long serialVersionUID = 1L;

    private String uploadId;
    private String source;
    private LocalDate uploadDate;
    private NotificationResponseType notificationResponseType;
    private ReviewNotificationStatus reviewNotificationStatus;
	private BigDecimal sysTaskId = null;
    private  String updFileName;
	private String moduleRef;
	private String taskCreatedBy;

	public BigDecimal getSysTaskId() {
		return sysTaskId;
	}

	public void setSysTaskId(BigDecimal sysTaskId) {
		this.sysTaskId = sysTaskId;
	}

	public String getUploadId() {
		return uploadId;
	}
	
	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public LocalDate getUploadDate() {
		return uploadDate;
	}
	
	public void setUploadDate(LocalDate uploadDate) {
		this.uploadDate = uploadDate;
	}

	public NotificationResponseType getNotificationResponseType() {
		return notificationResponseType;
	}

	public void setNotificationResponseType(NotificationResponseType notificationResponseType) {
		this.notificationResponseType = notificationResponseType;
	}

    public String getUpdFileName() {
        return updFileName;
    }

    public void setUpdFileName(String updFileName) {
        this.updFileName = updFileName;
    }

	public ReviewNotificationStatus getReviewNotificationStatus() {
		return reviewNotificationStatus;
	}

	public void setReviewNotificationStatus(ReviewNotificationStatus reviewNotificationStatus) {
		this.reviewNotificationStatus = reviewNotificationStatus;
	}

	public String getModuleRef() {
		return moduleRef;
	}

	public void setModuleRef(String moduleRef) {
		this.moduleRef = moduleRef;
	}

	public String getTaskCreatedBy() {
		return taskCreatedBy;
	}

	public void setTaskCreatedBy(String taskCreatedBy) {
		this.taskCreatedBy = taskCreatedBy;
	}
}
