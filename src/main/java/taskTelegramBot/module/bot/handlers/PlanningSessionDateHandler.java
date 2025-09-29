package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import taskTelegramBot.module.bot.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Обработчик состояния ввода даты игровой сессии
 *
 * <p>Управляет вторым шагом процесса планирования сессии -
 * получением и валидацией даты от пользователя.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class PlanningSessionDateHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика ввода даты сессии
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public PlanningSessionDateHandler(BotContext botContext, BotDependencies dependencies,
                                      StateManager stateManager, CallbackAnswerService callbackAnswerService,
                                      BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает ввод даты сессии пользователем
     *
     * <p>Проверяет корректность формата даты (ДД.ММ.ГГГГ) и то,
     * что дата не находится в прошлом. При успешной валидации
     * сохраняет дату и переходит к вводу времени.</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param message сообщение с датой сессии
     */
    @Override
    public void handleMessage(Long chatId, BotUserData userData, Message message) {
        String dateText = message.getText().trim();
        Integer gameId = userData.getSelectedGameId();

        if (gameId == null) {
            getMessageSender().sendMessage(chatId, "Ошибка: игра не выбрана");
            updateUserState(chatId, userData, BotState.MAIN_MENU);
            getMessageSender().sendMainMenu(chatId, getGameService().getAllGames());
            return;
        }

        try {
            if (!getValidationService().validateDate(dateText)) {
                getMessageSender().sendMessage(chatId, "Неверный формат даты или дата в прошлом. Используйте ДД.ММ.ГГГГ:");
                return;
            }

            LocalDate sessionDate = getValidationService().parseDate(dateText);
            userData.setTempData("sessionDate", sessionDate);

            updateUserState(chatId, userData, BotState.PLANNING_SESSION_TIME);
            getMessageSender().sendMessage(chatId, "Дата установлена: " +
                    sessionDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                    "\nТеперь введите время сессии (например, 18:30):");

        } catch (Exception e) {
            getMessageSender().sendMessage(chatId, "Ошибка: " + e.getMessage());
        }
    }
}