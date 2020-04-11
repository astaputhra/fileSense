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
@NamedQueries(value = {
		@NamedQuery(name = "FileSenseAuth.getRoleForFlow", query =
				" select fileSenseAuth.userRole from FileSenseAuth as fileSenseAuth where fileSenseAuth.flowName = ?1 and fileSenseAuth.isActive = 'Y' and company = ?2 and division = ?3 "),
        @NamedQuery(name = "FileSenseAuth.getUserId1ForFlow", query =
                " select fileSenseAuth.userId1 from FileSenseAuth as fileSenseAuth where fileSenseAuth.flowName = ?1 and fileSenseAuth.isActive = 'Y' "),
		@NamedQuery(name = "FileSenseAuth.getIsDuplicateAllowedForFlow", query =
				" select fileSenseAuth.isDuplicateAllowed from FileSenseAuth as fileSenseAuth where fileSenseAuth.flowName = ?1 and fileSenseAuth.isActive = 'Y' "),
		@NamedQuery(name = "FileSenseAuth.getRoleForCompanyAgnosticFlow", query =
				" select distinct fileSenseAuth.userRole from FileSenseAuth as fileSenseAuth where fileSenseAuth.flowName = ?1 and ((fileSenseAuth.company is null and fileSenseAuth.division is null) or (fileSenseAuth.isCompanySpecific = 'N' and fileSenseAuth.isDivisionSpecific = 'N') ) and fileSenseAuth.isActive = 'Y' "),
        @NamedQuery(name = "FileSenseAuth.getByFlowNameWhereETAExists", query =
				" select fileSenseAuth from FileSenseAuth as fileSenseAuth where fileSenseAuth.flowName = ?1 and fileSenseAuth.overHeadInSec is not null and fileSenseAuth.perRowCostInSec is not null and fileSenseAuth.isActive = 'Y' "),
		@NamedQuery(name = "FileSenseAuth.getFlowGrpsForRole", query =
                " select fileSenseAuth.flowGroup from FileSenseAuth as fileSenseAuth where fileSenseAuth.userRole = ?1 and fileSenseAuth.isActive = 'Y' "),
		@NamedQuery(name = "FileSenseAuth.getAuthorisedRolesForFlow", query =
                " select fileSenseAuth.userRole  from FileSenseAuth as fileSenseAuth where ((fileSenseAuth.isCompanySpecific='N' and  fileSenseAuth.isDivisionSpecific='N')  or (fileSenseAuth.isCompanySpecific='Y' and  fileSenseAuth.isDivisionSpecific='Y' and fileSenseAuth.company = ?2 and fileSenseAuth.division = ?3)) and fileSenseAuth.flowName = ?1 and fileSenseAuth.isActive='Y' "),
        @NamedQuery(name = "FileSenseAuth.getMaxDataSheetsByFlowName", query =
                " select fileSenseAuth.maxDataSheets  from FileSenseAuth as fileSenseAuth where ((fileSenseAuth.isCompanySpecific='N' and  fileSenseAuth.isDivisionSpecific='N')  or (fileSenseAuth.isCompanySpecific='Y' and  fileSenseAuth.isDivisionSpecific='Y' and fileSenseAuth.company = ?2 and fileSenseAuth.division = ?3)) and fileSenseAuth.flowName = ?1 and fileSenseAuth.isActive='Y' "),
        @NamedQuery(name = "FileSenseAuth.getFlowDesc", query =
                " select distinct fileSenseAuth.flowDescription from FileSenseAuth as fileSenseAuth where coalesce(fileSenseAuth.company, ?1) = ?1 and coalesce(fileSenseAuth.division, ?2) = ?2 and fileSenseAuth.flowName = ?3 and fileSenseAuth.isActive = 'Y' "),

})
@Table(name = "FILE_SENSE_AUTH", uniqueConstraints = { @UniqueConstraint(name = "I_FILE_SENSE_AUTH", columnNames = {
		"COMPANY", "DIVISION", "FLOW_NAME", "FLOW_GROUP", "USER_ROLE", "SRL_NO" }) })
public class FileSenseAuth extends AbstractCommonsEntity implements IEtlAuth{

    private static final long serialVersionUID = -8697760343280443023L;
    @Column(name = "COMPANY", length = 15)
	private String company;

	@Column(name = "DIVISION", length = 6)
	private String division;

	@Column(name = "IS_COMPANY_SPECIFIC", length = 1)
	@NotNull
	private boolean isCompanySpecific;

	@Column(name = "IS_DIVISION_SPECIFIC", length = 1)
	@NotNull
	private boolean isDivisionSpecific;

	@Size(max = 50)
	@Column(name = "FLOW_NAME", length = 50)
	@NotNull
	private String flowName;

    @Size(max = 60)
    @Column(name = "FLOW_DESCRIPTION", length = 60)
    @NotNull
    private String flowDescription;

    @Column(name = "FLOW_GROUP", length = 50)
	@NotNull
 	private String flowGroup;

	@Size(max = 100)
	@Column(name = "APPROVE_USER_ID", length = 100)
	@NotNull
	private String approveUserId;

    @Column(name = "SRL_NO", precision = 3, scale = 0)
    private BigDecimal srlNo = BigDecimal.ZERO;

    @Size(max = 9)
	@Column(name = "USER_ID1", length = 9)
	@NotNull
	private String userId1;

	@Size(max = 9)
	@Column(name = "USER_ID2", length = 9)
	private String userId2;

	@Size(max = 9)
	@Column(name = "USER_ID3", length = 9)
	private String userId3;

    @Column(name = "USER_ROLE", length = 15)
    private String userRole;

    @Column(name = "ROLE_GROUP", length = 6)
    @Enumerated(value = EnumType.STRING)
    private RoleGroup roleGroup;

    @Size(max = 50)
	@Column(name = "DATA_EMAIL_ID_IN", length = 50)
	private String dataEmailIdIn;

	@Size(max = 50)
	@Column(name = "DATA_EMAIL_ID_OUT", length = 50)
	private String dataEmailIdOut;

    @Column(name = "MODULE_KEY", length = 60)
    private String moduleKey;

    @Column(name = "IS_DUPLICATE_ALLOWED", length = 1)
    @NotNull
    private boolean isDuplicateAllowed;

    @Column(name = "IS_ACTIVE", length = 1)
	@NotNull
	private boolean isActive;

    @Size(max = 15)
    @Column(name = "PASSWORD", length = 15)
    private String password;

    @Size(max = 15)
    @Column(name = "ZIP_PASSWORD", length = 15)
    private String zipPassword;

    @Column(name = "PER_ROW_COST_IN_SEC")
    private int perRowCostInSec = 0;

    @Column(name = "OVERHEAD_IN_SEC")
    private int overHeadInSec = 0;

    @Column(name = "MAX_DATA_SHEETS")
    private BigDecimal maxDataSheets;

    @Column(name = "NUMBER_OF_ROWS")
    private int numberOfRows = 0;

    @Column(name = "PRE_PROCESS_METHOD")
    @Enumerated(value = EnumType.STRING)
    private PreProcessMethod preProcessMethod;

    @Column(name = "PRE_PROCESS_INPUT")
    private String preProcessInput;

    private transient String editedFields;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BigDecimal getMaxDataSheets() {
        return maxDataSheets;
    }

    public void setMaxDataSheets(BigDecimal maxDataSheets) {
        this.maxDataSheets = maxDataSheets;
    }

    public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

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

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

    public String getFlowDescription() {
        return flowDescription;
    }

    public void setFlowDescription(String flowDescription) {
        this.flowDescription = flowDescription;
    }

    public String getFlowGroup() {
        return flowGroup;
    }

    public void setFlowGroup(String flowGroup) {
        this.flowGroup = flowGroup;
    }

    public String getApproveUserId() {
		return approveUserId;
	}

	public void setApproveUserId(String approveUserId) {
		this.approveUserId = approveUserId;
	}

    public BigDecimal getSrlNo() {
        return srlNo;
    }

    public void setSrlNo(BigDecimal srlNo) {
        this.srlNo = srlNo;
    }

    public String getUserId1() {
		return userId1;
	}

	public void setUserId1(String userId1) {
		this.userId1 = userId1;
	}

	public String getUserId2() {
		return userId2;
	}

	public void setUserId2(String userId2) {
		this.userId2 = userId2;
	}

	public String getUserId3() {
		return userId3;
	}

	public void setUserId3(String userId3) {
		this.userId3 = userId3;
	}

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public RoleGroup getRoleGroup() {
        return roleGroup;
    }

    public void setRoleGroup(RoleGroup roleGroup) {
        this.roleGroup = roleGroup;
    }

    public String getDataEmailIdIn() {
		return dataEmailIdIn;
	}

	public void setDataEmailIdIn(String dataEmailIdIn) {
		this.dataEmailIdIn = dataEmailIdIn;
	}

	public String getDataEmailIdOut() {
		return dataEmailIdOut;
	}

	public void setDataEmailIdOut(String dataEmailIdOut) {
		this.dataEmailIdOut = dataEmailIdOut;
	}

    public String getModuleKey() {
        return moduleKey;
    }

    public void setModuleKey(String moduleKey) {
        this.moduleKey = moduleKey;
    }

    public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

    public String getZipPassword() {
        return zipPassword;
    }

    public void setZipPassword(String zipPassword) {
        this.zipPassword = zipPassword;
    }

    public int getPerRowCostInSec() {
        return perRowCostInSec;
    }

    public void setPerRowCostInSec(int perRowCostInSec) {
        this.perRowCostInSec = perRowCostInSec;
    }

    public int getOverHeadInSec() {
        return overHeadInSec;
    }

    public void setOverHeadInSec(int overHeadInSec) {
        this.overHeadInSec = overHeadInSec;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public boolean getIsDuplicateAllowed() {
		return isDuplicateAllowed;
	}

	public void setIsDuplicateAllowed(boolean isDuplicateAllowed) {
		this.isDuplicateAllowed = isDuplicateAllowed;
	}


    public PreProcessMethod getPreProcessMethod() {
        return preProcessMethod;
    }

    public void setPreProcessMethod(PreProcessMethod preProcessMethod) {
        this.preProcessMethod = preProcessMethod;
    }

    public String getPreProcessInput() {
        return preProcessInput;
    }

    public void setPreProcessInput(String preProcessInput) {
        this.preProcessInput = preProcessInput;
    }
}

