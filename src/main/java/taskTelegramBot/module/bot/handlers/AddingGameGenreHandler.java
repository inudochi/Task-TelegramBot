package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import taskTelegramBot.module.bot.*;

/**
 * Обработчик состояния выбора жанра новой игры
 *
 * <p>Управляет вторым шагом процесса добавления игры - выбором
 * жанра из предопределенного списка допустимых жанров.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class AddingGameGenreHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика выбора жанра
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public AddingGameGenreHandler(BotContext botContext, BotDependencies dependencies,
                                  StateManager stateManager, CallbackAnswerService callbackAnswerService,
                                  BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает выбор жанра игры через callback-запрос
     *
     * <p>Проверяет корректность выбранного жанра и сохраняет его
     * во временные данные пользователя. При успешной валидации
     * переходит к вводу минимального количества игроков.</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param callbackQuery callback-запрос с выбранным жанром
     */
    @Override
    public void handleCallbackQuery(Long chatId, BotUserData userData, CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        String callbackId = callbackQuery.getId();

        if (handleBackButton(data, callbackId, chatId, userData)) return;
        if (handleCancelButton(data, callbackId, chatId, userData, "Добавление игры отменено")) return;

        String genre = data.replace("genre_", "");

        try {
            if (!getValidationService().validateGenre(genre)) {
                answerCallbackQuery(callbackId, "Выберите жанр из списка");
                return;
            }

            userData.setTempData("newGameGenre", genre);
            answerCallbackQuery(callbackId, "Жанр выбран: " + genre);

            updateUserState(chatId, userData, BotState.ADDING_GAME_MIN_PLAYERS);
            getMessageSender().sendMessage(chatId, "Введите минимальное количество игроков (1-16):");

        } catch (Exception e) {
            answerCallbackQuery(callbackId, "Ошибка выбора жанра");
            getMessageSender().sendMessage(chatId, "Ошибка: " + e.getMessage());
        }
    }
}