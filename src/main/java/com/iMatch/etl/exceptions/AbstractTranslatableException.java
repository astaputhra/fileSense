package com.iMatch.etl.exceptions;

import com.iMatch.etl.IDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: shreyas
 * Date: 17/2/14
 * Time: 3:58 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTranslatableException extends RuntimeException  {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTranslatableException.class);
    private static final long serialVersionUID = -3816547935684926446L;

    private IDO requestDO;

    public void setRequestDO(IDO requestDO)
    {
        this.requestDO = requestDO;
    }

    public IDO getRequestDO() {
        return requestDO;
    }

    public AbstractTranslatableException(String message){
        super(message);
    }

}
