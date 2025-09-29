package taskTelegramBot.module.bot;

import taskTelegramBot.module.dao.Game;
import taskTelegramBot.module.dao.GameDaoFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для хранения данных пользователя во время сессии с ботом
 *
 * <p>Содержит текущее состояние пользователя, выбранные данные
 * и временные переменные, необходимые для многошаговых операций.
 * Обеспечивает сохранение контекста между сообщениями пользователя.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class BotUserData {
    private BotState state;
    private GameDaoFactory.DataSourceType dataSource;
    private Game currentGame;
    private Integer selectedGameId;
    private LocalDateTime plannedSession;
    private Map<String, Object> tempData = new HashMap<>();

    /**
     * Конструктор данных пользователя
     *
     * @param state начальное состояние пользователя
     */
    public BotUserData(BotState state) {
        this.state = state;
    }

    // region Методы для работы с временными данными

    /**
     * Сохраняет временные данные по ключу
     *
     * @param key ключ для сохранения данных
     * @param value сохраняемое значение
     */
    public void setTempData(String key, Object value) {
        tempData.put(key, value);
    }

    /**
     * Возвращает временные данные по ключу
     *
     * @param key ключ для получения данных
     * @return сохраненное значение или null если ключ не найден
     */
    public Object getTempData(String key) {
        return tempData.get(key);
    }

    /**
     * Возвращает временные строковые данные по ключу
     *
     * @param key ключ для получения данных
     * @return сохраненная строка или null если ключ не найден
     */
    public String getTempString(String key) {
        return (String) tempData.get(key);
    }

    /**
     * Возвращает временные целочисленные данные по ключу
     *
     * @param key ключ для получения данных
     * @return сохраненное число или null если ключ не найден
     */
    public Integer getTempInt(String key) {
        return (Integer) tempData.get(key);
    }

    /**
     * Очищает все временные данные
     */
    public void clearTempData() {
        tempData.clear();
    }

    /**
     * Проверяет наличие временных данных по ключу
     *
     * @param key ключ для проверки
     * @return true если данные существуют, false в противном случае
     */
    public boolean hasTempData(String key) {
        return tempData.containsKey(key);
    }

    // endregion

    // region Стандартные геттеры/сеттеры

    /**
     * Возвращает текущее состояние пользователя
     *
     * @return текущее состояние
     */
    public BotState getState() { return state; }

    /**
     * Устанавливает текущее состояние пользователя
     *
     * @param state новое состояние
     */
    public void setState(BotState state) { this.state = state; }

    /**
     * Возвращает выбранный источник данных
     *
     * @return тип источника данных
     */
    public GameDaoFactory.DataSourceType getDataSource() { return dataSource; }

    /**
     * Устанавливает источник данных
     *
     * @param dataSource тип источника данных
     */
    public void setDataSource(GameDaoFactory.DataSourceType dataSource) { this.dataSource = dataSource; }

    /**
     * Возвращает текущую редактируемую игру
     *
     * @return текущая игра или null
     */
    public Game getCurrentGame() { return currentGame; }

    /**
     * Устанавливает текущую редактируемую игру
     *
     * @param currentGame игра для редактирования
     */
    public void setCurrentGame(Game currentGame) { this.currentGame = currentGame; }

    /**
     * Возвращает идентификатор выбранной игры
     *
     * @return идентификатор игры или null
     */
    public Integer getSelectedGameId() { return selectedGameId; }

    /**
     * Устанавливает идентификатор выбранной игры
     *
     * @param selectedGameId идентификатор игры
     */
    public void setSelectedGameId(Integer selectedGameId) { this.selectedGameId = selectedGameId; }

    /**
     * Возвращает запланированное время сессии
     *
     * @return время сессии или null
     */
    public LocalDateTime getPlannedSession() { return plannedSession; }

    /**
     * Устанавливает время запланированной сессии
     *
     * @param plannedSession время сессии
     */
    public void setPlannedSession(LocalDateTime plannedSession) { this.plannedSession = plannedSession; }

    // endregion
}