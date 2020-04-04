package com.iMatch.etl.internal;

import java.math.BigDecimal;

/**
 * Created by anishjoseph on 03/07/18.
 */
public class PostEtlPostOverwriteSystemException extends RuntimeException {
    private BigDecimal sysTaskId;
    private Throwable originalException;

    public PostEtlPostOverwriteSystemException(BigDecimal sysTaskId, Throwable originalException) {
        this.sysTaskId = sysTaskId;
        this.originalException = originalException;
    }

    public BigDecimal getSysTaskId() {
        return sysTaskId;
    }

    public void setSysTaskId(BigDecimal sysTaskId) {
        this.sysTaskId = sysTaskId;
    }

    public Throwable getOriginalException() {
        return originalException;
    }

    public void setOriginalException(Throwable originalException) {
        this.originalException = originalException;
    }
}
