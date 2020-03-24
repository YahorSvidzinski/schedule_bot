package com.demo.mslu.schedule.service;

import com.demo.mslu.schedule.exception.ScheduleNotAvailableException;
import com.demo.mslu.schedule.model.ScheduleRequest;
import com.demo.mslu.schedule.model.constant.Week;
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

import static com.demo.mslu.schedule.model.constant.ButtonConstant.ALL_WEEK_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.BACK_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.CURRENT_WEEK_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.FRIDAY_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.GET_SCHEDULE_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.MONDAY_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.NEXT_WEEK_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.SATURDAY_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.START_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.THURSDAY_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.TUESDAY_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.WEDNESDAY_BUTTON;

/**
 * @author Aliaksandr Miron
 */
@Slf4j
@Service
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
    public SendMessage createOutgoingMessage(@NotNull ScheduleRequest scheduleRequest, Long chatId, String incomingMessage, Week week) throws ScheduleNotAvailableException {
        switch (incomingMessage) {
            case MONDAY_BUTTON:
                String daySchedule = scheduleService.getForDay(scheduleRequest, 1, week);
                return new SendMessage(chatId, daySchedule);

            case TUESDAY_BUTTON:
                daySchedule = scheduleService.getForDay(scheduleRequest, 2, week);
                return new SendMessage(chatId, daySchedule);

            case WEDNESDAY_BUTTON:
                daySchedule = scheduleService.getForDay(scheduleRequest, 3, week);
                return new SendMessage(chatId, daySchedule);

            case THURSDAY_BUTTON:
                daySchedule = scheduleService.getForDay(scheduleRequest, 4, week);
                return new SendMessage(chatId, daySchedule);

            case FRIDAY_BUTTON:
                daySchedule = scheduleService.getForDay(scheduleRequest, 5, week);
                return new SendMessage(chatId, daySchedule);

            case SATURDAY_BUTTON:
                daySchedule = scheduleService.getForDay(scheduleRequest, 6, week);
                return new SendMessage(chatId, daySchedule);

            case ALL_WEEK_BUTTON:
                String weekSchedule = scheduleService.getForWeek(scheduleRequest, week);
                return new SendMessage(chatId, weekSchedule);

            case GET_SCHEDULE_BUTTON, BACK_BUTTON, START_BUTTON:
                return new SendMessage(chatId, "Выберите неделю");

            case CURRENT_WEEK_BUTTON, NEXT_WEEK_BUTTON:
                return new SendMessage(chatId, "Выберите день недели");

            default:
                return new SendMessage(chatId, "Неверная команда");
        }
    }

    @Override
    public ReplyKeyboardMarkup createKeyboardMarkup(String incomingMessage) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> buttons = createButtons(incomingMessage);
        keyboardMarkup.setKeyboard(buttons);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    private List<KeyboardRow> createButtons(String incomingMessage) {
        List<KeyboardRow> keyboard = new ArrayList<>();

        switch (incomingMessage) {
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
