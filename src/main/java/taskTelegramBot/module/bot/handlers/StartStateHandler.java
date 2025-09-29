package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import taskTelegramBot.module.bot.*;

/**
 * Обработчик начального состояния бота
 *
 * <p>Обрабатывает первое взаимодействие пользователя с ботом.
 * Отправляет приветственное сообщение и предлагает выбрать источник данных.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class StartStateHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика начального состояния
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public StartStateHandler(BotContext botContext, BotDependencies dependencies,
                             StateManager stateManager, CallbackAnswerService callbackAnswerService,
                             BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает текстовые сообщения в начальном состоянии
     *
     * <p>Отправляет приветственное сообщение и переводит пользователя
     * в состояние выбора источника данных</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param message полученное сообщение
     */
    @Override
    public void handleMessage(Long chatId, BotUserData userData, Message message) {
        getMessageSender().sendWelcomeMessage(chatId);
        updateUserState(chatId, userData, BotState.CHOOSING_SOURCE);
    }

    /**
     * Обрабатывает callback-запросы в начальном состоянии
     *
     * <p>Напоминает пользователю о необходимости выбора источника данных
     * и повторно отправляет приветственное сообщение</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param callbackQuery callback-запрос
     */
    @Override
    public void handleCallbackQuery(Long chatId, BotUserData userData, CallbackQuery callbackQuery) {
        answerCallbackQuery(callbackQuery.getId(), "Пожалуйста, выберите источник данных");
        getMessageSender().sendWelcomeMessage(chatId);
    }
}