package com.iMatch.etl.internal;

import com.iMatch.etl.models.EtlStatusNotificationDO;

/**
 * Created by anishjoseph on 03/07/18.
 */
public class PostEtlPostOverwriteBusinessException extends RuntimeException {
    EtlStatusNotificationDO etlStatusNotificationForNonTransactional;
    public PostEtlPostOverwriteBusinessException(EtlStatusNotificationDO etlStatusNotificationForNonTransactional) {
        super();
        this.etlStatusNotificationForNonTransactional = etlStatusNotificationForNonTransactional;
    }

    public EtlStatusNotificationDO getEtlStatusNotificationForNonTransactional() {
        return etlStatusNotificationForNonTransactional;
    }

    public void setEtlStatusNotificationForNonTransactional(EtlStatusNotificationDO etlStatusNotificationForNonTransactional) {
        this.etlStatusNotificationForNonTransactional = etlStatusNotificationForNonTransactional;
    }
}
