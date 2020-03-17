package com.demo.mslu.schedule.controller;

import com.demo.mslu.schedule.model.ScheduleRequest;
import com.demo.mslu.schedule.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author Yahor Svidzinski
 */
@RestController
@RequestMapping("/schedule")
@AllArgsConstructor
public class ScheduleController {

	private ScheduleService scheduleService;

	@PostMapping("/nextday")
	public String getNextDay(@RequestBody ScheduleRequest scheduleRequest) {
		return scheduleService.getNextDay(scheduleRequest);
	}
}
