package com.demo.mslu.schedule.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Yahor Svidzinski
 */
@Data
@AllArgsConstructor
public class ScheduleResponse {

	private String time;
	private String subjectAndTeacherName;
	private String room;
}
