package com.iMatch.etl.internal;


import com.iMatch.etl.IHexGenExceptionMessage;
import com.iMatch.etl.exceptions.HexGenException;
import com.iMatch.etl.models.EtlStatusNotificationDO;

/**
 * Created with IntelliJ IDEA.
 * User: akash
 * Date: 10/6/13
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class PostETLRollbackException extends HexGenException {

    private static final long serialVersionUID = -500780874533247421L;
    private EtlStatusNotificationDO etlProcessCompleteDO;

    public PostETLRollbackException(IHexGenExceptionMessage message, EtlStatusNotificationDO etlStatusNotificationDO) {
        super(message);
        this.etlProcessCompleteDO = etlStatusNotificationDO;
    }

    public EtlStatusNotificationDO getEtlProcessCompleteDO() {
        return etlProcessCompleteDO;
    }

}
