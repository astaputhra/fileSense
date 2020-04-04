package com.iMatch.etl.preprocessmethods;

import com.iMatch.etl.EtlDefinition;
import com.iMatch.etl.IPreProcessMethod;
import com.iMatch.etl.datauploader.ETLServiceProvider;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ProcessAfterFixedDelay implements IPreProcessMethod {
    private static final Logger logger = LoggerFactory.getLogger(ProcessAfterFixedDelay.class);

    @Override
    public void apply(EtlDefinition defn, File file, ETLServiceProvider etlService) {
        logger.debug("applying proces-after-fixed-delay of {} seconds to file {}",defn.getPreProcessInput(), file.getName());
        try {
            TimeUnit.SECONDS.sleep(Long.parseLong(defn.getPreProcessInput()));
        } catch (InterruptedException e) {
            // on purpose
        }
    }

    @Override
    public boolean shouldApplyDelay(ETLServiceProvider etlService, PreProcessTime... preProcessTime) {
        return LocalTime.now().isBefore(preProcessTime[0].endTime);
    }
}