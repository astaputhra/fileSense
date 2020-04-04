package com.iMatch.etl.preprocessmethods;

import org.joda.time.LocalTime;

public class PreProcessTime {

    LocalTime startTime = LocalTime.now();

    LocalTime endTime = null;


    public PreProcessTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public PreProcessTime(Integer seconds) {
        this.endTime = LocalTime.now().plusSeconds(seconds);
    }

    public PreProcessTime(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
