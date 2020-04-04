package com.iMatch.etl.models;


public class EtlCompletedDO implements IFileUploaderDO {

	private static final long serialVersionUID = 1L;

    private String uploadID;
    private String updFileName;

    public EtlCompletedDO(String uploadID) {
        setUploadID(uploadID);
	}

    public EtlCompletedDO(String uploadID, String updFileName) {
        setUploadID(uploadID);
        setUpdFileName(updFileName);
	}

    public String getUploadID() {
        return uploadID;
    }

    public void setUploadID(String uploadID) {
        this.uploadID = uploadID;
    }

    public String getUpdFileName() {
        return updFileName;
    }

    public void setUpdFileName(String updFileName) {
        this.updFileName = updFileName;
    }

}
