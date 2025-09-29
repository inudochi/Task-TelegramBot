package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import taskTelegramBot.module.bot.*;

/**
 * Обработчик состояния ввода названия новой игры
 *
 * <p>Управляет первым шагом процесса добавления игры - получением
 * и валидацией названия игры от пользователя.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class AddingGameNameHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика ввода названия игры
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public AddingGameNameHandler(BotContext botContext, BotDependencies dependencies,
                                 StateManager stateManager, CallbackAnswerService callbackAnswerService,
                                 BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает ввод названия игры пользователем
     *
     * <p>Проверяет корректность названия (2-100 символов) и сохраняет
     * его во временные данные пользователя. При успешной валидации
     * переходит к выбору жанра.</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param message сообщение с названием игры
     */
    @Override
    public void handleMessage(Long chatId, BotUserData userData, Message message) {
        String title = message.getText().trim();

        try {
            if (!getValidationService().validateTitle(title)) {
                getMessageSender().sendMessage(chatId, "Название игры должно содержать от 2 до 100 символов");
                return;
            }

            userData.setTempData("newGameTitle", title);
            updateUserState(chatId, userData, BotState.ADDING_GAME_GENRE);
            getMessageSender().sendMessage(chatId, "Выберите жанр:", getKeyboardFactory().createGenreSelectionKeyboard());

        } catch (Exception e) {
            getMessageSender().sendMessage(chatId, "Ошибка: " + e.getMessage());
        }
    }
}