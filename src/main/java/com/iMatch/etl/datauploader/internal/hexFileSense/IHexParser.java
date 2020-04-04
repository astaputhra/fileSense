package com.iMatch.etl.datauploader.internal.hexFileSense;


import java.util.List;

public interface IHexParser {
    public List<SignatureOfEtlFlow> getMatchingFlows(String filename, List<SignatureOfEtlFlow> flows, String expectedFlowname, List<String> passwords);
}
