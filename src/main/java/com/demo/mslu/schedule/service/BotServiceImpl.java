package com.demo.mslu.schedule.service;

import com.demo.mslu.schedule.model.ScheduleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BotServiceImpl implements BotService {

    private final ScheduleService scheduleService;

    @Override
    public Optional<Long> getChatId(Update update) {
        Message message = update.getMessage();
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
    public SendMessage createOutgoingMessage(Long chatId, String incomingMessage) {
        ScheduleRequest request = createScheduleRequest();
        String message = scheduleService.getNextDay(request);
        return new SendMessage(chatId, message);
    }

    @Override
    public ReplyKeyboardMarkup createKeyBoard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add("Get next day");
        List<KeyboardRow> keyboard = new ArrayList<>(List.of(row));
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
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
