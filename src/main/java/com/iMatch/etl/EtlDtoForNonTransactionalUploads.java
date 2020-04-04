package com.iMatch.etl;

import com.iMatch.etl.models.EtlStatusNotificationDO;

/**
 * Created with IntelliJ IDEA.
 * User: anish
 * Date: 1/12/13
 * Time: 8:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class EtlDtoForNonTransactionalUploads {
    private EtlStatusNotificationDO etlStatusNotificationDO;
    private String postUploadEvent;

    public String getPostUploadEvent() {
        return postUploadEvent;
    }

    public void setPostUploadEvent(String postUploadEvent) {
        this.postUploadEvent = postUploadEvent;
    }

    public EtlStatusNotificationDO getEtlStatusNotificationDO() {
        return etlStatusNotificationDO;
    }

    public void setEtlStatusNotificationDO(EtlStatusNotificationDO etlStatusNotificationDO) {
        this.etlStatusNotificationDO = etlStatusNotificationDO;
    }

}
