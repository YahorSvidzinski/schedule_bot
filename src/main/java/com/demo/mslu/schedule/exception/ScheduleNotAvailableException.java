package com.demo.mslu.schedule.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Aliaksandr Miron
 */
@Getter
@NoArgsConstructor
public class ScheduleNotAvailableException extends Exception {

    private String message;

    public ScheduleNotAvailableException(String message) {
        super(message);
        this.message = message;
    }
}
