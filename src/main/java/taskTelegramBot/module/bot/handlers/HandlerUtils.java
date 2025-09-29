package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.BotState;
import taskTelegramBot.module.bot.BotUserData;
import taskTelegramBot.module.bot.MessageSender;
import taskTelegramBot.module.service.GameService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Утилитарный класс для общей логики обработчиков
 *
 * <p>Содержит общие методы и константы, используемые различными обработчиками.
 * Устраняет дублирование кода и обеспечивает единообразие обработки
 * стандартных сценариев взаимодействия.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class HandlerUtils {

    /**
     * Приватный конструктор для предотвращения создания экземпляров
     */
    private HandlerUtils() {
        // Utility class
    }

    /**
     * Стандартная обработка отмены для всех обработчиков
     *
     * <p>Сбрасывает временные данные пользователя и возвращает в главное меню</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param messageSender отправитель сообщений
     * @param gameService сервис игр
     */
    public static void handleCancel(Long chatId, BotUserData userData,
                                    MessageSender messageSender, GameService gameService) {
        userData.clearTempData();
        userData.setSelectedGameId(null);
        userData.setCurrentGame(null);
        userData.setState(BotState.MAIN_MENU);

        messageSender.sendMainMenu(chatId, gameService.getAllGames());
    }

    /**
     * Создание inline-кнопки с указанным текстом и callback-данными
     *
     * @param text текст кнопки
     * @param callbackData данные для callback-запроса
     * @return сконфигурированная inline-кнопка
     */
    public static InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    /**
     * Валидация числового ввода с отправкой сообщения об ошибке
     *
     * @param input входная строка
     * @param chatId идентификатор чата
     * @param messageSender отправитель сообщений
     * @return преобразованное число или null если преобразование не удалось
     */
    public static Integer validateAndParseInteger(String input, Long chatId,
                                                  MessageSender messageSender) {
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            messageSender.sendMessage(chatId, "Введите корректное число");
            return null;
        }
    }

    /**
     * Проверка минимального количества игроков с отправкой сообщения об ошибке
     *
     * @param minPlayers минимальное количество игроков
     * @param chatId идентификатор чата
     * @param messageSender отправитель сообщений
     * @return true если количество корректно, false в противном случае
     */
    public static boolean validateMinPlayers(int minPlayers, Long chatId,
                                             MessageSender messageSender) {
        if (minPlayers < 1 || minPlayers > 16) {
            messageSender.sendMessage(chatId, "Минимальное количество игроков должно быть от 1 до 16");
            return false;
        }
        return true;
    }

    /**
     * Проверка максимального количества игроков с отправкой сообщения об ошибке
     *
     * @param minPlayers минимальное количество игроков
     * @param maxPlayers максимальное количество игроков
     * @param chatId идентификатор чата
     * @param messageSender отправитель сообщений
     * @return true если количество корректно, false в противном случае
     */
    public static boolean validateMaxPlayers(int minPlayers, int maxPlayers, Long chatId,
                                             MessageSender messageSender) {
        if (maxPlayers < 1 || maxPlayers > 16) {
            messageSender.sendMessage(chatId, "Максимальное количество игроков должно быть от 1 до 16");
            return false;
        }
        if (maxPlayers < minPlayers) {
            messageSender.sendMessage(chatId, "Максимальное количество игроков должно быть больше или равно минимальному");
            return false;
        }
        return true;
    }

    /**
     * Обработка кнопки "Назад" для callback-запросов
     *
     * @param callbackData данные callback-запроса
     * @param callbackQueryId идентификатор callback-запроса
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param messageSender отправитель сообщений
     * @param gameService сервис игр
     * @return true если кнопка "Назад" была обработана, false в противном случае
     */
    public static boolean handleBackButton(String callbackData, String callbackQueryId,
                                           Long chatId, BotUserData userData,
                                           MessageSender messageSender, GameService gameService) {
        if ("back_to_menu".equals(callbackData)) {
            messageSender.answerCallbackQuery(callbackQueryId, "Возврат в главное меню");
            handleCancel(chatId, userData, messageSender, gameService);
            return true;
        }
        return false;
    }

    /**
     * Обработка кнопки "Отмена" для callback-запросов
     *
     * @param callbackData данные callback-запроса
     * @param callbackQueryId идентификатор callback-запроса
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param messageSender отправитель сообщений
     * @param gameService сервис игр
     * @param cancelMessage сообщение об отмене операции
     * @return true если кнопка "Отмена" была обработана, false в противном случае
     */
    public static boolean handleCancelButton(String callbackData, String callbackQueryId,
                                             Long chatId, BotUserData userData,
                                             MessageSender messageSender, GameService gameService,
                                             String cancelMessage) {
        if ("cancel".equals(callbackData)) {
            messageSender.answerCallbackQuery(callbackQueryId, cancelMessage);
            handleCancel(chatId, userData, messageSender, gameService);
            return true;
        }
        return false;
    }
}