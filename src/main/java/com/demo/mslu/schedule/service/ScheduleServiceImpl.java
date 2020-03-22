package com.demo.mslu.schedule.service;

import com.demo.mslu.schedule.model.ScheduleRequest;
import com.demo.mslu.schedule.model.ScheduleResponse;
import com.demo.mslu.schedule.repository.ScheduleRepository;
import com.demo.mslu.schedule.util.MultimapCollector;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import static com.demo.mslu.schedule.model.constant.ButtonConstant.FRIDAY_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.MONDAY_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.SATURDAY_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.THURSDAY_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.TUESDAY_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.WEDNESDAY_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.MessageConstant.TODAY_IS_A_DAY_OFF_MESSAGE;
import static com.demo.mslu.schedule.model.constant.WeekDaysConstant.LocalizedFormat.LOCALIZED_FRIDAY;
import static com.demo.mslu.schedule.model.constant.WeekDaysConstant.LocalizedFormat.LOCALIZED_MONDAY;
import static com.demo.mslu.schedule.model.constant.WeekDaysConstant.LocalizedFormat.LOCALIZED_SATURDAY;
import static com.demo.mslu.schedule.model.constant.WeekDaysConstant.LocalizedFormat.LOCALIZED_THURSDAY;
import static com.demo.mslu.schedule.model.constant.WeekDaysConstant.LocalizedFormat.LOCALIZED_TUESDAY;
import static com.demo.mslu.schedule.model.constant.WeekDaysConstant.LocalizedFormat.LOCALIZED_WEDNESDAY;
import static com.demo.mslu.schedule.model.constant.WeekDaysConstant.NumericFormat.NUMBER_FRIDAY;
import static com.demo.mslu.schedule.model.constant.WeekDaysConstant.NumericFormat.NUMBER_MONDAY;
import static com.demo.mslu.schedule.model.constant.WeekDaysConstant.NumericFormat.NUMBER_SATURDAY;
import static com.demo.mslu.schedule.model.constant.WeekDaysConstant.NumericFormat.NUMBER_THURSDAY;
import static com.demo.mslu.schedule.model.constant.WeekDaysConstant.NumericFormat.NUMBER_TUESDAY;
import static com.demo.mslu.schedule.model.constant.WeekDaysConstant.NumericFormat.NUMBER_WEDNESDAY;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;
import static java.util.Objects.nonNull;
import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.Collectors.joining;
import static java.util.stream.StreamSupport.stream;

/**
 * @author Yahor Svidzinski
 */
@Service
@AllArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRequester scheduleRequester;
    private final ScheduleRepository scheduleRepository;

    @Override
    public String getDayOfWeek(@NotNull ScheduleRequest scheduleRequest, @NotNull DayOfWeek dayOfWeek) {
        if (SUNDAY.equals(dayOfWeek)) {
            return TODAY_IS_A_DAY_OFF_MESSAGE;
        }
        scheduleRequest.setWeek(scheduleRequest.getWeek() + calculateWeek());
        final String scheduleResponseForDay = getConvertedDay(scheduleRequest, dayOfWeek);
        if (nonNull(scheduleResponseForDay)) {
            return scheduleResponseForDay;
        }
        throw new IllegalStateException();
    }

    @Override
    public String getWeek(@NotNull ScheduleRequest scheduleRequest) {
        scheduleRequest.setWeek(scheduleRequest.getWeek() + calculateWeek());
        final InputStream reportInputStream = scheduleRequester.requestReport(scheduleRequest);
        try {
            HSSFSheet sheet = new HSSFWorkbook(reportInputStream).getSheetAt(0);
            final Multimap<Integer, ScheduleResponse> objects = convertSheetToScheduleMap(sheet);
            return convertDayToTelegramResponse(MONDAY, objects.get(1)) +
                    convertDayToTelegramResponse(TUESDAY, objects.get(2)) +
                    convertDayToTelegramResponse(WEDNESDAY, objects.get(3)) +
                    convertDayToTelegramResponse(THURSDAY, objects.get(4)) +
                    convertDayToTelegramResponse(FRIDAY, objects.get(5)) +
                    convertDayToTelegramResponse(SATURDAY, objects.get(6));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer calculateWeek() {
        final LocalDate initialWeek = LocalDate.of(2020, 3, 16);
        return Period.between(initialWeek, LocalDate.now(ZoneId.systemDefault())).getDays() / 7;
    }

    private String getConvertedDay(@NotNull ScheduleRequest scheduleRequest, DayOfWeek dayOfWeek) {
        final InputStream reportInputStream = scheduleRequester.requestReport(scheduleRequest);
        try {
            HSSFSheet sheet = new HSSFWorkbook(reportInputStream).getSheetAt(0);
            final Multimap<Integer, ScheduleResponse> objects = convertSheetToScheduleMap(sheet);
            final Collection<ScheduleResponse> scheduleResponseForDay = objects.get(dayOfWeek.getValue());
            return convertDayToTelegramResponse(dayOfWeek, scheduleResponseForDay);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertDayToTelegramResponse(@NotNull DayOfWeek day, @NotNull Collection<ScheduleResponse> scheduleResponseForDay) {
        StringBuilder localizedDay = new StringBuilder("<b>" + convertDayOfWeekLocalizedFormat(day) + "</b>" + "\n");
        for (ScheduleResponse subject : scheduleResponseForDay) {
            if (subject.getTime().isEmpty() && subject.getSubjectAndTeacherName().isEmpty() && subject.getRoom().isEmpty()) {
                return localizedDay
                        .append("Выходной")
                        .append("\n")
                        .append("\n")
                        .toString();
            }
            localizedDay
                    .append("<i>")
                    .append(subject.getTime())
                    .append("</i>")
                    .append("\n")
                    .append(subject.getSubjectAndTeacherName())
                    .append("\n")
                    .append(subject.getRoom())
                    .append("\n")
                    .append("\n");
        }
        return localizedDay.toString();
    }

    private Multimap<Integer, ScheduleResponse> convertSheetToScheduleMap(@NotNull Sheet sheet) {
        final Stream<Row> rowStream = convertToStream(sheet.rowIterator()).skip(2);
        return rowStream
                .filter(row -> !isRowEmpty(row))
                .collect(MultimapCollector.toMultimap(
                        row -> {
                            if (row.getCell(0).getStringCellValue().isEmpty()
                                    && !row.getCell(1).getStringCellValue().isEmpty()) {
                                return convertDayOfWeekToNumber(sheet.getRow(findMergedCellRow(sheet, row.getRowNum())).getCell(0));
                            } else
                                return convertDayOfWeekToNumber(row.getCell(0));
                        },
                        row -> new ScheduleResponse(row.getCell(1).getStringCellValue(),
                                row.getCell(2).getStringCellValue(),
                                row.getCell(3).getStringCellValue())));
    }

    private Integer findMergedCellRow(@NotNull Sheet sheet, @NotNull Integer rowNumber) {
        while (sheet.getRow(rowNumber).getCell(0).getStringCellValue().isEmpty()) {
            rowNumber -= 1;
        }
        return rowNumber;
    }

    private Integer convertDayOfWeekToNumber(@NotNull Cell cell) {
        switch (cell.getStringCellValue()) {
            case MONDAY_BUTTON_VALUE -> {
                return NUMBER_MONDAY;
            }
            case TUESDAY_BUTTON_VALUE -> {
                return NUMBER_TUESDAY;
            }
            case WEDNESDAY_BUTTON_VALUE -> {
                return NUMBER_WEDNESDAY;
            }
            case THURSDAY_BUTTON_VALUE -> {
                return NUMBER_THURSDAY;
            }
            case FRIDAY_BUTTON_VALUE -> {
                return NUMBER_FRIDAY;
            }
            case SATURDAY_BUTTON_VALUE -> {
                return NUMBER_SATURDAY;
            }
            default -> {
                return null;
            }
        }
    }

    private String convertDayOfWeekLocalizedFormat(@NotNull DayOfWeek day) {
        switch (day) {
            case MONDAY -> {
                return LOCALIZED_MONDAY;
            }
            case TUESDAY -> {
                return LOCALIZED_TUESDAY;
            }
            case WEDNESDAY -> {
                return LOCALIZED_WEDNESDAY;
            }
            case THURSDAY -> {
                return LOCALIZED_THURSDAY;
            }
            case FRIDAY -> {
                return LOCALIZED_FRIDAY;
            }
            case SATURDAY -> {
                return LOCALIZED_SATURDAY;
            }
            default -> {
                return null;
            }
        }
    }

    private boolean isRowEmpty(@NotNull Row row) {
        final Stream<Cell> cellStream = convertToStream(row.cellIterator());
        return cellStream
                .map(Cell::getStringCellValue)
                .collect(joining())
                .isEmpty();
    }

    private <T> Stream<T> convertToStream(@NotNull Iterator<T> iterator) {
        return stream(spliteratorUnknownSize(iterator, ORDERED), false);
    }
}
