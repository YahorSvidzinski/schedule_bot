package com.demo.mslu.schedule.model;

import com.demo.mslu.schedule.service.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.demo.mslu.schedule.model.constant.BotConstant.BOT_NAME;

/**
 * @author Aliaksandr Miron
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScheduleBot extends TelegramLongPollingBot {

	@Value("${bot.token}")
	private String token;

	private final BotService botService;

	private ScheduleRequest scheduleRequest = createScheduleRequest();

	private Integer startWeek = scheduleRequest.getWeek();

	@Override
	public void onUpdateReceived(Update update) {
		Long chatId = botService.getChatId(update).orElseThrow();
		String incomingMessage = botService.getIncomingMessage(update).orElseThrow();
		ReplyKeyboardMarkup keyboard = botService.createKeyboard(incomingMessage);
		if (incomingMessage.equals("Следующая неделя") && scheduleRequest.getWeek().equals(startWeek)) {
			scheduleRequest.setWeek(scheduleRequest.getWeek() + 1);
		} else if (incomingMessage.equals("Текущая неделя")) {
			scheduleRequest.setWeek(startWeek);
		}

		SendMessage outgoingMessage = botService.createOutgoingMessage(scheduleRequest, chatId, incomingMessage);
		outgoingMessage.setReplyMarkup(keyboard);
		outgoingMessage.enableHtml(true);
		sendMessage(outgoingMessage);
	}

	@Override
	public String getBotUsername() {
		return BOT_NAME;
	}

	@Override
	public String getBotToken() {
		return token;
	}

	private void sendMessage(SendMessage message) {
		try {
			execute(message);
		} catch (TelegramApiException e) {
			String errorMessage = e.getMessage();
			log.error(errorMessage);
		}
	}

	private ScheduleRequest createScheduleRequest() {
		return ScheduleRequest.builder()
				.course(3)
				.faculty(7)
				.year(2019)
				.group(1360)
				.week(497)
				.build();
	}
}
