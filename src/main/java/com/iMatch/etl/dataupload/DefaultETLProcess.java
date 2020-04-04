package com.iMatch.etl.dataupload;


import com.iMatch.etl.models.EtlCompletedDO;

public class DefaultETLProcess extends AbstractETLProcess {


    @Override
    public ETLProcessCompleteDO process(EtlCompletedDO etlCompletedDO) throws DuplicateFileExistsException {
        ETLProcessCompleteDO etlProcessCompleteDO = new ETLProcessCompleteDO();
//        EtlEnrichedDO etlEnrichedDO = new EtlEnrichedDO(etlCompletedDO.getUploadID());
//        etlProcessCompleteDO.setPayload(etlEnrichedDO);
//        List<UploadErrors> uploadErrorsList =   dbFuncs.references.uploadErrors.findByUploadId(etlCompletedDO.getUploadID());
//        if (uploadErrorsList == null || uploadErrorsList.isEmpty() )
//            etlProcessCompleteDO.setProcessStatus(ProcessStatus.PROCEED_WITHOUT_CLEANUP);
//        else
//            etlProcessCompleteDO.setProcessStatus(ProcessStatus.PROCEED_WITH_CLEANUP);
//        etlProcessCompleteDO.setProcessStatus(ProcessStatus.PROCEED_WITHOUT_CLEANUP);
		return etlProcessCompleteDO;
	}
}