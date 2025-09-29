package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import taskTelegramBot.module.bot.*;

/**
 * Обработчик состояния ввода минимального количества игроков
 *
 * <p>Управляет третьим шагом процесса добавления игры - получением
 * и валидацией минимального количества игроков.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class AddingGameMinPlayersHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика ввода минимального количества игроков
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public AddingGameMinPlayersHandler(BotContext botContext, BotDependencies dependencies,
                                       StateManager stateManager, CallbackAnswerService callbackAnswerService,
                                       BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает ввод минимального количества игроков
     *
     * <p>Проверяет корректность введенного числа (1-16) и сохраняет
     * его во временные данные пользователя. При успешной валидации
     * переходит к вводу максимального количества игроков.</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param message сообщение с количеством игроков
     */
    @Override
    public void handleMessage(Long chatId, BotUserData userData, Message message) {
        Integer minPlayers = validateAndParseInteger(message.getText(), chatId);
        if (minPlayers == null) return;

        if (!validateMinPlayers(minPlayers, chatId)) return;

        userData.setTempData("newGameMinPlayers", minPlayers);
        updateUserState(chatId, userData, BotState.ADDING_GAME_MAX_PLAYERS);
        getMessageSender().sendMessage(chatId, "Введите максимальное количество игроков (1-16):");
    }
}