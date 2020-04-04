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

public class DoNotProcessBetween implements IPreProcessMethod {
    private static final Logger logger = LoggerFactory.getLogger(DoNotProcessBetween.class);

    private final boolean processWhenFree;
    private DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT);

    public DoNotProcessBetween(boolean processWhenFree) {
        this.processWhenFree = processWhenFree;
    }

    @Override
    public void apply(EtlDefinition defn, File file, ETLServiceProvider etlService) {
        String preProcessInput = defn.getPreProcessInput();
        logger.debug("applying do-not-process-between " + (processWhenFree?"but process when free" : "")+ "  method for time {} to file {}", preProcessInput, file.getName());
        while (true) {
            String[] processingTime = preProcessInput.split(",");
            LocalTime start = timeFormatter.parseLocalTime(processingTime[0]);
            LocalTime end = timeFormatter.parseLocalTime(processingTime[1]);
            LocalTime now = LocalTime.now();
            if (now.isBefore(start) || now.isAfter(end)) {
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
        logger.debug("done applying do-not-process-between "+ (processWhenFree?"but process when free" : "")+ " method for time {} to file {}", preProcessInput, file.getName());
    }

    @Override
    public boolean shouldApplyDelay(ETLServiceProvider etlService, PreProcessTime... preProcessTime) {
        LocalTime now = LocalTime.now();
        if (now.isBefore(preProcessTime[0].startTime) || now.isAfter(preProcessTime[0].endTime)) {
            return false;
        }

        if (processWhenFree && !etlService.busy()) {
            logger.debug("etl service not busy");
            return false;
        }
        return true;
    }


}