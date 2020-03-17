package com.demo.mslu.schedule.service;

import com.demo.mslu.schedule.model.ScheduleRequest;
import lombok.AllArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;

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
			System.out.println(sheet.getRow(1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Success";
	}
}
