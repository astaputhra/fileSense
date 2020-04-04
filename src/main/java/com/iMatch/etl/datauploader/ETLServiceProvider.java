package com.iMatch.etl.datauploader;

import com.iMatch.etl.datauploader.internal.SupportedUploadFileTypes;
import com.iMatch.etl.datauploader.internal.hexFileSense.SignatureOfEtlFlow;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: anish
 * Date: 2/1/13
 * Time: 2:07 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ETLServiceProvider {
    public String downloadData(String etlFlowName, Map<String, String> params) throws Exception;

    public EtlJobStats uploadData(String uploadID, String fileName, String etlFlowName, String updTableName, Map<String, String> params) throws Exception;

    public List<SignatureOfEtlFlow> getEtlFlowsSignaturesForFileType(SupportedUploadFileTypes fileType);

    public SignatureOfEtlFlow getEtlSig(String etlType, String displayName);

    public List<String> getEtlTypes();

    public Collection<SignatureOfEtlFlow> getEtlSigForType(String etlType);

    public List<SignatureOfEtlFlow> senseFileType(String filename, List<String> passwords);

    public String getEtlTypeForFlow(String flowName);

    public String getDisplayNameForFlow(String flowName);

    public String getParameterForFlow(String flowName, String param);

    public SignatureOfEtlFlow getSignatureForFlow(String flowname);

    boolean busy();
}
