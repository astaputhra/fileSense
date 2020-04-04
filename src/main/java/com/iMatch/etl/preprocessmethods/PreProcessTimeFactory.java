package com.iMatch.etl.preprocessmethods;

import com.iMatch.etl.internal.PreProcessMethod;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class PreProcessTimeFactory {
    public static final String TIME_FORMAT = "HH:mm:ss";

    public static PreProcessTime[] getTimeInputs(PreProcessMethod method, String input) {
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(TIME_FORMAT);
        switch (method) {
            case FIXED_DELAY:
                return new PreProcessTime[]{new PreProcessTime(Integer.parseInt(input))};
            case NOW_OR_AFTER:
            case ALWAYS_AFTER:
                LocalTime configuredTime = timeFormatter.parseLocalTime(input);
                return new PreProcessTime[]{new PreProcessTime(configuredTime)};
            case NOT_BETWEEN:
            case NOW_OR_NOT_BETWEEN:
                String[] processingTime = input.split(",");
                LocalTime start = timeFormatter.parseLocalTime(processingTime[0]);
                LocalTime end = timeFormatter.parseLocalTime(processingTime[1]);
                return new PreProcessTime[]{new PreProcessTime(start, end)};
            case FIXED_DELAY_AND_NOT_BETWEEN:
            case FIXED_DELAY_AND_NOW_OR_NOT_BETWEEN:
                String[] inputs = input.split(" ");
                PreProcessTime[] preProcessTimes = new PreProcessTime[2];
                preProcessTimes[0] = new PreProcessTime(Integer.parseInt(inputs[0]));

                String[] processingTime2 = inputs[1].split(",");
                LocalTime start1 = timeFormatter.parseLocalTime(processingTime2[0]);
                LocalTime end1 = timeFormatter.parseLocalTime(processingTime2[1]);
                preProcessTimes[1] = new PreProcessTime(start1,end1);
                return preProcessTimes;
            default:
                throw new RuntimeException("pre-process-method " + method.name() + " not handled");
        }
    }
}
