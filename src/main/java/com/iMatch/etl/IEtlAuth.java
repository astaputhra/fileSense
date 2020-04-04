package com.iMatch.etl;

import com.iMatch.etl.internal.PreProcessMethod;

/**
 * Created with IntelliJ IDEA.
 * User: anish
 * Date: 7/20/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IEtlAuth {
    public String getCompany();
    public String getDivision();
    public String getFlowName();
    public String getApproveUserId();
    public String getUserId1();
    public String getUserId2();
    public String getUserId3();
    public String getDataEmailIdIn();
    public String getDataEmailIdOut();
    public boolean getIsDivisionSpecific();
    public boolean getIsCompanySpecific();
    PreProcessMethod getPreProcessMethod();
    String getPreProcessInput();
    String getUserRole();
}
