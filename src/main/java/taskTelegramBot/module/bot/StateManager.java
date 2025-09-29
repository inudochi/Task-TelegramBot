package taskTelegramBot.module.bot;

/**
 * Менеджер состояний пользователя
 *
 * <p>Отвечает за управление состояниями пользователя в процессе
 * взаимодействия с ботом. Обеспечивает переходы между состояниями,
 * сброс временных данных и валидацию переходов.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class StateManager {

    /**
     * Обновляет состояние пользователя
     *
     * @param userData данные пользователя
     * @param newState новое состояние
     */
    public void updateUserState(BotUserData userData, BotState newState) {
        userData.setState(newState);
    }

    /**
     * Сбрасывает временные данные пользователя
     *
     * @param userData данные пользователя для сброса
     */
    public void resetUserData(BotUserData userData) {
        userData.clearTempData();
        userData.setSelectedGameId(null);
        userData.setCurrentGame(null);
    }

    /**
     * Проверяет валидность перехода между состояниями
     *
     * @param current текущее состояние
     * @param next следующее состояние
     * @return true если переход допустим, false в противном случае
     */
    public boolean isValidTransition(BotState current, BotState next) {
        // Логика валидации переходов между состояниями
        return true; // Упрощенная реализация
    }
}