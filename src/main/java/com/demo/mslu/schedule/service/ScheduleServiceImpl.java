package com.demo.mslu.schedule.service;

import com.demo.mslu.schedule.exception.ScheduleNotAvailableException;
import com.demo.mslu.schedule.model.Schedule;
import com.demo.mslu.schedule.model.ScheduleRequest;
import com.demo.mslu.schedule.model.ScheduleResponse;
import com.demo.mslu.schedule.model.constant.Week;
import com.demo.mslu.schedule.repository.ScheduleRepository;
import com.google.common.collect.Multimap;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collection;

import static com.demo.mslu.schedule.converter.ScheduleConverter.convertDayToTelegramResponse;
import static com.demo.mslu.schedule.converter.ScheduleConverter.convertSheetToScheduleMap;
import static com.demo.mslu.schedule.converter.ScheduleConverter.scheduleToString;

/**
 * @author Yahor Svidzinski
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRequester scheduleRequester;
    private final ScheduleRepository scheduleRepository;

    @Override
    public String getForDay(@NotNull ScheduleRequest scheduleRequest, @NotNull Integer dayOfWeek, Week week) throws ScheduleNotAvailableException {
        if (scheduleIsNotSaved(week)) {
            requestSchedule(scheduleRequest, week);
        }

        return getDaySchedule(week, dayOfWeek);
    }

    @Override
    public String getForWeek(@NotNull ScheduleRequest scheduleRequest, Week week) throws ScheduleNotAvailableException {
        if (scheduleIsNotSaved(week)) {
            requestSchedule(scheduleRequest, week);
        }

        return getWeekSchedule(week);
    }

    private String getWeekSchedule(Week week) {
        Schedule schedule = scheduleRepository.findByWeek(week).orElseThrow();
        return scheduleToString(schedule);
    }

    private String getDaySchedule(Week week, Integer dayOfWeek) {
        Schedule schedule = scheduleRepository.findByWeek(week).orElseThrow();
        return switch (dayOfWeek) {
            case 1 -> schedule.getMonday();
            case 2 -> schedule.getTuesday();
            case 3 -> schedule.getWednesday();
            case 4 -> schedule.getThursday();
            case 5 -> schedule.getFriday();
            case 6 -> schedule.getSaturday();
            default -> "";
        };
    }

    private void requestSchedule(@NotNull ScheduleRequest scheduleRequest, Week week) throws ScheduleNotAvailableException {
        scheduleRequest.setWeek(scheduleRequest.getWeek() + calculateWeek());
        final InputStream reportInputStream = scheduleRequester.requestReport(scheduleRequest);
        try {
            HSSFSheet sheet = new HSSFWorkbook(reportInputStream).getSheetAt(0);
            final Multimap<Integer, ScheduleResponse> subjects = convertSheetToScheduleMap(sheet);

            if (scheduleIsNotAvailable(subjects)) {
                throw new ScheduleNotAvailableException("Расписание недоступно. Повторите запрос позже");
            }

            String monday = convertDayToTelegramResponse(1, subjects.get(1));
            String tuesday = convertDayToTelegramResponse(2, subjects.get(2));
            String wednesday = convertDayToTelegramResponse(3, subjects.get(3));
            String thursday = convertDayToTelegramResponse(4, subjects.get(4));
            String friday = convertDayToTelegramResponse(5, subjects.get(5));
            String saturday = convertDayToTelegramResponse(6, subjects.get(6));

            Schedule schedule = Schedule.builder()
                    .week(week)
                    .monday(monday)
                    .tuesday(tuesday)
                    .wednesday(wednesday)
                    .thursday(thursday)
                    .friday(friday)
                    .saturday(saturday)
                    .build();

            scheduleRepository.save(schedule);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean scheduleIsNotAvailable(Multimap<Integer, ScheduleResponse> subjects) {
        Collection<ScheduleResponse> scheduleResponses = subjects.values();
        return scheduleResponses.stream()
                .allMatch(scheduleResponse -> scheduleResponse.getRoom().isEmpty() &&
                        scheduleResponse.getSubjectAndTeacherName().isEmpty() &&
                        scheduleResponse.getTime().isEmpty());
    }

    private boolean scheduleIsNotSaved(Week week) {
        return scheduleRepository.findByWeek(week).isEmpty();
    }

    private Integer calculateWeek() {
        final LocalDate initialWeek = LocalDate.of(2020, 3, 16);
        return Period.between(initialWeek, LocalDate.now(ZoneId.systemDefault())).getDays() / 7;
    }
}
