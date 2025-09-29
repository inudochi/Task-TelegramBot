package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.dao.Game;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Обработчик состояния выбора игры для удаления
 *
 * <p>Управляет первым шагом процесса удаления игры - выбором
 * конкретной игры из списка для последующего подтверждения удаления.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class DeleteGameSelectHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика выбора игры для удаления
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public DeleteGameSelectHandler(BotContext botContext, BotDependencies dependencies,
                                   StateManager stateManager, CallbackAnswerService callbackAnswerService,
                                   BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает выбор игры для удаления через callback-запрос
     *
     * <p>Извлекает идентификатор выбранной игры, проверяет ее существование
     * и переводит в состояние подтверждения удаления с отображением
     * подробной информации об игре.</p>
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
        if (handleCancelButton(data, callbackId, chatId, userData, "Удаление отменено")) return;

        try {
            int gameId = Integer.parseInt(data.replace("delete_game_", ""));
            Game game = getGameService().getGameById(gameId);

            if (game == null) {
                answerCallbackQuery(callbackId, "Игра не найдена");
                return;
            }

            userData.setSelectedGameId(gameId);
            answerCallbackQuery(callbackId, "Выбрана игра: " + game.getTitle());

            updateUserState(chatId, userData, BotState.DELETING_GAME_CONFIRM);

            String confirmationMessage = String.format(
                    "Вы уверены, что хотите удалить игру?\n\n%s\nЖанр: %s\nИгроков: %d-%d",
                    game.getTitle(), game.getGenre(), game.getMinPlayers(), game.getMaxPlayers()
            );

            getMessageSender().sendConfirmationKeyboard(chatId, confirmationMessage);

        } catch (NumberFormatException e) {
            answerCallbackQuery(callbackId, "Ошибка выбора игры");
        } catch (Exception e) {
            answerCallbackQuery(callbackId, "Ошибка");
            getMessageSender().sendMessage(chatId, "Ошибка: " + e.getMessage());
        }
    }
}