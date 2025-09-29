package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.bot.*;

import java.util.EnumMap;
import java.util.Map;

/**
 * Фабрика обработчиков состояний с поддержкой новых сервисов
 *
 * <p>Создает и управляет обработчиками для каждого состояния бота.
 * Реализует паттерн Фабрика для инкапсуляции логики создания обработчиков
 * и обеспечивает легкое переключение между различными состояниями.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class BotStateHandlerFactory {
    private final Map<BotState, BotStateHandler> handlers;
    private BotDependencies dependencies;
    private BotContext botContext;
    private final StateManager stateManager;
    private final CallbackAnswerService callbackAnswerService;
    private final BotValidationService botValidationService;

    /**
     * Конструктор фабрики обработчиков
     *
     * @param botContext контекст бота с основными зависимостями
     * @param dependencies зависимости сервисов приложения
     */
    public BotStateHandlerFactory(BotContext botContext, BotDependencies dependencies) {
        this.botContext = botContext;
        this.dependencies = dependencies;
        this.stateManager = new StateManager();
        this.callbackAnswerService = new CallbackAnswerService(botContext.getBot());
        this.botValidationService = new BotValidationService(botContext.getMessageSender());
        this.handlers = new EnumMap<>(BotState.class);
        initializeHandlers();
    }

    /**
     * Инициализирует все обработчики состояний
     */
    private void initializeHandlers() {
        // Основные обработчики
        handlers.put(BotState.START, createStartStateHandler());
        handlers.put(BotState.CHOOSING_SOURCE, createSourceSelectionHandler());
        handlers.put(BotState.MAIN_MENU, createMainMenuHandler());
        handlers.put(BotState.ADDING_GAME_NAME, createAddingGameNameHandler());
        handlers.put(BotState.ADDING_GAME_GENRE, createAddingGameGenreHandler());
        handlers.put(BotState.ADDING_GAME_MIN_PLAYERS, createAddingGameMinPlayersHandler());
        handlers.put(BotState.ADDING_GAME_MAX_PLAYERS, createAddingGameMaxPlayersHandler());
        handlers.put(BotState.DELETING_GAME_SELECT, createDeleteGameSelectHandler());
        handlers.put(BotState.DELETING_GAME_CONFIRM, createDeleteGameConfirmHandler());

        // Обработчики редактирования
        handlers.put(BotState.EDITING_GAME_SELECT, createEditGameSelectHandler());
        handlers.put(BotState.EDITING_GAME_FIELD, createEditingGameFieldHandler());

        // Обработчики планирования сессии
        handlers.put(BotState.PLANNING_SESSION_SELECT, createPlanningSessionSelectHandler());
        handlers.put(BotState.PLANNING_SESSION_DATE, createPlanningSessionDateHandler());
        handlers.put(BotState.PLANNING_SESSION_TIME, createPlanningSessionTimeHandler());
    }

    // region Фабричные методы для каждого обработчика

    /**
     * Создает обработчик начального состояния
     *
     * @return обработчик состояния START
     */
    private StartStateHandler createStartStateHandler() {
        return new StartStateHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик выбора источника данных
     *
     * @return обработчик состояния CHOOSING_SOURCE
     */
    private SourceSelectionHandler createSourceSelectionHandler() {
        return new SourceSelectionHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик главного меню
     *
     * @return обработчик состояния MAIN_MENU
     */
    private MainMenuHandler createMainMenuHandler() {
        return new MainMenuHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик ввода названия игры
     *
     * @return обработчик состояния ADDING_GAME_NAME
     */
    private AddingGameNameHandler createAddingGameNameHandler() {
        return new AddingGameNameHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик выбора жанра игры
     *
     * @return обработчик состояния ADDING_GAME_GENRE
     */
    private AddingGameGenreHandler createAddingGameGenreHandler() {
        return new AddingGameGenreHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик ввода минимального количества игроков
     *
     * @return обработчик состояния ADDING_GAME_MIN_PLAYERS
     */
    private AddingGameMinPlayersHandler createAddingGameMinPlayersHandler() {
        return new AddingGameMinPlayersHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик ввода максимального количества игроков
     *
     * @return обработчик состояния ADDING_GAME_MAX_PLAYERS
     */
    private AddingGameMaxPlayersHandler createAddingGameMaxPlayersHandler() {
        return new AddingGameMaxPlayersHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик выбора игры для удаления
     *
     * @return обработчик состояния DELETING_GAME_SELECT
     */
    private DeleteGameSelectHandler createDeleteGameSelectHandler() {
        return new DeleteGameSelectHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик подтверждения удаления игры
     *
     * @return обработчик состояния DELETING_GAME_CONFIRM
     */
    private DeleteGameConfirmHandler createDeleteGameConfirmHandler() {
        return new DeleteGameConfirmHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик выбора игры для редактирования
     *
     * @return обработчик состояния EDITING_GAME_SELECT
     */
    private EditGameSelectHandler createEditGameSelectHandler() {
        return new EditGameSelectHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик редактирования поля игры
     *
     * @return обработчик состояния EDITING_GAME_FIELD
     */
    private EditingGameFieldHandler createEditingGameFieldHandler() {
        return new EditingGameFieldHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик выбора игры для планирования сессии
     *
     * @return обработчик состояния PLANNING_SESSION_SELECT
     */
    private PlanningSessionSelectHandler createPlanningSessionSelectHandler() {
        return new PlanningSessionSelectHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик ввода даты сессии
     *
     * @return обработчик состояния PLANNING_SESSION_DATE
     */
    private PlanningSessionDateHandler createPlanningSessionDateHandler() {
        return new PlanningSessionDateHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик ввода времени сессии
     *
     * @return обработчик состояния PLANNING_SESSION_TIME
     */
    private PlanningSessionTimeHandler createPlanningSessionTimeHandler() {
        return new PlanningSessionTimeHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    /**
     * Создает обработчик по умолчанию для неизвестных состояний
     *
     * @return обработчик состояния по умолчанию
     */
    private DefaultStateHandler createDefaultHandler() {
        return new DefaultStateHandler(botContext, dependencies, stateManager, callbackAnswerService, botValidationService);
    }

    // endregion

    /**
     * Возвращает обработчик для указанного состояния
     *
     * @param state состояние бота
     * @return соответствующий обработчик или обработчик по умолчанию
     */
    public BotStateHandler getHandler(BotState state) {
        return handlers.getOrDefault(state, createDefaultHandler());
    }

    /**
     * Метод для обновления зависимостей во всех обработчиках
     *
     * <p>Используется при переключении источника данных для обновления
     * всех сервисов в реальном времени</p>
     *
     * @param newDependencies новые зависимости сервисов
     */
    public void updateDependencies(BotDependencies newDependencies) {
        this.dependencies = newDependencies;

        // Создаем новый контекст с обновленным сервисом
        BotContext newContext = new BotContext(
                newDependencies.getGameService(),
                this.botContext.getMessageSender(),
                this.botContext.getKeyboardFactory()
        );

        // Сохраняем фабрику обработчиков в новом контексте
        newContext.setHandlerFactory(this);
        this.botContext = newContext;

        // Переинициализируем все обработчики с новыми зависимостями
        initializeHandlers();

        System.out.println("Зависимости обновлены. Новый источник: " +
                newDependencies.getGameService().getClass().getSimpleName());
    }

    /**
     * Возвращает текущий контекст бота
     *
     * @return контекст бота
     */
    public BotContext getBotContext() {
        return botContext;
    }
}