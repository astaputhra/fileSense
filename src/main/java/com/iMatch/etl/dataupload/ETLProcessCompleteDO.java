package com.iMatch.etl.dataupload;

import com.iMatch.etl.IDO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: akash
 * Date: 10/6/13
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ETLProcessCompleteDO implements IDO {
    private ProcessStatus processStatus;
    private IDO payload;
    private List<IDO> payloads;

    public ETLProcessCompleteDO() {
        processStatus = ProcessStatus.PROCEED_WITH_CLEANUP;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public IDO getPayload() {
        return payload;
    }

    public void setPayload(IDO payload) {
        this.payload = payload;
    }

    public List<IDO> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<IDO> payloads) {
        this.payloads = payloads;
    }
}
