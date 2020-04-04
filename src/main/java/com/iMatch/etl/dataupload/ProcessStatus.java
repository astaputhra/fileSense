package com.iMatch.etl.dataupload;

public enum ProcessStatus {

    /*
     * Call cleanup method of class after processing
     */
    PROCEED_WITH_CLEANUP,

    /*
     * Proceed without calling cleanup method of class
     */
    PROCEED_WITHOUT_CLEANUP,

    /*
     * Rollback transaction
     */
    ROLLBACK

}
