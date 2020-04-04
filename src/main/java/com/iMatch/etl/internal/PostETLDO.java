package com.iMatch.etl.internal;

import com.iMatch.etl.IDO;
import com.iMatch.etl.models.ArchiveUploadedFileDO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: akash
 * Date: 10/6/13
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class PostETLDO implements IDO {

    private static final long serialVersionUID = 5421771971117694171L;
    ArchiveUploadedFileDO archiveUploadedFileDO;
    List<IDO> payloads;

    public ArchiveUploadedFileDO getArchiveUploadedFileDO() {
        return archiveUploadedFileDO;
    }

    public void setArchiveUploadedFileDO(ArchiveUploadedFileDO archiveUploadedFileDO) {
        this.archiveUploadedFileDO = archiveUploadedFileDO;
    }

    public List<IDO> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<IDO> payloads) {
        this.payloads = payloads;
    }
}
