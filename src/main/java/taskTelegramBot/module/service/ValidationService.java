package taskTelegramBot.module.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Сервис валидации входных данных приложения
 *
 * <p>Обеспечивает проверку корректности данных вводимых пользователем,
 * включая валидацию названий игр, жанров, количества игроков, дат и времени.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class ValidationService {

    /**
     * Список допустимых жанров игр
     */
    public static final List<String> VALID_GENRES = Arrays.asList(
            "Стратегия", "Ролевая игра", "Шутер", "Приключения",
            "Симулятор", "Гонки", "Головоломка", "Спортивная",
            "Файтинг", "Хоррор", "Песочница", "Выживание"
    );

    // region Существующие методы для desktop

    /**
     * Проверяет корректность даты сессии
     *
     * <p>Дата должна быть не раньше текущей даты</p>
     *
     * @param date дата для проверки
     * @return true если дата корректна, false в противном случае
     */
    public boolean validateDate(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    // endregion

    // region Новые методы для бота

    /**
     * Проверяет корректность строкового представления даты
     *
     * <p>Ожидает дату в формате "dd.MM.yyyy" и проверяет что она не в прошлом</p>
     *
     * @param dateText строковое представление даты
     * @return true если дата корректна, false в противном случае
     */
    public boolean validateDate(String dateText) {
        try {
            LocalDate date = parseDate(dateText);
            return validateDate(date); // Используем существующий метод
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Преобразует строку в объект LocalDate
     *
     * <p>Ожидает дату в формате "dd.MM.yyyy"</p>
     *
     * @param dateText строковое представление даты
     * @return объект LocalDate
     * @throws DateTimeParseException если строка имеет неверный формат
     */
    public LocalDate parseDate(String dateText) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(dateText, formatter);
    }

    /**
     * Проверяет корректность строкового представления времени
     *
     * <p>Ожидает время в формате "HH:mm"</p>
     *
     * @param timeText строковое представление времени
     * @return true если время корректно, false в противном случае
     */
    public boolean validateTime(String timeText) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime.parse(timeText, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Преобразует строку в объект LocalTime
     *
     * <p>Ожидает время в формате "HH:mm"</p>
     *
     * @param timeText строковое представление времени
     * @return объект LocalTime
     * @throws DateTimeParseException если строка имеет неверный формат
     */
    public LocalTime parseTime(String timeText) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeText, formatter);
    }

    // endregion

    // region Остальные существующие методы...

    /**
     * Проверяет корректность названия игры
     *
     * <p>Название должно содержать от 2 до 100 символов</p>
     *
     * @param title название игры для проверки
     * @return true если название корректно, false в противном случае
     */
    public boolean validateTitle(String title) {
        return title != null && title.length() >= 2 && title.length() <= 100;
    }

    /**
     * Проверяет корректность жанра игры
     *
     * <p>Жанр должен присутствовать в списке допустимых жанров</p>
     *
     * @param genre жанр игры для проверки
     * @return true если жанр корректен, false в противном случае
     */
    public boolean validateGenre(String genre) {
        if (genre == null) return false;
        for (String validGenre : VALID_GENRES) {
            if (validGenre.equalsIgnoreCase(genre)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверяет корректность минимального количества игроков
     *
     * <p>Минимальное количество игроков должно быть в диапазоне от 1 до 16</p>
     *
     * @param minPlayers минимальное количество игроков
     * @return true если количество корректно, false в противном случае
     */
    public boolean validateMinPlayers(int minPlayers) {
        return minPlayers >= 1 && minPlayers <= 16;
    }

    /**
     * Проверяет корректность максимального количества игроков
     *
     * <p>Максимальное количество игроков должно быть в диапазоне от 1 до 16
     * и не меньше минимального количества игроков</p>
     *
     * @param minPlayers минимальное количество игроков
     * @param maxPlayers максимальное количество игроков
     * @return true если количество корректно, false в противном случае
     */
    public boolean validateMaxPlayers(int minPlayers, int maxPlayers) {
        return maxPlayers >= 1 && maxPlayers <= 16 && maxPlayers >= minPlayers;
    }

    /**
     * Комплексная проверка всех параметров игры
     *
     * <p>Выполняет проверку названия, жанра, минимального и максимального
     * количества игроков одной операцией</p>
     *
     * @param title название игры
     * @param genre жанр игры
     * @param minPlayers минимальное количество игроков
     * @param maxPlayers максимальное количество игроков
     * @return true если все параметры корректны, false в противном случае
     */
    public boolean validateGameInput(String title, String genre, int minPlayers, int maxPlayers) {
        return validateTitle(title) &&
                validateGenre(genre) &&
                validateMinPlayers(minPlayers) &&
                validateMaxPlayers(minPlayers, maxPlayers);
    }

    // endregion
}