package taskTelegramBot.module.bot.handlers;

import taskTelegramBot.module.bot.*;
import taskTelegramBot.module.service.GameService;
import taskTelegramBot.module.service.SessionService;
import taskTelegramBot.module.service.ValidationService;

/**
 * Базовый абстрактный класс обработчиков состояний с разделенными ответственностями
 *
 * <p>Предоставляет общую функциональность для всех обработчиков состояний,
 * включая доступ к сервисам, управление состояниями и обработку callback-запросов.
 * Реализует принцип DRY, устраняя дублирование кода в конкретных обработчиках.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public abstract class BaseStateHandler implements BotStateHandler {
    protected BotContext botContext;
    protected BotDependencies dependencies;
    protected final StateManager stateManager;
    protected final CallbackAnswerService callbackAnswerService;
    protected final BotValidationService botValidationService;

    /**
     * Конструктор базового обработчика
     *
     * @param botContext контекст бота с основными зависимостями
     * @param dependencies зависимости сервисов приложения
     * @param stateManager менеджер управления состояниями пользователя
     * @param callbackAnswerService сервис ответов на callback-запросы
     * @param botValidationService сервис валидации для бота
     */
    protected BaseStateHandler(BotContext botContext, BotDependencies dependencies,
                               StateManager stateManager, CallbackAnswerService callbackAnswerService,
                               BotValidationService botValidationService) {
        this.botContext = botContext;
        this.dependencies = dependencies;
        this.stateManager = stateManager;
        this.callbackAnswerService = callbackAnswerService;
        this.botValidationService = botValidationService;
    }

    // region Геттеры для сервисов

    /**
     * Возвращает сервис работы с играми
     *
     * @return экземпляр GameService
     */
    protected GameService getGameService() {
        return dependencies.getGameService();
    }

    /**
     * Возвращает сервис валидации данных
     *
     * @return экземпляр ValidationService
     */
    protected ValidationService getValidationService() {
        return dependencies.getValidationService();
    }

    /**
     * Возвращает сервис управления сессиями
     *
     * @return экземпляр SessionService
     */
    protected SessionService getSessionService() {
        return dependencies.getSessionService();
    }

    /**
     * Возвращает отправитель сообщений
     *
     * @return экземпляр MessageSender
     */
    protected MessageSender getMessageSender() {
        return botContext.getMessageSender();
    }

    /**
     * Возвращает фабрику клавиатур
     *
     * @return экземпляр KeyboardFactory
     */
    protected KeyboardFactory getKeyboardFactory() {
        return botContext.getKeyboardFactory();
    }

    // endregion

    // region Делегирование ответственности специализированным сервисам

    /**
     * Обновляет состояние пользователя
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param newState новое состояние
     */
    protected void updateUserState(Long chatId, BotUserData userData, BotState newState) {
        stateManager.updateUserState(userData, newState);
    }

    /**
     * Отправляет ответ на callback-запрос
     *
     * @param callbackQueryId идентификатор callback-запроса
     * @param text текст ответа
     */
    protected void answerCallbackQuery(String callbackQueryId, String text) {
        callbackAnswerService.answerCallbackQuery(callbackQueryId, text);
    }

    /**
     * Валидирует и преобразует строку в целое число
     *
     * @param input входная строка
     * @param chatId идентификатор чата для отправки сообщений об ошибках
     * @return преобразованное число или null если преобразование не удалось
     */
    protected Integer validateAndParseInteger(String input, Long chatId) {
        return botValidationService.validateAndParseInteger(input, getMessageSender(), chatId);
    }

    /**
     * Проверяет корректность минимального количества игроков
     *
     * @param minPlayers минимальное количество игроков
     * @param chatId идентификатор чата для отправки сообщений об ошибках
     * @return true если количество корректно, false в противном случае
     */
    protected boolean validateMinPlayers(int minPlayers, Long chatId) {
        return botValidationService.validateMinPlayers(minPlayers, getMessageSender(), chatId);
    }

    /**
     * Проверяет корректность максимального количества игроков
     *
     * @param minPlayers минимальное количество игроков
     * @param maxPlayers максимальное количество игроков
     * @param chatId идентификатор чата для отправки сообщений об ошибках
     * @return true если количество корректно, false в противном случае
     */
    protected boolean validateMaxPlayers(int minPlayers, int maxPlayers, Long chatId) {
        return botValidationService.validateMaxPlayers(minPlayers, maxPlayers, getMessageSender(), chatId);
    }

    // endregion

    // region Упрощенные методы для обработки кнопок (делегируют HandlerUtils)

    /**
     * Обрабатывает нажатие кнопки "Назад"
     *
     * @param callbackData данные callback-запроса
     * @param callbackQueryId идентификатор callback-запроса
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @return true если кнопка "Назад" была обработана, false в противном случае
     */
    protected boolean handleBackButton(String callbackData, String callbackQueryId,
                                       Long chatId, BotUserData userData) {
        return HandlerUtils.handleBackButton(callbackData, callbackQueryId, chatId, userData,
                getMessageSender(), getGameService());
    }

    /**
     * Обрабатывает нажатие кнопки "Отмена"
     *
     * @param callbackData данные callback-запроса
     * @param callbackQueryId идентификатор callback-запроса
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param cancelMessage сообщение об отмене операции
     * @return true если кнопка "Отмена" была обработана, false в противном случае
     */
    protected boolean handleCancelButton(String callbackData, String callbackQueryId,
                                         Long chatId, BotUserData userData, String cancelMessage) {
        return HandlerUtils.handleCancelButton(callbackData, callbackQueryId, chatId, userData,
                getMessageSender(), getGameService(), cancelMessage);
    }

    // endregion

    // region Обновление зависимостей

    /**
     * Обновляет зависимости сервисов
     *
     * @param newDependencies новые зависимости
     */
    public void updateDependencies(BotDependencies newDependencies) {
        this.dependencies = newDependencies;
    }

    /**
     * Обновляет контекст бота
     *
     * @param newContext новый контекст бота
     */
    public void updateBotContext(BotContext newContext) {
        this.botContext = newContext;
    }

    // endregion

    // region Реализация по умолчанию

    /**
     * Обработка текстовых сообщений по умолчанию
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param message полученное сообщение
     */
    @Override
    public void handleMessage(Long chatId, BotUserData userData, org.telegram.telegrambots.meta.api.objects.Message message) {
        getMessageSender().sendMessage(chatId, "Это состояние не поддерживает текстовые сообщения");
    }

    /**
     * Обработка callback-запросов по умолчанию
     *
     * @param chatId идентификатор чата
     * @param userData данные пользователя
     * @param callbackQuery callback-запрос
     */
    @Override
    public void handleCallbackQuery(Long chatId, BotUserData userData, org.telegram.telegrambots.meta.api.objects.CallbackQuery callbackQuery) {
        answerCallbackQuery(callbackQuery.getId(), "Действие не поддерживается в текущем состоянии");
        getMessageSender().sendMessage(chatId, "Пожалуйста, используйте кнопки меню для навигации.");
    }

    // endregion
}