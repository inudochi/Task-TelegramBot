package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.dao.Game;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Обработчик состояния ввода максимального количества игроков
 *
 * <p>Управляет заключительным шагом процесса добавления игры -
 * получением и валидацией максимального количества игроков
 * и созданием новой игры в системе.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class AddingGameMaxPlayersHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика ввода максимального количества игроков
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public AddingGameMaxPlayersHandler(BotContext botContext, BotDependencies dependencies,
                                       StateManager stateManager, CallbackAnswerService callbackAnswerService,
                                       BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает ввод максимального количества игроков и создает игру
     *
     * <p>Проверяет корректность введенного числа (1-16) и его соответствие
     * минимальному количеству. При успешной валидации создает новую игру
     * и возвращает пользователя в главное меню.</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param message сообщение с количеством игроков
     */
    @Override
    public void handleMessage(Long chatId, BotUserData userData, Message message) {
        Integer maxPlayers = validateAndParseInteger(message.getText(), chatId);
        if (maxPlayers == null) return;

        String title = userData.getTempString("newGameTitle");
        String genre = userData.getTempString("newGameGenre");
        int minPlayers = userData.getTempInt("newGameMinPlayers");

        if (!validateMaxPlayers(minPlayers, maxPlayers, chatId)) return;

        try {
            Game game = getGameService().createGameWithValidation(title, genre, minPlayers, maxPlayers);

            stateManager.resetUserData(userData);
            updateUserState(chatId, userData, BotState.MAIN_MENU);

            getMessageSender().sendMessage(chatId, "Игра '" + game.getTitle() + "' успешно добавлена!");
            getMessageSender().sendMainMenu(chatId, getGameService().getAllGames());

        } catch (IllegalArgumentException e) {
            getMessageSender().sendMessage(chatId, "Ошибка валидации: " + e.getMessage());
        } catch (Exception e) {
            getMessageSender().sendMessage(chatId, "Произошла ошибка при добавлении игры: " + e.getMessage());
        }
    }
}