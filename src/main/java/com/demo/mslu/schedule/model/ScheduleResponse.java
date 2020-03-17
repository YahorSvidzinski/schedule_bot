package com.demo.mslu.schedule.model;

import lombok.Data;

/**
 * @author Yahor Svidzinski
 */
@Data
public class ScheduleResponse {

	private String day;
	private String time;
	private String subjectName;
	private String room;
}
