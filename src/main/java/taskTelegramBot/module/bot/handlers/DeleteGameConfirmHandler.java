package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.dao.Game;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Обработчик состояния подтверждения удаления игры
 *
 * <p>Управляет заключительным шагом процесса удаления игры -
 * подтверждением операции и фактическим удалением игры из системы.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class DeleteGameConfirmHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика подтверждения удаления
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public DeleteGameConfirmHandler(BotContext botContext, BotDependencies dependencies,
                                    StateManager stateManager, CallbackAnswerService callbackAnswerService,
                                    BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает подтверждение или отмену удаления игры
     *
     * <p>Выполняет фактическое удаление игры при подтверждении операции
     * или отменяет удаление. В любом случае возвращает пользователя
     * в главное меню с обновленным списком игр.</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param callbackQuery callback-запрос с решением пользователя
     */
    @Override
    public void handleCallbackQuery(Long chatId, BotUserData userData, CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Integer gameId = userData.getSelectedGameId();

        if (gameId == null) {
            getMessageSender().sendMessage(chatId, "Игра не выбрана");
            updateUserState(chatId, userData, BotState.MAIN_MENU);
            return;
        }

        try {
            if (data.equals("confirm_delete")) {
                Game game = getGameService().getGameById(gameId);
                if (game != null) {
                    getGameService().deleteGame(gameId);
                    getMessageSender().sendMessage(chatId, "Игра '" + game.getTitle() + "' успешно удалена!");
                } else {
                    getMessageSender().sendMessage(chatId, "Игра не найдена");
                }
            } else {
                getMessageSender().sendMessage(chatId, "Удаление отменено");
            }

        } catch (Exception e) {
            getMessageSender().sendMessage(chatId, "Ошибка при удалении игры: " + e.getMessage());
        } finally {
            stateManager.resetUserData(userData);
            updateUserState(chatId, userData, BotState.MAIN_MENU);
            getMessageSender().sendMainMenu(chatId, getGameService().getAllGames());
        }
    }
}