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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.demo.mslu.schedule.model.constant.ButtonConstant.ALL_WEEK_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.BACK_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.CURRENT_WEEK_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.FRIDAY_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.GET_SCHEDULE_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.MONDAY_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.NEXT_WEEK_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.SATURDAY_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.THURSDAY_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.TUESDAY_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.WEDNESDAY_BUTTON_VALUE;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.getAllStudyWeekDays;
import static com.demo.mslu.schedule.model.constant.MessageConstant.CHOOSE_WEEK_DAY_MESSAGE;
import static com.demo.mslu.schedule.model.constant.MessageConstant.CHOOSE_WEEK_MESSAGE;
import static com.demo.mslu.schedule.model.constant.MessageConstant.WRONG_COMMAND_MESSAGE;
import static com.demo.mslu.schedule.util.KeyboardUtils.newKeyboardRow;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;

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
    public SendMessage createOutgoingMessage(@NotNull ScheduleRequest scheduleRequest, Long chatId, String buttonValue) {
        return switch (buttonValue) {
            case MONDAY_BUTTON_VALUE -> new SendMessage(chatId, scheduleService.getDayOfWeek(scheduleRequest, MONDAY));
            case TUESDAY_BUTTON_VALUE -> new SendMessage(chatId, scheduleService.getDayOfWeek(scheduleRequest, TUESDAY));
            case WEDNESDAY_BUTTON_VALUE -> new SendMessage(chatId, scheduleService.getDayOfWeek(scheduleRequest, WEDNESDAY));
            case THURSDAY_BUTTON_VALUE -> new SendMessage(chatId, scheduleService.getDayOfWeek(scheduleRequest, THURSDAY));
            case FRIDAY_BUTTON_VALUE -> new SendMessage(chatId, scheduleService.getDayOfWeek(scheduleRequest, FRIDAY));
            case SATURDAY_BUTTON_VALUE -> new SendMessage(chatId, scheduleService.getDayOfWeek(scheduleRequest, SATURDAY));
            case ALL_WEEK_BUTTON_VALUE -> new SendMessage(chatId, scheduleService.getWeek(scheduleRequest));
            case GET_SCHEDULE_BUTTON_VALUE,
                    BACK_BUTTON_VALUE -> new SendMessage(chatId, CHOOSE_WEEK_MESSAGE);
            case CURRENT_WEEK_BUTTON_VALUE,
                    NEXT_WEEK_BUTTON_VALUE -> new SendMessage(chatId, CHOOSE_WEEK_DAY_MESSAGE);
            default -> new SendMessage(chatId, WRONG_COMMAND_MESSAGE);
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

    private List<KeyboardRow> createButtons(String buttonValue) {
        List<KeyboardRow> keyboard = new ArrayList<>();

        switch (buttonValue) {
            case GET_SCHEDULE_BUTTON_VALUE,
                    BACK_BUTTON_VALUE -> {
                KeyboardRow currentWeekButton = newKeyboardRow(CURRENT_WEEK_BUTTON_VALUE);
                KeyboardRow nextWeekButton = newKeyboardRow(NEXT_WEEK_BUTTON_VALUE);
                keyboard.addAll(List.of(currentWeekButton, nextWeekButton));
                return keyboard;
            }
            case CURRENT_WEEK_BUTTON_VALUE,
                    NEXT_WEEK_BUTTON_VALUE,
                    MONDAY_BUTTON_VALUE,
                    TUESDAY_BUTTON_VALUE,
                    WEDNESDAY_BUTTON_VALUE,
                    THURSDAY_BUTTON_VALUE,
                    FRIDAY_BUTTON_VALUE,
                    SATURDAY_BUTTON_VALUE,
                    ALL_WEEK_BUTTON_VALUE -> {
                KeyboardRow allWeekButton = newKeyboardRow(ALL_WEEK_BUTTON_VALUE);
                KeyboardRow daysButtons = newKeyboardRow(getAllStudyWeekDays());
                KeyboardRow backButton = newKeyboardRow(BACK_BUTTON_VALUE);
                keyboard.addAll(List.of(allWeekButton, daysButtons, backButton));
                return keyboard;
            }
            default -> {
                KeyboardRow getScheduleButton = newKeyboardRow(GET_SCHEDULE_BUTTON_VALUE);
                keyboard.add(getScheduleButton);
                return keyboard;
            }
        }
    }
}
