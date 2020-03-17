package com.demo.mslu.schedule.service;

import com.demo.mslu.schedule.model.ScheduleRequest;

import javax.validation.constraints.NotNull;

/**
 * @author Yahor Svidzinski
 */
public interface ScheduleService {

	String getNextDay(@NotNull ScheduleRequest scheduleRequest);
}
