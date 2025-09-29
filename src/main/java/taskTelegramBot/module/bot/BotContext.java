package taskTelegramBot.module.bot;

import taskTelegramBot.module.bot.handlers.BotStateHandlerFactory;
import taskTelegramBot.module.service.GameService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

/**
 * Контекст бота, содержащий все необходимые зависимости и сервисы
 *
 * <p>Центральный класс, который объединяет все компоненты бота:
 * сервисы данных, отправитель сообщений, фабрики и обработчики.
 * Обеспечивает легкий доступ к зависимостям из любого обработчика.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class BotContext {
    private final GameService gameService;
    private final MessageSender messageSender;
    private final KeyboardFactory keyboardFactory;
    private BotStateHandlerFactory handlerFactory;

    /**
     * Конструктор контекста бота
     *
     * @param gameService сервис работы с играми
     * @param messageSender отправитель сообщений
     * @param keyboardFactory фабрика клавиатур
     */
    public BotContext(GameService gameService, MessageSender messageSender, KeyboardFactory keyboardFactory) {
        this.gameService = gameService;
        this.messageSender = messageSender;
        this.keyboardFactory = keyboardFactory;
    }

    // region Геттеры

    /**
     * Возвращает сервис работы с играми
     *
     * @return экземпляр GameService
     */
    public GameService getGameService() { return gameService; }

    /**
     * Возвращает отправитель сообщений
     *
     * @return экземпляр MessageSender
     */
    public MessageSender getMessageSender() { return messageSender; }

    /**
     * Возвращает фабрику клавиатур
     *
     * @return экземпляр KeyboardFactory
     */
    public KeyboardFactory getKeyboardFactory() { return keyboardFactory; }

    /**
     * Возвращает фабрику обработчиков состояний
     *
     * @return экземпляр BotStateHandlerFactory
     */
    public BotStateHandlerFactory getHandlerFactory() {
        return handlerFactory;
    }

    /**
     * Устанавливает фабрику обработчиков состояний
     *
     * @param handlerFactory фабрика обработчиков
     */
    public void setHandlerFactory(BotStateHandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    /**
     * Возвращает экземпляр бота для низкоуровневых операций
     *
     * @return экземпляр TelegramLongPollingBot
     */
    public TelegramLongPollingBot getBot() {
        return messageSender.getBot();
    }

    // endregion
}