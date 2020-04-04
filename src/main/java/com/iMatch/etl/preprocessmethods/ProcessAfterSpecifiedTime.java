package com.iMatch.etl.preprocessmethods;

import com.iMatch.etl.EtlDefinition;
import com.iMatch.etl.IPreProcessMethod;
import com.iMatch.etl.datauploader.ETLServiceProvider;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ProcessAfterSpecifiedTime implements IPreProcessMethod {
    private static final Logger logger = LoggerFactory.getLogger(ProcessAfterSpecifiedTime.class);

    private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT);
    private boolean processWhenFree;

    public ProcessAfterSpecifiedTime(boolean processWhenFree) {
        this.processWhenFree = processWhenFree;
    }

    @Override
    public void apply(EtlDefinition defn, File file, ETLServiceProvider etlService) {
        String preProcessInput = defn.getPreProcessInput();
        logger.debug("applying process-after-specified-time "+ (processWhenFree?"but process when free" : "")+  " method for time {} to file {}", preProcessInput, file.getName());
        while (true) {
            LocalTime configuredTime = timeFormatter.parseLocalTime(defn.getPreProcessInput());
            if (LocalTime.now().isAfter(configuredTime)) {
                break;
            }

            if (processWhenFree && !etlService.busy()) {
                logger.debug("etl service not busy");
                break;
            }

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.debug("done applying process-after-specified-time "+ (processWhenFree?"but process when free" : "")+ "method for time {} to file {}", preProcessInput, file.getName());
    }

    public boolean shouldApplyDelay(ETLServiceProvider etlService, PreProcessTime... preProcessTime) {
        if (LocalTime.now().isAfter(preProcessTime[0].endTime)) {
            return false;
        }

        if (processWhenFree && !etlService.busy()) {
            logger.debug("etl service not busy");
            return false;
        }
        return true;
    }
}
