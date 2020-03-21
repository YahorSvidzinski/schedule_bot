package com.demo.mslu.schedule.util;

import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author Timofei Shostko
 */
@NoArgsConstructor(access = PRIVATE)
public class KeyboardUtils {

    public static KeyboardRow newKeyboardRow(String buttonName) {
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add(buttonName);
        return keyboardButtons;
    }

    public static KeyboardRow newKeyboardRow(List<String> buttonNames) {
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.addAll(buttonNames);
        return keyboardButtons;
    }
}
