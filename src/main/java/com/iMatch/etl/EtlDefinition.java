package com.iMatch.etl;

import com.iMatch.etl.enums.UploadErrorType;
import com.iMatch.etl.internal.PreProcessMethod;

public class EtlDefinition {
	private String etlFlow;
	private String genericType;
	private String displayName;

	private String company;
	private String division;
	private String approveUserId;
	private String dataEmailIdIn;
	private String dataEmailIdOut;
	private boolean isCompanySpecific;
	private boolean isDivisionSpecific;
	private PreProcessMethod preProcessMethod;
	private String preProcessInput;
	private String userRole;

	private UploadErrorType errorType;

	public boolean getIsCompanySpecific() {
		return isCompanySpecific;
	}

	public void setIsCompanySpecific(boolean isCompanySpecific) {
		this.isCompanySpecific = isCompanySpecific;
	}

	public boolean getIsDivisionSpecific() {
		return isDivisionSpecific;
	}

	public void setIsDivisionSpecific(boolean isDivisionSpecific) {
		this.isDivisionSpecific = isDivisionSpecific;
	}

	public String getApproveUserId() {
		return approveUserId;
	}

	public void setApproveUserId(String approveUserId) {
		this.approveUserId = approveUserId;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDataEmailIdIn() {
		return dataEmailIdIn;
	}

	public void setDataEmailIdIn(String dataEmailIdIn) {
		this.dataEmailIdIn = dataEmailIdIn;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getDataEmailIdOut() {
		if (dataEmailIdOut != null)
			return dataEmailIdOut;
		return dataEmailIdIn;
	}

	public void setDataEmailIdOut(String dataEmailIdOut) {
		this.dataEmailIdOut = dataEmailIdOut;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEtlFlow() {
		return etlFlow;
	}

	public void setEtlFlow(String etlFlow) {
		this.etlFlow = etlFlow;
	}

	public String getGenericType() {
		return genericType;
	}

	public void setGenericType(String genericType) {
		this.genericType = genericType;
	}

	public boolean isValidExternalUser(String fromEmailID) {
		if (dataEmailIdIn == null)
			return false;
		return dataEmailIdIn.equalsIgnoreCase(fromEmailID);
	}

	public String getUser() {
		return approveUserId;
	}

	public String getPreProcessInput() {
		return preProcessInput;
	}

	public PreProcessMethod getPreProcessMethod() {
		return preProcessMethod;
	}

	public void setPreProcessMethod(PreProcessMethod preProcessMethod) {
		this.preProcessMethod = preProcessMethod;
	}

	public void setPreProcessInput(String preProcessInput) {
		this.preProcessInput = preProcessInput;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public boolean isCompanySpecific() {
		return isCompanySpecific;
	}

	public void setCompanySpecific(boolean companySpecific) {
		isCompanySpecific = companySpecific;
	}

	public boolean isDivisionSpecific() {
		return isDivisionSpecific;
	}

	public void setDivisionSpecific(boolean divisionSpecific) {
		isDivisionSpecific = divisionSpecific;
	}

	public UploadErrorType getErrorType() {
		return errorType;
	}

	public void setErrorType(UploadErrorType errorType) {
		this.errorType = errorType;
	}
}
