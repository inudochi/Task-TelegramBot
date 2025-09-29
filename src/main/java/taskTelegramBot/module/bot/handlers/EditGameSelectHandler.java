package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.dao.Game;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static taskTelegramBot.module.bot.handlers.HandlerUtils.createButton;

/**
 * Обработчик состояния выбора игры для редактирования
 *
 * <p>Управляет первым шагом процесса редактирования игры - выбором
 * конкретной игры из списка и отображением интерфейса выбора поля для редактирования.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class EditGameSelectHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика выбора игры для редактирования
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public EditGameSelectHandler(BotContext botContext, BotDependencies dependencies,
                                 StateManager stateManager, CallbackAnswerService callbackAnswerService,
                                 BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает выбор игры для редактирования через callback-запрос
     *
     * <p>Извлекает идентификатор выбранной игры, проверяет ее существование
     * и отображает интерфейс выбора поля для редактирования с текущими значениями.</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param callbackQuery callback-запрос с идентификатором выбранной игры
     */
    @Override
    public void handleCallbackQuery(Long chatId, BotUserData userData, CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        if (data.equals("back_to_menu")) {
            answerCallbackQuery(callbackQuery.getId(), "Возврат в главное меню");
            handleCancel(chatId, userData);
            return;
        }

        if (data.equals("cancel")) {
            answerCallbackQuery(callbackQuery.getId(), "Редактирование отменено");
            handleCancel(chatId, userData);
            return;
        }

        try {
            int gameId = Integer.parseInt(data.replace("edit_game_", ""));
            Game game = getGameService().getGameById(gameId);

            if (game == null) {
                answerCallbackQuery(callbackQuery.getId(), "Игра не найдена");
                getMessageSender().sendMessage(chatId, "Игра не найдена. Попробуйте выбрать другую игру.");
                return;
            }

            userData.setSelectedGameId(gameId);
            userData.setCurrentGame(game);
            answerCallbackQuery(callbackQuery.getId(), "Выбрана игра: " + game.getTitle());

            showEditFieldsKeyboard(chatId, game);

        } catch (NumberFormatException e) {
            answerCallbackQuery(callbackQuery.getId(), "Ошибка выбора игры");
            getMessageSender().sendMessage(chatId, "Ошибка выбора игры. Попробуйте еще раз.");
        } catch (Exception e) {
            answerCallbackQuery(callbackQuery.getId(), "Ошибка");
            getMessageSender().sendMessage(chatId, "Произошла ошибка: " + e.getMessage());
        }
    }

    /**
     * Отображает клавиатуру выбора поля для редактирования
     *
     * @param chatId идентификатор чата
     * @param game выбранная игра для редактирования
     */
    private void showEditFieldsKeyboard(Long chatId, Game game) {
        String message = String.format(
                "Редактирование игры:\n\n" +
                        "%s\n" +
                        "Жанр: %s\n" +
                        "Игроков: %d-%d\n" +
                        "Статус: %s\n\n" +
                        "Выберите поле для редактирования:",
                game.getTitle(),
                game.getGenre(),
                game.getMinPlayers(),
                game.getMaxPlayers(),
                game.getStatus()
        );

        InlineKeyboardMarkup keyboard = createEditFieldsKeyboard();
        getMessageSender().sendMessage(chatId, message, keyboard);
    }

    /**
     * Создает клавиатуру с полями для редактирования
     *
     * @return сконфигурированная inline-клавиатура
     */
    private InlineKeyboardMarkup createEditFieldsKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(createButton("Название", "edit_title"));
        row1.add(createButton("Жанр", "edit_genre"));

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(createButton("Мин. игроки", "edit_min_players"));
        row2.add(createButton("Макс. игроки", "edit_max_players"));

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(createButton("Назад к списку", "back_to_select"));
        row3.add(createButton("В меню", "back_to_menu"));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    /**
     * Обрабатывает отмену редактирования
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     */
    private void handleCancel(Long chatId, BotUserData userData) {
        stateManager.resetUserData(userData);
        updateUserState(chatId, userData, BotState.MAIN_MENU);

        List<Game> games = getGameService().getAllGames();
        getMessageSender().sendMainMenu(chatId, games);
    }

    /**
     * Обрабатывает текстовые сообщения в состоянии выбора игры для редактирования
     *
     * <p>Напоминает пользователю о необходимости использования кнопок
     * для выбора игры и поля редактирования</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param message полученное сообщение
     */
    @Override
    public void handleMessage(Long chatId, BotUserData userData, org.telegram.telegrambots.meta.api.objects.Message message) {
        getMessageSender().sendMessage(chatId, "Пожалуйста, используйте кнопки для выбора игры и поля редактирования.");
    }
}