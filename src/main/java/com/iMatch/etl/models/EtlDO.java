package com.iMatch.etl.models;

import java.math.BigDecimal;

public class EtlDO implements IFileUploaderDO {

private static final long serialVersionUID = 1L;

	private BigDecimal jobID = BigDecimal.ZERO;

	public BigDecimal getJobID() {
		return jobID;
	}

	public void setJobID(BigDecimal jobID) {
		this.jobID = jobID;
	}

	public EtlDO(BigDecimal jobID) {
		super();
		this.jobID = jobID;
	}

}
