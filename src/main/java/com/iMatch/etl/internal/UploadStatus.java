package com.iMatch.etl.internal;

public enum UploadStatus {
    PRE_ETL_ERROR,                          // An error occured before processing (i.e. duplicate file, etc)
	ETL_FAILED, 				            // Some error in either calling kettle, or kettle ran into some errors
	PENDING_DUP_APPROVAL, 		            // File is duplicate pending approval by user to either overwrite or discard.
    BUSINESS_PROCESS_COMPLETED,             // ETL has completed processing the file
    BUSINESS_PROCESS_COMPLETED_WITH_ERRORS, // ETL has completed processing the file however errors are present
    BUSINESS_PROCESS_ABORTED,               // ETL failed during processing of file (business logic)
    USER_ABORTED_DUPLICATE,                 // ETL aborted by user as this is duplicate
    BUSINESS_VALIDATION_FAILED,             // ETL aborted by user as this is duplicate
    DUPLICATE_FILE_ALREADY_PENDING_APPROVAL,// ETL aborted by user as this is duplicate
    SYSTEM_ERROR_IN_P1,                     // Business logic on P1 side has run into an exception
    SENT_TO_P1_FOR_PROCESSING,              // Business logic on P1 side has run into an exception
    USER_REJECTED_AT_REVIEW,                // Business logic on P1 side has run into an exception
    REVIEWER_REJECTED_AT_REVIEW,            // Business logic on P1 side has run into an exception
    PENDING_BACKDATED_APPROVAL, 		    // File is back dated approval pending by user to either overwrite or discard.
    BACKDATED_ALREADY_PENDING_APPROVAL,     // ETL aborted by user as this is duplicate
    PENDING_REVIEW,                         // File is under Review
    POST_ETL_ERROR,                         // Aborted due to errors in Post etl process
    COMPLIANCE_HARD_BREACH,                 // Record had hard breach
}
