package taskTelegramBot.module.bot;

import taskTelegramBot.module.service.ValidationService;

/**
 * Специализированная валидация для бота (расширяет общий ValidationService)
 *
 * <p>Добавляет функциональность отправки сообщений об ошибках
 * пользователю непосредственно во время валидации. Интегрирует
 * валидацию данных с пользовательским интерфейсом бота.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class BotValidationService extends ValidationService {
    private final MessageSender messageSender;

    /**
     * Конструктор сервиса валидации для бота
     *
     * @param messageSender отправитель сообщений для уведомлений об ошибках
     */
    public BotValidationService(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    /**
     * Валидирует и преобразует строку в целое число с отправкой сообщения об ошибке
     *
     * @param input входная строка для преобразования
     * @param messageSender отправитель сообщений
     * @param chatId идентификатор чата для отправки сообщений об ошибках
     * @return преобразованное число или null если преобразование не удалось
     */
    public Integer validateAndParseInteger(String input, MessageSender messageSender, Long chatId) {
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            if (messageSender != null && chatId != null) {
                messageSender.sendMessage(chatId, "Введите корректное число");
            }
            return null;
        }
    }

    /**
     * Проверяет минимальное количество игроков с отправкой сообщения об ошибке
     *
     * @param minPlayers минимальное количество игроков
     * @param messageSender отправитель сообщений
     * @param chatId идентификатор чата для отправки сообщений об ошибках
     * @return true если количество корректно, false в противном случае
     */
    public boolean validateMinPlayers(int minPlayers, MessageSender messageSender, Long chatId) {
        if (!super.validateMinPlayers(minPlayers)) {
            if (messageSender != null && chatId != null) {
                messageSender.sendMessage(chatId, "Минимальное количество игроков должно быть от 1 до 16");
            }
            return false;
        }
        return true;
    }

    /**
     * Проверяет максимальное количество игроков с отправкой сообщения об ошибке
     *
     * @param minPlayers минимальное количество игроков
     * @param maxPlayers максимальное количество игроков
     * @param messageSender отправитель сообщений
     * @param chatId идентификатор чата для отправки сообщений об ошибках
     * @return true если количество корректно, false в противном случае
     */
    public boolean validateMaxPlayers(int minPlayers, int maxPlayers, MessageSender messageSender, Long chatId) {
        if (!super.validateMaxPlayers(minPlayers, maxPlayers)) {
            if (messageSender != null && chatId != null) {
                messageSender.sendMessage(chatId,
                        maxPlayers < minPlayers ?
                                "Максимальное количество игроков должно быть больше или равно минимальному" :
                                "Максимальное количество игроков должно быть от 1 до 16");
            }
            return false;
        }
        return true;
    }

    /**
     * Комплексная проверка всех параметров игры с отправкой сообщений об ошибках
     *
     * @param title название игры
     * @param genre жанр игры
     * @param minPlayers минимальное количество игроков
     * @param maxPlayers максимальное количество игроков
     * @param chatId идентификатор чата для отправки сообщений об ошибках
     * @return true если все параметры корректны, false в противном случае
     */
    public boolean validateGameInputWithMessages(String title, String genre, int minPlayers, int maxPlayers, Long chatId) {
        if (!validateTitle(title)) {
            messageSender.sendMessage(chatId, "Название игры должно содержать от 2 до 100 символов");
            return false;
        }
        if (!validateGenre(genre)) {
            messageSender.sendMessage(chatId, "Выберите жанр из допустимого списка");
            return false;
        }
        if (!validateMinPlayers(minPlayers, messageSender, chatId)) {
            return false;
        }
        if (!validateMaxPlayers(minPlayers, maxPlayers, messageSender, chatId)) {
            return false;
        }
        return true;
    }
}