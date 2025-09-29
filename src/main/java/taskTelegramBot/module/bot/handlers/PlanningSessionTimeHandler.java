package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.dao.Game;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Обработчик состояния ввода времени игровой сессии
 *
 * <p>Управляет заключительным шагом процесса планирования сессии -
 * получением времени, объединением с датой и сохранением сессии в системе.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class PlanningSessionTimeHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика ввода времени сессии
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public PlanningSessionTimeHandler(BotContext botContext, BotDependencies dependencies,
                                      StateManager stateManager, CallbackAnswerService callbackAnswerService,
                                      BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает ввод времени сессии и сохраняет запланированную сессию
     *
     * <p>Проверяет корректность формата времени (ЧЧ:ММ), объединяет
     * дату и время, проверяет что итоговая дата-время не в прошлом,
     * и сохраняет сессию в системе.</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param message сообщение с временем сессии
     */
    @Override
    public void handleMessage(Long chatId, BotUserData userData, Message message) {
        String timeText = message.getText().trim();
        Integer gameId = userData.getSelectedGameId();
        LocalDate sessionDate = (LocalDate) userData.getTempData("sessionDate");

        if (gameId == null || sessionDate == null) {
            getMessageSender().sendMessage(chatId, "Ошибка: данные сессии неполные");
            updateUserState(chatId, userData, BotState.MAIN_MENU);
            getMessageSender().sendMainMenu(chatId, getGameService().getAllGames());
            return;
        }

        try {
            if (!getValidationService().validateTime(timeText)) {
                getMessageSender().sendMessage(chatId, "Неверный формат времени. Используйте ЧЧ:MM:");
                return;
            }

            LocalTime sessionTime = getValidationService().parseTime(timeText);
            LocalDateTime sessionDateTime = LocalDateTime.of(sessionDate, sessionTime);

            if (sessionDateTime.isBefore(LocalDateTime.now())) {
                getMessageSender().sendMessage(chatId, "Дата и время не могут быть в прошлом");
                return;
            }

            getSessionService().planFutureSession(gameId, sessionDateTime);

            Game game = getGameService().getGameById(gameId);
            String successMessage = String.format(
                    "Сессия запланирована!\n\n Игра: %s\n Дата: %s\n Время: %s",
                    game.getTitle(),
                    sessionDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    sessionTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            );

            getMessageSender().sendMessage(chatId, successMessage);

            stateManager.resetUserData(userData);
            updateUserState(chatId, userData, BotState.MAIN_MENU);
            getMessageSender().sendMainMenu(chatId, getGameService().getAllGames());

        } catch (Exception e) {
            getMessageSender().sendMessage(chatId, "Ошибка при планировании сессии: " + e.getMessage());
        }
    }
}