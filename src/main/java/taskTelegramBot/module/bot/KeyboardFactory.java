package taskTelegramBot.module.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import taskTelegramBot.module.dao.Game;
import taskTelegramBot.module.service.ValidationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Фабрика для создания клавиатур Telegram бота
 *
 * <p>Создает различные типы inline-клавиатур для взаимодействия
 * с пользователем: выбора источников данных, жанров, списков игр
 * и подтверждения операций.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class KeyboardFactory {

    /**
     * Создает клавиатуру для выбора источника данных
     *
     * @return inline-клавиатура с кнопками выбора JSON и PostgreSQL
     */
    public InlineKeyboardMarkup createSourceSelectionKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton("JSON", "source_json"));
        row1.add(createButton("PostgreSQL", "source_postgres"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton("Назад", "back_to_menu"));

        rows.add(row1);
        rows.add(row2);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    /**
     * Создает клавиатуру для выбора жанра игры
     *
     * @return inline-клавиатура с кнопками допустимых жанров
     */
    public InlineKeyboardMarkup createGenreSelectionKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<String> validGenres = ValidationService.VALID_GENRES;

        // Группируем жанры по 2 в строке для компактности
        for (int i = 0; i < validGenres.size(); i += 2) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(createButton(validGenres.get(i), "genre_" + validGenres.get(i)));

            if (i + 1 < validGenres.size()) {
                row.add(createButton(validGenres.get(i + 1), "genre_" + validGenres.get(i + 1)));
            }

            rows.add(row);
        }

        List<InlineKeyboardButton> cancelRow = new ArrayList<>();
        cancelRow.add(createButton("Назад", "back_to_menu"));
        rows.add(cancelRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    /**
     * Создает клавиатуру со списком игр для различных операций
     *
     * @param games список игр для отображения
     * @param actionPrefix префикс для callback-данных (edit_game, delete_game, plan_session)
     * @return inline-клавиатура с кнопками игр
     */
    public InlineKeyboardMarkup createGamesListKeyboard(List<Game> games, String actionPrefix) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (games != null && !games.isEmpty()) {
            for (int i = 0; i < games.size(); i++) {
                Game game = games.get(i);
                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(createButton((i+1) + ". " + game.getTitle(), actionPrefix + "_" + game.getId()));
                rows.add(row);
            }
        } else {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(createButton("Нет игр", "no_games"));
            rows.add(row);
        }

        List<InlineKeyboardButton> cancelRow = new ArrayList<>();
        cancelRow.add(createButton("Назад", "back_to_menu"));
        rows.add(cancelRow);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    /**
     * Создает клавиатуру для подтверждения операций
     *
     * @return inline-клавиатура с кнопками "Да" и "Нет"
     */
    public InlineKeyboardMarkup createConfirmationKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton("Да", "confirm_delete"));
        row1.add(createButton("Нет", "cancel_delete"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton("Назад", "back_to_menu"));

        rows.add(row1);
        rows.add(row2);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    /**
     * Создает inline-кнопку с указанным текстом и callback-данными
     *
     * @param text отображаемый текст кнопки
     * @param callbackData данные для callback-запроса
     * @return сконфигурированная inline-кнопка
     */
    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }
}