package com.demo.mslu.schedule.model;

import com.demo.mslu.schedule.model.constant.Week;
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

import static com.demo.mslu.schedule.model.constant.ButtonConstant.CURRENT_WEEK_BUTTON;
import static com.demo.mslu.schedule.model.constant.ButtonConstant.NEXT_WEEK_BUTTON;
import static com.demo.mslu.schedule.model.constant.Week.CURRENT_WEEK;
import static com.demo.mslu.schedule.model.constant.Week.NEXT_WEEK;

/**
 * @author Aliaksandr Miron
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ScheduleBot extends TelegramLongPollingBot {

    @Value("${bot.token}")
    private String token;

    @Value("${bot.name}")
    private String name;

    private final BotService botService;

    private ScheduleRequest scheduleRequest = createScheduleRequest();

    private Integer startWeek = scheduleRequest.getWeek();

    private Week week = CURRENT_WEEK;

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = botService.getChatId(update).orElseThrow();
        String incomingMessage = botService.getIncomingMessage(update).orElseThrow();
        ReplyKeyboardMarkup keyboard = botService.createKeyboardMarkup(incomingMessage);

        synchronizeWeek(incomingMessage);

        SendMessage outgoingMessage = botService.createOutgoingMessage(scheduleRequest, chatId, incomingMessage, week);
        outgoingMessage.setReplyMarkup(keyboard);
        outgoingMessage.enableHtml(true);
        sendMessage(outgoingMessage);
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    private void synchronizeWeek(String incomingMessage) {
        Integer weekNumber = scheduleRequest.getWeek();
        if (NEXT_WEEK_BUTTON.equals(incomingMessage) && weekNumber.equals(startWeek)) {
            scheduleRequest.setWeek(weekNumber + 1);
            this.week = NEXT_WEEK;
        } else if (CURRENT_WEEK_BUTTON.equals(incomingMessage)) {
            scheduleRequest.setWeek(startWeek);
            this.week = CURRENT_WEEK;
        }
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
