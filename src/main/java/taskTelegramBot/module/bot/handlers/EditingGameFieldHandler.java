package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.dao.Game;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Обработчик состояния редактирования конкретного поля игры
 *
 * <p>Управляет процессом ввода нового значения для выбранного поля
 * игры и сохранения изменений в системе.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class EditingGameFieldHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика редактирования поля игры
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public EditingGameFieldHandler(BotContext botContext, BotDependencies dependencies,
                                   StateManager stateManager, CallbackAnswerService callbackAnswerService,
                                   BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает выбор поля для редактирования через callback-запрос
     *
     * <p>Сохраняет выбранное поле во временные данные пользователя
     * и запрашивает новое значение с отображением текущего значения.</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param callbackQuery callback-запрос с выбранным полем
     */
    @Override
    public void handleCallbackQuery(Long chatId, BotUserData userData, CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        if (data.equals("back_to_menu")) {
            answerCallbackQuery(callbackQuery.getId(), "Возврат в главное меню");
            updateUserState(chatId, userData, BotState.MAIN_MENU);
            getMessageSender().sendMainMenu(chatId, getGameService().getAllGames());
            return;
        }

        if (data.equals("back_to_select")) {
            answerCallbackQuery(callbackQuery.getId(), "Возврат к выбору игры");
            updateUserState(chatId, userData, BotState.EDITING_GAME_SELECT);
            getMessageSender().sendGamesList(chatId, getGameService().getAllGames(), "edit_game");
            return;
        }

        userData.setTempData("editingField", data.replace("edit_", ""));
        String fieldName = getFieldDisplayName(data);
        answerCallbackQuery(callbackQuery.getId(), "Редактирование: " + fieldName);

        Game game = userData.getCurrentGame();
        String currentValue = getCurrentFieldValue(game, data);
        getMessageSender().sendMessage(chatId,
                String.format("Текущее значение %s: %s\nВведите новое значение:", fieldName, currentValue));
    }

    /**
     * Обрабатывает ввод нового значения для поля игры
     *
     * <p>Проверяет корректность нового значения и сохраняет изменения
     * в системе. При успешном обновлении возвращает пользователя в главное меню.</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param message сообщение с новым значением поля
     */
    @Override
    public void handleMessage(Long chatId, BotUserData userData, Message message) {
        String newValue = message.getText().trim();
        String field = userData.getTempString("editingField");
        Integer gameId = userData.getSelectedGameId();

        if (gameId == null || field == null) {
            getMessageSender().sendMessage(chatId, "Ошибка: данные для редактирования не найдены");
            updateUserState(chatId, userData, BotState.MAIN_MENU);
            getMessageSender().sendMainMenu(chatId, getGameService().getAllGames());
            return;
        }

        try {
            Game updatedGame = getGameService().updateGameField(gameId, field, newValue);

            getMessageSender().sendMessage(chatId, "Изменения сохранены!");
            stateManager.resetUserData(userData);
            updateUserState(chatId, userData, BotState.MAIN_MENU);
            getMessageSender().sendMainMenu(chatId, getGameService().getAllGames());

        } catch (IllegalArgumentException e) {
            getMessageSender().sendMessage(chatId, "Ошибка: " + e.getMessage());
        } catch (Exception e) {
            getMessageSender().sendMessage(chatId, "Ошибка при сохранении изменений: " + e.getMessage());
        }
    }

    /**
     * Возвращает отображаемое имя поля для пользователя
     *
     * @param field идентификатор поля
     * @return отображаемое имя поля
     */
    private String getFieldDisplayName(String field) {
        switch (field) {
            case "edit_title": return "названия";
            case "edit_genre": return "жанра";
            case "edit_min_players": return "минимального количества игроков";
            case "edit_max_players": return "максимального количества игроков";
            default: return "поля";
        }
    }

    /**
     * Возвращает текущее значение указанного поля игры
     *
     * @param game игра
     * @param field идентификатор поля
     * @return текущее значение поля
     */
    private String getCurrentFieldValue(Game game, String field) {
        switch (field) {
            case "edit_title": return game.getTitle();
            case "edit_genre": return game.getGenre();
            case "edit_min_players": return String.valueOf(game.getMinPlayers());
            case "edit_max_players": return String.valueOf(game.getMaxPlayers());
            default: return "неизвестно";
        }
    }
}