package taskTelegramBot.module.dao;

import java.time.LocalDateTime;

/**
 * Класс-сущность, представляющий игровую запись в коллекции
 *
 * <p>Содержит информацию об игре: название, жанр, количество игроков,
 * статус активности и дату последней игровой сессии.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class Game {
    private int id;
    private String title;
    private String genre;
    private int minPlayers;
    private int maxPlayers;
    private String status;
    private LocalDateTime lastPlayed;

    /**
     * Конструктор по умолчанию, инициализирует статус "Активно"
     */
    public Game() {
        this.status = "Активно"; // Устанавливаем значение по умолчанию
    }

    /**
     * Конструктор с параметрами для создания новой игры
     *
     * @param id уникальный идентификатор игры
     * @param title название игры
     * @param genre жанр игры
     * @param minPlayers минимальное количество игроков
     * @param maxPlayers максимальное количество игроков
     */
    public Game(int id, String title, String genre, int minPlayers, int maxPlayers) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.status = "Активно";
    }

    //Геттеры и сеттеры

    /**
     * Возвращает уникальный идентификатор игры
     *
     * @return идентификатор игры
     */
    public int getId() { return id; }

    /**
     * Устанавливает уникальный идентификатор игры
     *
     * @param id идентификатор игры
     */
    public void setId(int id) { this.id = id; }

    /**
     * Возвращает название игры
     *
     * @return название игры
     */
    public String getTitle() { return title; }

    /**
     * Устанавливает название игры
     *
     * @param title название игры
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Возвращает жанр игры
     *
     * @return жанр игры
     */
    public String getGenre() { return genre; }

    /**
     * Устанавливает жанр игры
     *
     * @param genre жанр игры
     */
    public void setGenre(String genre) { this.genre = genre; }

    /**
     * Возвращает минимальное количество игроков
     *
     * @return минимальное количество игроков
     */
    public int getMinPlayers() { return minPlayers; }

    /**
     * Устанавливает минимальное количество игроков
     *
     * @param minPlayers минимальное количество игроков
     */
    public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers; }

    /**
     * Возвращает максимальное количество игроков
     *
     * @return максимальное количество игроков
     */
    public int getMaxPlayers() { return maxPlayers; }

    /**
     * Устанавливает максимальное количество игроков
     *
     * @param maxPlayers максимальное количество игроков
     */
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    /**
     * Возвращает статус активности игры
     *
     * @return статус игры ("Активно" или "Неактивно")
     */
    public String getStatus() { return status; }

    /**
     * Устанавливает статус активности игры
     *
     * @param status статус игры
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Возвращает дату и время последней игровой сессии
     *
     * @return дата последней сессии или null если игра не игралась
     */
    public LocalDateTime getLastPlayed() { return lastPlayed; }

    /**
     * Устанавливает дату и время последней игровой сессии
     *
     * @param lastPlayed дата последней сессии
     */
    public void setLastPlayed(LocalDateTime lastPlayed) { this.lastPlayed = lastPlayed; }

    // endregion

    /**
     * Регистрирует текущую дату и время как время последней игровой сессии
     */
    public void logPlaySession() {
        this.lastPlayed = LocalDateTime.now();
    }

    /**
     * Возвращает строковое представление объекта игры
     *
     * @return строковое представление игры
     */
    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", minPlayers=" + minPlayers +
                ", maxPlayers=" + maxPlayers +
                ", status='" + status + '\'' +
                ", lastPlayed=" + lastPlayed +
                '}';
    }
}