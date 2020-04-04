package com.iMatch.etl.models;

public class EtlCleanupErrorsDO implements IFileUploaderDO {

	private static final long serialVersionUID = 1L;

    private String uploadID;

    public String getUploadID() {
        return uploadID;
    }

    public void setUploadID(String uploadID) {
        this.uploadID = uploadID;
    }
}
