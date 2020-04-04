package com.iMatch.etl;

import com.iMatch.etl.datauploader.ETLServiceProvider;
import com.iMatch.etl.preprocessmethods.PreProcessTime;

import java.io.File;

public interface IPreProcessMethod {
    public static final String TIME_FORMAT = "HH:mm:ss";

    void apply(EtlDefinition defn, File file, ETLServiceProvider etlService);
    boolean shouldApplyDelay(ETLServiceProvider etlService, PreProcessTime... preProcessTime);
}