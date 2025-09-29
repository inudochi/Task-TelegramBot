package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.dao.Game;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

/**
 * Обработчик главного меню бота
 *
 * <p>Управляет навигацией по основным функциям бота: добавление,
 * редактирование, удаление игр, планирование сессий и другие операции.
 * Является центральным узлом управления взаимодействием с пользователем.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class MainMenuHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика главного меню
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public MainMenuHandler(BotContext botContext, BotDependencies dependencies,
                           StateManager stateManager, CallbackAnswerService callbackAnswerService,
                           BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает текстовые команды главного меню
     *
     * <p>Определяет выбранную пользователем операцию и переводит
     * в соответствующее состояние для ее выполнения</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param message полученное сообщение с командой
     */
    @Override
    public void handleMessage(Long chatId, BotUserData userData, Message message) {
        String text = message.getText();

        switch (text) {
            case "Добавить игру":
                stateManager.resetUserData(userData);
                updateUserState(chatId, userData, BotState.ADDING_GAME_NAME);
                getMessageSender().sendMessage(chatId, "Введите название игры:");
                break;

            case "Редактировать игру":
                updateUserState(chatId, userData, BotState.EDITING_GAME_SELECT);
                List<Game> gamesForEdit = getGameService().getAllGames();
                if (gamesForEdit.isEmpty()) {
                    getMessageSender().sendMessage(chatId, "Нет игр для редактирования. Сначала добавьте игру.");
                    getMessageSender().sendMainMenu(chatId, gamesForEdit);
                } else {
                    getMessageSender().sendGamesList(chatId, gamesForEdit, "edit_game");
                }
                break;

            case "Удалить игру":
                updateUserState(chatId, userData, BotState.DELETING_GAME_SELECT);
                List<Game> games = getGameService().getAllGames();
                getMessageSender().sendGamesList(chatId, games, "delete_game");
                break;

            case "Запланировать сессию":
                updateUserState(chatId, userData, BotState.PLANNING_SESSION_SELECT);
                List<Game> allGames = getGameService().getAllGames();
                getMessageSender().sendGamesList(chatId, allGames, "plan_session");
                break;

            case "Случайная игра":
                handleRandomGame(chatId);
                break;

            case "Обновить статусы":
                updateAllGamesStatus(chatId);
                break;

            case "Сменить источник":
                updateUserState(chatId, userData, BotState.CHOOSING_SOURCE);
                getMessageSender().sendSourceSelection(chatId);
                break;

            default:
                getMessageSender().sendMessage(chatId, "Неизвестная команда. Используйте кнопки меню.");
                break;
        }
    }

    /**
     * Обрабатывает выбор случайной игры
     *
     * <p>Выбирает случайную игру из коллекции с предпочтением
     * неигранным играм и отображает информацию о ней</p>
     *
     * @param chatId идентификатор чата
     */
    private void handleRandomGame(Long chatId) {
        try {
            Game randomGame = getGameService().getRandomGame();
            if (randomGame != null) {
                String message = String.format(
                        "Случайная игра:\n\n%s\nЖанр: %s\nИгроков: %d-%d\nСтатус: %s",
                        randomGame.getTitle(), randomGame.getGenre(),
                        randomGame.getMinPlayers(), randomGame.getMaxPlayers(),
                        randomGame.getStatus()
                );
                getMessageSender().sendMessage(chatId, message);
            } else {
                getMessageSender().sendMessage(chatId, "Нет игр в коллекции");
            }
        } catch (Exception e) {
            getMessageSender().sendMessage(chatId, "Ошибка при выборе случайной игры: " + e.getMessage());
        }
    }

    /**
     * Обновляет статусы всех игр в коллекции
     *
     * <p>Выполняет массовое обновление статусов игр на основе
     * времени последней игровой сессии</p>
     *
     * @param chatId идентификатор чата
     */
    private void updateAllGamesStatus(Long chatId) {
        try {
            getGameService().updateAllGamesStatus();
            getMessageSender().sendMessage(chatId, "Статусы всех игр успешно обновлены!");

            List<Game> games = getGameService().getAllGames();
            getMessageSender().sendMainMenu(chatId, games);

        } catch (Exception e) {
            getMessageSender().sendMessage(chatId, "Ошибка при обновлении статусов: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает callback-запросы в главном меню
     *
     * <p>Обрабатывает нажатие кнопки "Назад" для возврата в главное меню
     * и предоставляет подсказки по использованию меню</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param callbackQuery callback-запрос
     */
    @Override
    public void handleCallbackQuery(Long chatId, BotUserData userData, CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        if (data.equals("back_to_menu")) {
            answerCallbackQuery(callbackQuery.getId(), "Возврат в главное меню");
            getMessageSender().sendMainMenu(chatId, getGameService().getAllGames());
        } else {
            answerCallbackQuery(callbackQuery.getId(), "Используйте кнопки меню для навигации");
        }
    }
}