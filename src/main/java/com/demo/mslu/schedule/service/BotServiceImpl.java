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

import static com.demo.mslu.schedule.model.constant.ButtonConstant.BACK_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.CURRENT_WEEK_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.GET_SCHEDULE_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.NEXT_WEEK_BUTTON;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BotServiceImpl implements BotService {

    private final ScheduleService scheduleService;

    @Override
    public Optional<Long> getChatId(Update update) {
        Message message = update.getMessage();
        Long chatId = nonNull(message) ? message.getChatId() : null;
        return Optional.of(chatId);
    }

    @Override
    public Optional<String> getIncomingMessage(Update update) {
        Message message = update.getMessage();
        String messageText = nonNull(message) ? message.getText() : null;
        return Optional.of(messageText);
    }

    @Override
    public SendMessage createOutgoingMessage(Long chatId, String incomingMessage) {
        switch (incomingMessage.toLowerCase()) {
            case GET_SCHEDULE_BUTTON, BACK_BUTTON -> {
                String message = "Выберите неделю";
                return new SendMessage(chatId, message);
            }
            case CURRENT_WEEK_BUTTON, NEXT_WEEK_BUTTON -> {
                String message = "Выберите день недели";
                return new SendMessage(chatId, message);
            }
            default -> {
                ScheduleRequest request = createScheduleRequest();
                String message = scheduleService.getNextDay(request);
                return new SendMessage(chatId, message);
            }
        }
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
            case CURRENT_WEEK_BUTTON, NEXT_WEEK_BUTTON -> {
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
