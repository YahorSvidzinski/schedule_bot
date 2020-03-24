package com.demo.mslu.schedule.service;

import com.demo.mslu.schedule.exception.ScheduleNotAvailableException;
import com.demo.mslu.schedule.model.ScheduleRequest;
import com.demo.mslu.schedule.model.constant.Week;

import javax.validation.constraints.NotNull;

/**
 * @author Yahor Svidzinski
 */
public interface ScheduleService {

	String getForDay(@NotNull ScheduleRequest scheduleRequest, @NotNull Integer dayOfWeek, Week week) throws ScheduleNotAvailableException;

	String getForWeek(@NotNull ScheduleRequest scheduleRequest, Week week) throws ScheduleNotAvailableException;
}
