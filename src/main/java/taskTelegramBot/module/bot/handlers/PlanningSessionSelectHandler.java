package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.dao.Game;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Обработчик состояния выбора игры для планирования сессии
 *
 * <p>Управляет первым шагом процесса планирования игровой сессии -
 * выбором конкретной игры из списка.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class PlanningSessionSelectHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика выбора игры для планирования сессии
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public PlanningSessionSelectHandler(BotContext botContext, BotDependencies dependencies,
                                        StateManager stateManager, CallbackAnswerService callbackAnswerService,
                                        BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает выбор игры для планирования сессии через callback-запрос
     *
     * <p>Извлекает идентификатор выбранной игры, проверяет ее существование
     * и переводит в состояние ввода даты сессии.</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param callbackQuery callback-запрос с идентификатором выбранной игры
     */
    @Override
    public void handleCallbackQuery(Long chatId, BotUserData userData, CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        String callbackId = callbackQuery.getId();

        if (handleBackButton(data, callbackId, chatId, userData)) return;
        if (handleCancelButton(data, callbackId, chatId, userData, "Планирование отменено")) return;

        try {
            int gameId = Integer.parseInt(data.replace("plan_session_", ""));
            Game game = getGameService().getGameById(gameId);

            if (game == null) {
                getMessageSender().sendMessage(chatId, "Игра не найдена");
                return;
            }

            userData.setSelectedGameId(gameId);
            updateUserState(chatId, userData, BotState.PLANNING_SESSION_DATE);
            answerCallbackQuery(callbackId, "Выбрана игра: " + game.getTitle());

            getMessageSender().sendMessage(chatId,
                    "Введите дату сессии в формате ДД.ММ.ГГГГ (например, 25.12.2024):");

        } catch (NumberFormatException e) {
            getMessageSender().sendMessage(chatId, "Ошибка выбора игры");
        } catch (Exception e) {
            answerCallbackQuery(callbackId, "Ошибка");
            getMessageSender().sendMessage(chatId, "Ошибка: " + e.getMessage());
        }
    }
}