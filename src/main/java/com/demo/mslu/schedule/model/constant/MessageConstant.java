package com.demo.mslu.schedule.model.constant;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author Timofei Shostko
 */
@NoArgsConstructor(access = PRIVATE)
public class MessageConstant {

    public static final String CHOOSE_WEEK_MESSAGE = "Выберите неделю";
    public static final String CHOOSE_WEEK_DAY_MESSAGE = "Выберите день недели";
    public static final String WRONG_COMMAND_MESSAGE = "Неверная команда";
    public static final String TODAY_IS_A_DAY_OFF_MESSAGE = "Cегодня выходной";
}
