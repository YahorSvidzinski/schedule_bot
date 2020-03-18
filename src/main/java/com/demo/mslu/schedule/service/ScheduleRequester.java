package com.demo.mslu.schedule.service;

import com.demo.mslu.schedule.model.ScheduleRequest;
import com.demo.mslu.schedule.model.ZoneType;
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

import static com.demo.mslu.schedule.model.ZoneType.*;
import static com.demo.mslu.schedule.util.ScheduleConstants.*;
import static org.springframework.http.HttpHeaders.COOKIE;
import static org.springframework.http.HttpMethod.GET;

/**
 * @author Yahor Svidzinski
 */
@Component
@Slf4j
public class ScheduleRequester {

    private static final String T_ZONE_ID = "t:zoneid";
    private static final String T_FORM_ID = "t:formid";
    private static final String T_FORM_COMPONENT_ID = "t:formcomponentid";
    private static final String T_SELECT_VALUE = "t:selectvalue";

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

        HttpEntity<MultiValueMap<String, String>> entity = getHttpEntityWithDefaultValues(course, postHeaders, STUDY_WEEK_ZONE);
        final ResponseEntity<String> postResponse = new RestTemplate().postForEntity(
                SCHEDULE_URL + "." + COURSE_CHANGE_FUNCTION,
                entity,
                String.class);

        cookie = postResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
    }

    private void updateFaculty(@NotNull Integer faculty) {
        updateEntityWithCookies(faculty, STUDY_GROUP_ZONE, FACULTY_CHANGE_FUNCTION);
    }

    private void updateYear(@NotNull Integer year) {
        updateEntityWithCookies(year, STUDY_WEEK_ZONE, YEAR_CHANGE_FUNCTION);
    }

    private void updateGroup(@NotNull Integer group) {
        updateEntityWithCookies(group, BUTTON_ZONE, GROUP_CHANGE_FUNCTION);
    }

    private void updateWeek(@NotNull Integer week) {
        updateEntityWithCookies(week, BUTTON_ZONE, WEEK_CHANGE_FUNCTION);
    }

    private void updateEntityWithCookies(@NotNull Integer entity, ZoneType zoneType, String entityChangeFunction) {
        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        postHeaders.add(COOKIE, cookie);

        HttpEntity<MultiValueMap<String, String>> httpEntity = getHttpEntityWithDefaultValues(entity, postHeaders, zoneType);
        new RestTemplate().postForEntity(
                SCHEDULE_URL + "." + entityChangeFunction,
                httpEntity,
                String.class);
    }

    private HttpEntity<MultiValueMap<String, String>> getHttpEntityWithDefaultValues(@NotNull Integer course, HttpHeaders postHeaders, ZoneType zoneType) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(T_ZONE_ID, zoneType.getValue());
        map.add(T_FORM_ID, "printForm");
        map.add(T_FORM_COMPONENT_ID, "reports/publicreports/ScheduleListForGroupReport:printform");
        map.add(T_SELECT_VALUE, course.toString());


        return new HttpEntity<>(map, postHeaders);
    }
}
