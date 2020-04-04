package com.iMatch.etl;

import com.iMatch.etl.models.EtlStatusNotificationDO;

/**
 * Created with IntelliJ IDEA.
 * User: anish
 * Date: 30/11/13
 * Time: 10:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class EtlErrors extends Exception {
    private static final long serialVersionUID = 2044315451685566641L;
    public boolean retryFailed = false;
    private EtlStatusNotificationDO etlStatusNotificationDO;

    public EtlErrors(EtlStatusNotificationDO etlStatusNotificationDO){
        this.etlStatusNotificationDO = etlStatusNotificationDO;
    }

    public EtlErrors(EtlStatusNotificationDO etlStatusNotificationDO, boolean retryFailed) {
        this.etlStatusNotificationDO = etlStatusNotificationDO;
        this.retryFailed = retryFailed;
    }

    public EtlStatusNotificationDO getEtlStatusNotificationDO() {
        return etlStatusNotificationDO;
    }
}
