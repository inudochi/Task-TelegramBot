package taskTelegramBot.module.bot;

import taskTelegramBot.module.bot.handlers.BotStateHandler;
import taskTelegramBot.module.bot.handlers.BotStateHandlerFactory;
import taskTelegramBot.module.dao.GameDaoFactory;
import taskTelegramBot.module.service.GameService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Основной класс Telegram бота для управления игровой коллекцией
 *
 * <p>Реализует логику получения и маршрутизации обновлений от Telegram,
 * управляет состояниями пользователей и координирует работу всех
 * обработчиков и сервисов приложения.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class ManagerBot extends TelegramLongPollingBot {
    private final Map<Long, BotUserData> userStates = new ConcurrentHashMap<>();
    private final BotStateHandlerFactory handlerFactory;
    private final MessageSender messageSender;

    /**
     * Конструктор бота, инициализирует все зависимости
     */
    public ManagerBot() {
        this.messageSender = new MessageSender(this);

        // Инициализируем сервис с источником по умолчанию
        GameService defaultGameService = new GameService(
                GameDaoFactory.createDao(GameDaoFactory.DataSourceType.JSON)
        );

        BotDependencies dependencies = new BotDependencies(defaultGameService);
        KeyboardFactory keyboardFactory = new KeyboardFactory();

        // Создаем BotContext
        BotContext botContext = new BotContext(defaultGameService, messageSender, keyboardFactory);

        // Создаем фабрику обработчиков
        this.handlerFactory = new BotStateHandlerFactory(botContext, dependencies);

        // Устанавливаем фабрику в контекст
        botContext.setHandlerFactory(handlerFactory);

        System.out.println("Bot initialized with JSON data source");
    }

    /**
     * Возвращает имя пользователя бота
     *
     * @return имя бота в Telegram
     */
    @Override
    public String getBotUsername() {
        return "GameManagerBot";
    }

    /**
     * Возвращает токен бота для аутентификации в Telegram API
     *
     * @return токен бота
     */
    @Override
    public String getBotToken() {
        return "8231989050:AAFiHOk9ioI4QwesyeKWV4A83G8twW3JJDw";
    }

    /**
     * Обрабатывает входящие обновления от Telegram
     *
     * <p>Маршрутизирует текстовые сообщения и callback-запросы
     * соответствующим обработчикам в зависимости от текущего
     * состояния пользователя</p>
     *
     * @param update входящее обновление от Telegram
     */
    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = getChatId(update);
        if (chatId == null) return;

        BotUserData userData = userStates.computeIfAbsent(chatId,
                id -> new BotUserData(BotState.START));

        BotStateHandler handler = handlerFactory.getHandler(userData.getState());

        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handler.handleMessage(chatId, userData, update.getMessage());
            } else if (update.hasCallbackQuery()) {
                handler.handleCallbackQuery(chatId, userData, update.getCallbackQuery());
            }

            userStates.put(chatId, userData);

        } catch (Exception e) {
            System.err.println("Error processing update: " + e.getMessage());
            e.printStackTrace();
            messageSender.sendMessage(chatId, "An unexpected error occurred. Please try again.");
        }
    }

    /**
     * Извлекает идентификатор чата из обновления
     *
     * @param update обновление от Telegram
     * @return идентификатор чата или null если извлечь не удалось
     */
    private Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return null;
    }
}