package com.demo.mslu.schedule.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@AllArgsConstructor
@Slf4j
public class ScheduleController {

	@GetMapping("/nosleep")
	public String getNextDay() {
		return "No time to sleep";
	}
}