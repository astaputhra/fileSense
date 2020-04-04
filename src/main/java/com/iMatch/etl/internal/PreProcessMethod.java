package com.iMatch.etl.internal;

import com.iMatch.etl.EtlDefinition;
import com.iMatch.etl.IPreProcessMethod;
import com.iMatch.etl.datauploader.ETLServiceProvider;
import com.iMatch.etl.preprocessmethods.*;

import java.io.File;

public enum PreProcessMethod {
    FIXED_DELAY(new ProcessAfterFixedDelay()),
    NOW_OR_AFTER(new ProcessAfterSpecifiedTime(true)),
    ALWAYS_AFTER(new ProcessAfterSpecifiedTime(false)),
    NOT_BETWEEN(new DoNotProcessBetween(false)),
    NOW_OR_NOT_BETWEEN(new DoNotProcessBetween(true)),
    FIXED_DELAY_AND_NOT_BETWEEN(new CompositePreProcessor(new ProcessAfterFixedDelay(),new DoNotProcessBetween(false))),
    FIXED_DELAY_AND_NOW_OR_NOT_BETWEEN(new CompositePreProcessor(new ProcessAfterFixedDelay(),new DoNotProcessBetween(true)));

    private final IPreProcessMethod handler;

    PreProcessMethod(IPreProcessMethod processAfterFixedDelay) {
        this.handler = processAfterFixedDelay;
    }

    public void apply(EtlDefinition fEtlDefinition, File file, ETLServiceProvider etlService) {
        handler.apply(fEtlDefinition,file,etlService);
    }

    public boolean shouldApply(File file, ETLServiceProvider etlService, PreProcessTime... preProcessTime) {
        return handler.shouldApplyDelay(etlService,preProcessTime);
    }

}
