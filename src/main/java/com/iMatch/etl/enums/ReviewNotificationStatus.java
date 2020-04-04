package com.iMatch.etl.enums;

/**
 * Created by venkat on 24-10-2019.
 */
public enum ReviewNotificationStatus {
    PENDING_DUP_APPROVAL, 		     // File is duplicate pending approval by user to either overwrite or discard.
    PENDING_BACKDATED_APPROVAL, 	// File is back dated approval pending by user to either overwrite or discard.
    PENDING_REVIEW,                 // File is under Review pending approval by user to either overwrite or discard
}
