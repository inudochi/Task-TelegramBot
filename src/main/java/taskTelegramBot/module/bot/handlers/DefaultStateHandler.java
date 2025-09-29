package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import taskTelegramBot.module.bot.*;

/**
 * Обработчик состояния по умолчанию для неизвестных или неподдерживаемых состояний
 *
 * <p>Предоставляет обработку по умолчанию для ситуаций, когда состояние
 * пользователя неизвестно или не имеет специализированного обработчика.
 * Возвращает пользователя в главное меню с соответствующими сообщениями.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class DefaultStateHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика по умолчанию
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public DefaultStateHandler(BotContext botContext, BotDependencies dependencies,
                               StateManager stateManager, CallbackAnswerService callbackAnswerService,
                               BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает текстовые сообщения в неизвестном состоянии
     *
     * <p>Информирует пользователя о том, что состояние не поддерживает
     * текстовые сообщения и возвращает в главное меню</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param message полученное сообщение
     */
    @Override
    public void handleMessage(Long chatId, BotUserData userData, Message message) {
        getMessageSender().sendMessage(chatId, "Функция в разработке. Возврат в главное меню.");
        updateUserState(chatId, userData, BotState.MAIN_MENU);
        getMessageSender().sendMainMenu(chatId, getGameService().getAllGames());
    }

    /**
     * Обрабатывает callback-запросы в неизвестном состоянии
     *
     * <p>Информирует пользователя о том, что действие не поддерживается
     * и предлагает использовать кнопки меню для навигации</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param callbackQuery callback-запрос
     */
    @Override
    public void handleCallbackQuery(Long chatId, BotUserData userData, CallbackQuery callbackQuery) {
        answerCallbackQuery(callbackQuery.getId(), "Действие не поддерживается");
        getMessageSender().sendMessage(chatId, "Пожалуйста, используйте кнопки меню для навигации.");
    }
}