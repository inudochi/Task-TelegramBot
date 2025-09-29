package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.dao.Game;
import taskTelegramBot.module.dao.GameDaoFactory;
import taskTelegramBot.module.service.GameService;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

/**
 * Обработчик состояния выбора источника данных
 *
 * <p>Управляет процессом выбора между JSON и PostgreSQL в качестве
 * источника хранения данных. Обеспечивает переключение источников
 * в реальном времени с обновлением всех зависимостей.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class SourceSelectionHandler extends BaseStateHandler {

    /**
     * Конструктор обработчика выбора источника данных
     *
     * @param botContext контекст бота
     * @param dependencies зависимости сервисов
     * @param stateManager менеджер состояний
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации
     */
    public SourceSelectionHandler(BotContext botContext, BotDependencies dependencies,
                                  StateManager stateManager, CallbackAnswerService callbackAnswerService,
                                  BotValidationService botValidationService) {
        super(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Обрабатывает выбор источника данных через callback-запросы
     *
     * <p>Обрабатывает нажатия кнопок выбора JSON или PostgreSQL,
     * обновляет зависимости сервисов и переводит в главное меню</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param callbackQuery callback-запрос с выбранным источником
     */
    @Override
    public void handleCallbackQuery(Long chatId, BotUserData userData, CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        String callbackId = callbackQuery.getId();

        if (data.equals("back_to_menu")) {
            answerCallbackQuery(callbackId, "Возврат в главное меню");
            updateUserState(chatId, userData, BotState.MAIN_MENU);
            getMessageSender().sendMainMenu(chatId, getGameService().getAllGames());
            return;
        }

        try {
            GameDaoFactory.DataSourceType sourceType = parseSourceType(data);
            userData.setDataSource(sourceType);

            // Создаем новый сервис с выбранным источником
            GameService newService = new GameService(GameDaoFactory.createDao(sourceType));

            // Обновляем зависимости
            BotDependencies newDependencies = new BotDependencies(newService);

            // Обновляем фабрику обработчиков
            BotStateHandlerFactory handlerFactory = botContext.getHandlerFactory();
            if (handlerFactory != null) {
                handlerFactory.updateDependencies(newDependencies);

                // Обновляем текущий обработчик
                updateDependencies(newDependencies);
                updateBotContext(handlerFactory.getBotContext());
            }

            answerCallbackQuery(callbackId, "Источник данных выбран: " + sourceType);

            // Обновляем состояние и показываем главное меню
            updateUserState(chatId, userData, BotState.MAIN_MENU);

            // Загружаем игры из нового источника
            List<Game> games = newService.getAllGames();
            getMessageSender().sendMainMenu(chatId, games);

            System.out.println("Успешно переключено на источник: " + sourceType);

        } catch (Exception e) {
            System.err.println("Ошибка при переключении источника: " + e.getMessage());
            e.printStackTrace();
            answerCallbackQuery(callbackId, "Ошибка выбора источника");
            getMessageSender().sendMessage(chatId, "Ошибка выбора источника: " + e.getMessage());
            getMessageSender().sendSourceSelection(chatId);
        }
    }

    /**
     * Преобразует данные callback-запроса в тип источника данных
     *
     * @param data данные callback-запроса
     * @return тип источника данных
     * @throws IllegalArgumentException если тип источника неизвестен
     */
    private GameDaoFactory.DataSourceType parseSourceType(String data) {
        switch (data) {
            case "source_json":
                return GameDaoFactory.DataSourceType.JSON;
            case "source_postgres":
                return GameDaoFactory.DataSourceType.POSTGRES;
            default:
                throw new IllegalArgumentException("Неизвестный источник: " + data);
        }
    }

    /**
     * Обрабатывает текстовые сообщения в состоянии выбора источника
     *
     * <p>Напоминает пользователю о необходимости использования кнопок
     * для выбора источника данных</p>
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param message полученное сообщение
     */
    @Override
    public void handleMessage(Long chatId, BotUserData userData, org.telegram.telegrambots.meta.api.objects.Message message) {
        getMessageSender().sendMessage(chatId, "Пожалуйста, выберите источник данных используя кнопки");
        getMessageSender().sendSourceSelection(chatId);
    }
}