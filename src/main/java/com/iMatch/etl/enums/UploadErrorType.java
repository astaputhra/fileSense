package com.iMatch.etl.enums;

/**
 * Created with IntelliJ IDEA.
 * User: anish
 * Date: 6/27/13
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */
public enum UploadErrorType {
    UNAUTHORIZED_EXTERNAL_EMAILID, //External user is authorized for at least one flow but not authorized for the flow that this file type maps to
    UNAUTHORIZED_USER,  //Internal user is authorized for at least one flow but not authorized for the flow that this file type maps to
    NO_ATTACHEMENT_FOUND,  //Email came with no attachment
    SYSTEM_ERROR,
    NO_MAPPING_FLOW,  //Unable to sense which flow maps to this file
    DUPLICATE, //File with same checksum already exists
    UNKNOWN_EXTERNAL_EMAILID, //Email received from user that is not configured to upload via email
    MULTIPLE_MAPPING_FOUND, //File maps to multiple flows
    WRONG_DIRECTORY, //File needs to be in a certain directory to determine company and division
    NO_LEAF_FOUND, //Flow determined but this flow does not map to a leaf in the etl-directory-config.xml
    PENDING_APPROVAL, //Flow determined but this flow does not map to a leaf in the etl-directory-config.xml
    DUP_PENDING_APPROVAL, //Flow determined but this flow does not map to a leaf in the etl-directory-config.xml
    PASSWORD_EXCEPTION, //Flow determined but this flow does not map to a leaf in the etl-directory-config.xml
    USER_REJECTED_AT_REVIEW, //Flow determined but this flow does not map to a leaf in the etl-directory-config.xml
    REVIEWER_REJECTED_AT_REVIEW, //Flow determined but this flow does not map to a leaf in the etl-directory-config.xml
    BACKDATED_PENDING_APPROVAL, //Flow determined but this flow does not map to a leaf in the etl-directory-config.xml
    BUSINESS_ERROR, //Flow determined but this flow does not map to a leaf in the etl-directory-config.xml
    MAX_SHEETS_BREACHED, //Workbooks has more sheets than permitted
    NO_ACTIVE_USER, //Workbooks has more sheets than permitted

}
