package com.iMatch.etl.models;

public interface IUploadFileReference {
    String getChecksum();
    String getUserId();
    String getFilePath();
}
