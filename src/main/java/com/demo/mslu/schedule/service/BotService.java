package com.demo.mslu.schedule.service;

import com.demo.mslu.schedule.model.ScheduleRequest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * @author Aliaksandr Miron
 */
public interface BotService {

	Optional<Long> getChatId(Update update);

	Optional<String> getIncomingMessage(Update update);

	SendMessage createOutgoingMessage(@NotNull ScheduleRequest scheduleRequest, Long chatId, String incomingMessage);

	ReplyKeyboardMarkup createKeyboard(String incomingMessage);
}
