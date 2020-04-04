package com.iMatch.etl.internal;

import com.iMatch.etl.enums.NotificationResponseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class ReviewDetails implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ReviewDetails.class);

    private String rejectedBy;
    private String recRemarks;
    private String statusMsg;
    private NotificationResponseType notificationResponseType;


    public ReviewDetails() {
    }

    public ReviewDetails(NotificationResponseType notificationResponseType, String rejectedBy, String recRemarks) {
        this.recRemarks = recRemarks;
        this.notificationResponseType = notificationResponseType;
        this.rejectedBy = rejectedBy;
    }

    public String getRecRemarks() {
        return recRemarks;
    }

    public void setRecRemarks(String recRemarks) {
        this.recRemarks = recRemarks;
    }

    public NotificationResponseType getNotificationResponseType() {
        return notificationResponseType;
    }

    public void setNotificationResponseType(NotificationResponseType notificationResponseType) {
        this.notificationResponseType = notificationResponseType;
    }

    public String getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }
}