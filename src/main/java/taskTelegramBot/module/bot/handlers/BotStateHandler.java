package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.BotUserData;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Интерфейс обработчиков состояний Telegram бота
 *
 * <p>Определяет контракт для всех обработчиков, которые управляют
 * различными состояниями пользователя в процессе взаимодействия с ботом.
 * Каждое состояние бота имеет свой собственный обработчик.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public interface BotStateHandler {

    /**
     * Обрабатывает текстовые сообщения от пользователя
     *
     * @param chatId идентификатор чата пользователя
     * @param userData данные пользователя и текущее состояние
     * @param message полученное текстовое сообщение
     */
    void handleMessage(Long chatId, BotUserData userData, Message message);

    /**
     * Обрабатывает callback-запросы от inline-кнопок
     *
     * @param chatId идентификатор чата пользователя
     * @param userData данные пользователя и текущее состояние
     * @param callbackQuery callback-запрос от нажатой кнопки
     */
    void handleCallbackQuery(Long chatId, BotUserData userData, CallbackQuery callbackQuery);
}