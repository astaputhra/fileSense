package com.iMatch.etl.models;

public class EtlReviewedDO implements IFileUploaderDO {
	private static final long serialVersionUID = 1L;
    private String uploadId;

    public EtlReviewedDO(){}

    public EtlReviewedDO(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

}
