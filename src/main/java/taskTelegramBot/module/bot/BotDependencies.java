package taskTelegramBot.module.bot;

import taskTelegramBot.module.service.GameService;
import taskTelegramBot.module.service.SessionService;
import taskTelegramBot.module.service.ValidationService;

/**
 * Контейнер зависимостей сервисов для бота
 *
 * <p>Инкапсулирует все сервисы приложения, необходимые для работы бота.
 * Обеспечивает централизованное управление зависимостями и легкое
 * переключение между различными реализациями сервисов.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class BotDependencies {
    private final GameService gameService;
    private final ValidationService validationService;
    private final SessionService sessionService;

    /**
     * Конструктор контейнера зависимостей
     *
     * @param gameService сервис работы с играми
     */
    public BotDependencies(GameService gameService) {
        this.gameService = gameService;
        this.validationService = new ValidationService();
        this.sessionService = new SessionService(gameService);
    }

    // region Геттеры

    /**
     * Возвращает сервис работы с играми
     *
     * @return экземпляр GameService
     */
    public GameService getGameService() {
        return gameService;
    }

    /**
     * Возвращает сервис валидации данных
     *
     * @return экземпляр ValidationService
     */
    public ValidationService getValidationService() {
        return validationService;
    }

    /**
     * Возвращает сервис управления сессиями
     *
     * @return экземпляр SessionService
     */
    public SessionService getSessionService() {
        return sessionService;
    }

    // endregion
}