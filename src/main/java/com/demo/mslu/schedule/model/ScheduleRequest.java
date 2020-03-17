package com.demo.mslu.schedule.model;

import lombok.Data;

/**
 * @author Yahor Svidzinski
 */
@Data
public class ScheduleRequest {

	private Integer course;
	private Integer faculty;
	private Integer year;
	private Integer group;
	private Integer week;
}
