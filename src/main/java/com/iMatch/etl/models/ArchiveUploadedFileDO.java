package com.iMatch.etl.models;

import com.iMatch.etl.IDO;

/**
 * Created with IntelliJ IDEA.
 * User: akash
 * Date: 18/7/13
 * Time: 11:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArchiveUploadedFileDO implements IFileUploaderDO {
    private static final long serialVersionUID = -2007935592474945551L;
    private IDO notificationDO;
    private String uploadId;

    public IDO getNotificationDO() {
        return notificationDO;
    }

    public void setNotificationDO(IDO notificationDO) {
        this.notificationDO = notificationDO;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
}
