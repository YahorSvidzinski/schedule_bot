package com.demo.mslu.schedule.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Yahor Svidzinski
 */
@Data
@Builder
@AllArgsConstructor
public class ScheduleRequest {

	private Integer course;
	private Integer faculty;
	private Integer year;
	private Integer group;
	private Integer week;
}
