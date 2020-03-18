package com.demo.mslu.schedule.service;

import com.demo.mslu.schedule.model.ScheduleRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.demo.mslu.schedule.model.constant.ButtonConstant.*;
import static java.time.DayOfWeek.*;

/**
 * @author Aliaksandr Miron
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BotServiceImpl implements BotService {

	private final ScheduleService scheduleService;

	@Override
	public Optional<Long> getChatId(Update update) {
		Message message = update.getMessage();
		log.info(message.getChat().getFirstName() + " " + message.getChat().getLastName());
		Long chatId = message.getChatId();
		return Optional.of(chatId);
	}

	@Override
	public Optional<String> getIncomingMessage(Update update) {
		Message message = update.getMessage();
		String messageText = message.getText();
		return Optional.of(messageText);
	}

	@Override
	public SendMessage createOutgoingMessage(@NotNull ScheduleRequest scheduleRequest, Long chatId, String incomingMessage) {
		return switch (incomingMessage) {
			case "ПН" -> new SendMessage(chatId, scheduleService.getDayOfWeek(scheduleRequest, MONDAY));
			case "ВТ" -> new SendMessage(chatId, scheduleService.getDayOfWeek(scheduleRequest, TUESDAY));
			case "СР" -> new SendMessage(chatId, scheduleService.getDayOfWeek(scheduleRequest, WEDNESDAY));
			case "ЧТ" -> new SendMessage(chatId, scheduleService.getDayOfWeek(scheduleRequest, THURSDAY));
			case "ПТ" -> new SendMessage(chatId, scheduleService.getDayOfWeek(scheduleRequest, FRIDAY));
			case "СБ" -> new SendMessage(chatId, scheduleService.getDayOfWeek(scheduleRequest, SATURDAY));
			case "Вся неделя" -> new SendMessage(chatId, scheduleService.getWeek(scheduleRequest));
			case "Получить расписание", "Назад" -> new SendMessage(chatId, "Выберите неделю");
			case "Текущая неделя", "Следующая неделя" -> new SendMessage(chatId, "Выберите день недели");
			default -> new SendMessage(chatId, "Неверная команда");
		};
	}

	@Override
	public ReplyKeyboardMarkup createKeyboard(String incomingMessage) {
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboardRow = createButtons(incomingMessage);
		keyboardMarkup.setKeyboard(keyboardRow);
		keyboardMarkup.setResizeKeyboard(true);
		return keyboardMarkup;
	}

	private List<KeyboardRow> createButtons(String incomingMessage) {
		List<KeyboardRow> keyboard = new ArrayList<>();

		switch (incomingMessage.toLowerCase()) {
			case GET_SCHEDULE_BUTTON, BACK_BUTTON -> {
				KeyboardRow currentWeekButton = new KeyboardRow();
				KeyboardRow nextWeekButton = new KeyboardRow();
				currentWeekButton.add("Текущая неделя");
				nextWeekButton.add("Следующая неделя");
				keyboard.addAll(List.of(currentWeekButton, nextWeekButton));
				return keyboard;
			}
			case CURRENT_WEEK_BUTTON, NEXT_WEEK_BUTTON, MONDAY_BUTTON, TUESDAY_BUTTON, WEDNESDAY_BUTTON,
					THURSDAY_BUTTON, FRIDAY_BUTTON, SATURDAY_BUTTON, ALL_WEEK_BUTTON -> {

				KeyboardRow allWeekButton = new KeyboardRow();
				KeyboardRow daysButtons = new KeyboardRow();
				KeyboardRow backButton = new KeyboardRow();
				allWeekButton.add("Вся неделя");
				daysButtons.addAll(List.of("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ"));
				backButton.add("Назад");
				keyboard.addAll(List.of(allWeekButton, daysButtons, backButton));
				return keyboard;
			}
			default -> {
				KeyboardRow getScheduleButton = new KeyboardRow();
				getScheduleButton.add("Получить расписание");
				keyboard.add(getScheduleButton);
				return keyboard;
			}
		}
	}
}
