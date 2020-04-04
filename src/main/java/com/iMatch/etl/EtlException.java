package com.iMatch.etl;

import com.iMatch.etl.models.PreETLErrorDO;

/**
 * Created with IntelliJ IDEA.
 * User: anish
 * Date: 30/11/13
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class EtlException extends Exception{
    private static final long serialVersionUID = -1721299767958813287L;
    private PreETLErrorDO preETLErrorDO;
    public EtlException(PreETLErrorDO preETLErrorDO){
        this.preETLErrorDO = preETLErrorDO;
    }

    public PreETLErrorDO getPreETLErrorDO() {
        return preETLErrorDO;
    }
}
