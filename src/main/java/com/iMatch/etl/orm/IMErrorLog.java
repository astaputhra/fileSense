package com.iMatch.etl.orm;

import com.iMatch.etl.IEtlAuth;
import com.iMatch.etl.enums.RoleGroup;
import com.iMatch.etl.internal.PreProcessMethod;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Configurable
@Table(name = "IM_EVENT_LOG")
public class IMErrorLog extends AbstractCommonsEntity {

    private static final long serialVersionUID = 4208510215882718007L;
    @Column(name = "UPD_IDENTIFIER", length = 100)
    private String uploadId;

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }
}

