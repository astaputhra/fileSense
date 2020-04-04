package com.iMatch.etl.models;

import com.iMatch.etl.enums.Channel;
import org.joda.time.LocalDate;

public class BackDatedForUploadDO implements IFileUploaderDO {

    private static final long serialVersionUID = 1L;

    private String uploadId;
    private String source;
    private LocalDate uploadDate;
    private String updFileName;
    private Channel channel;

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUpdFileName() {
        return updFileName;
    }

    public void setUpdFileName(String updFileName) {
        this.updFileName = updFileName;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
