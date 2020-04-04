package com.iMatch.etl.datauploader.internal.kettle;

import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.LogMessage;
import org.pentaho.di.core.logging.LoggingPluginInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HexKettleLoggingPlugin implements LoggingPluginInterface {

    private static final Logger logger = LoggerFactory.getLogger(HexKettleLoggingPlugin.class);


    Map<String, ArrayList<String>> kettleErrors = new ConcurrentHashMap<>();

    public HexKettleLoggingPlugin() {
    }

    @Override
    public void init() {
        logger.debug("init called");
    }

    @Override
    public void dispose() {
        logger.debug("dispose called");
    }

    @Override
    public void eventAdded(KettleLoggingEvent event) {
        LogMessage message = (LogMessage) event.getMessage();
        String channelId = message.getLogChannelId();
//        String subject = message.getSubject() + " ";
        String subject = "";

        String logText = message.getMessage();

        switch (event.getLevel()) {
            case ERROR :
                logger.error(subject + logText);
                captureError(channelId, logText);
                break;
            case MINIMAL :
                logger.info(subject + logText);
                break;
            case BASIC :
                logger.debug(subject + logText);
                break;
            case DETAILED :
            case DEBUG :
            case ROWLEVEL:
                logger.trace(subject + logText);
                break;
            case NOTHING:
                break;
            default:
                logger.debug(subject + logText);
        }
    }

    private void captureError(String logChannelId, String logText) {
        logger.trace("capturing errors for " + logChannelId + " " + logText);
        kettleErrors.computeIfAbsent(logChannelId, k -> new ArrayList<>());
        kettleErrors.get(logChannelId).add(logText);
    }

    private List<String> getErrors(String channelId) {
        ArrayList<String> errorsList = kettleErrors.remove(channelId);
        logger.trace("returning errors for " + channelId + " " + errorsList);
        return errorsList;
    }

    public List<String> getKettleErrors(Trans trans) {
        List<String> errors = new ArrayList<>();

        errors.add("Kettle Error : " + getErrors(trans.getLogChannelId()));

        List<StepMetaDataCombi> steps = trans.getSteps();
        for (StepMetaDataCombi step : steps) {
            List<String> stepErrors = getErrors(step.step.getLogChannel().getLogChannelId());
            if (stepErrors != null && !stepErrors.isEmpty())
                errors.add("Step Errors : " + step.stepname + " " + stepErrors);
        }
        return errors;
    }

    public void clearErrors(Trans trans) {
        logger.debug("cleaning up errors for " + trans.getTransMeta().getName());
        List<String> errors = getErrors(trans.getLogChannelId());
        if (errors != null)
            logger.trace("cleaned up errors for " + trans.getLogChannelId() + " errors " + errors);

        List<StepMetaDataCombi> steps = trans.getSteps();
        for (StepMetaDataCombi step : steps) {
            errors = getErrors(step.step.getLogChannel().getLogChannelId());
            if (errors != null)
                logger.trace("cleaned up step errors for " + trans.getLogChannelId() + " step errors " + errors);
        }
    }

}