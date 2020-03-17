package com.demo.mslu.schedule.service;

import com.demo.mslu.schedule.model.ScheduleRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import static com.demo.mslu.schedule.util.ScheduleConstants.*;
import static org.springframework.http.HttpHeaders.COOKIE;
import static org.springframework.http.HttpMethod.GET;

/**
 * @author Yahor Svidzinski
 */
@Component
@Slf4j
public class ScheduleRequester {

	//Cookie was set after invocation of updateCourse method
	private String cookie;

	public InputStream requestReport(@NotNull ScheduleRequest scheduleRequest) {
		updateCourse(scheduleRequest.getCourse());
		updateFaculty(scheduleRequest.getFaculty());
		updateYear(scheduleRequest.getYear());
		updateGroup(scheduleRequest.getGroup());
		updateWeek(scheduleRequest.getWeek());

		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
		requestHeaders.add(COOKIE, cookie);

		final ResponseEntity<byte[]> response = new RestTemplate().exchange(SCHEDULE_URL + "." + PRINT_REPORT_FUNCTION,
				GET,
				new HttpEntity<String>(requestHeaders),
				byte[].class);
		return new ByteArrayInputStream(response.getBody());
	}

	//Update starts from course update because data is depended on choice of previous section
	private void updateCourse(@NotNull Integer course) {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("t:zoneid", "studyWeekZone");
		map.add("t:formid", "printForm");
		map.add("t:formcomponentid", "reports/publicreports/ScheduleListForGroupReport:printform");
		map.add("t:selectvalue", course.toString());


		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, postHeaders);
		final ResponseEntity<String> postResponse = new RestTemplate().postForEntity(
				SCHEDULE_URL + "." + COURSE_CHANGE_FUNCTION,
				entity,
				String.class);

		cookie = postResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
	}

	private void updateFaculty(@NotNull Integer faculty) {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		postHeaders.add(COOKIE, cookie);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("t:zoneid", "studyGroupZone");
		map.add("t:formid", "printForm");
		map.add("t:formcomponentid", "reports/publicreports/ScheduleListForGroupReport:printform");
		map.add("t:selectvalue", faculty.toString());

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, postHeaders);
		new RestTemplate().postForEntity(
				SCHEDULE_URL + "." + FACULTY_CHANGE_FUNCTION,
				entity,
				String.class);
	}

	private void updateYear(@NotNull Integer year) {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		postHeaders.add(COOKIE, cookie);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("t:zoneid", "studyWeekZone");
		map.add("t:formid", "printForm");
		map.add("t:formcomponentid", "reports/publicreports/ScheduleListForGroupReport:printform");
		map.add("t:selectvalue", year.toString());

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, postHeaders);
		new RestTemplate().postForEntity(
				SCHEDULE_URL + "." + YEAR_CHANGE_FUNCTION,
				entity,
				String.class);
	}

	private void updateGroup(@NotNull Integer group) {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		postHeaders.add(COOKIE, cookie);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("t:zoneid", "buttonZone");
		map.add("t:formid", "printForm");
		map.add("t:formcomponentid", "reports/publicreports/ScheduleListForGroupReport:printform");
		map.add("t:selectvalue", group.toString());

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, postHeaders);
		new RestTemplate().postForEntity(
				SCHEDULE_URL + "." + GROUP_CHANGE_FUNCTION,
				entity,
				String.class);
	}

	private void updateWeek(@NotNull Integer week) {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		postHeaders.add(COOKIE, cookie);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("t:zoneid", "buttonZone");
		map.add("t:formid", "printForm");
		map.add("t:formcomponentid", "reports/publicreports/ScheduleListForGroupReport:printform");
		map.add("t:selectvalue", week.toString());

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, postHeaders);
		new RestTemplate().postForEntity(
				SCHEDULE_URL + "." + WEEK_CHANGE_FUNCTION,
				entity,
				String.class);
	}
}
