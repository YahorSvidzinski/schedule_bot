package com.demo.mslu.schedule.service;

import com.demo.mslu.schedule.model.ScheduleRequest;
import com.demo.mslu.schedule.model.ScheduleResponse;
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
import java.util.Iterator;
import java.util.stream.Stream;

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

	private ScheduleRequester scheduleRequester;

	@Override
	public String getNextDay(@NotNull ScheduleRequest scheduleRequest) {
		final InputStream reportInputStream = scheduleRequester.requestReport(scheduleRequest);
		try {
			HSSFSheet sheet = new HSSFWorkbook(reportInputStream).getSheetAt(0);
			final Multimap<Integer, ScheduleResponse> objects = convertSheetToScheduleMap(sheet);
			final String value = sheet.getRow(3).getCell(0).getStringCellValue();
			System.out.println(objects.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Success";
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
			case "ПН" -> {
				return 1;
			}
			case "ВТ" -> {
				return 2;
			}
			case "СР" -> {
				return 3;
			}
			case "ЧТ" -> {
				return 4;
			}
			case "ПТ" -> {
				return 5;
			}
			case "СБ" -> {
				return 6;
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
