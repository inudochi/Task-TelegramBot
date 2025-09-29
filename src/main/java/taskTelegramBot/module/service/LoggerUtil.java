package taskTelegramBot.module.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Утилитарный класс для логирования событий приложения
 *
 * <p>Обеспечивает запись логов в файл и вывод в консоль с поддержкой
 * различных уровней логирования и форматирования сообщений.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class LoggerUtil {
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = "application.log";
    private static final Path LOG_PATH = Paths.get(LOG_DIR, LOG_FILE);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Уровни логирования поддерживаемые системой
     */
    public enum Level {
        INFO("INFO"), WARN("WARN"), ERROR("ERROR"), DEBUG("DEBUG");

        private final String levelName;

        Level(String levelName) {
            this.levelName = levelName;
        }

        /**
         * Возвращает текстовое представление уровня логирования
         *
         * @return название уровня логирования
         */
        public String getLevelName() {
            return levelName;
        }
    }

    static {
        initializeLogger();
    }

    /**
     * Инициализирует систему логирования при загрузке класса
     */
    private static void initializeLogger() {
        try {
            // Создаем директорию для логов если не существует
            if (!Files.exists(Paths.get(LOG_DIR))) {
                Files.createDirectories(Paths.get(LOG_DIR));
            }

            // Создаем файл лога если не существует
            if (!Files.exists(LOG_PATH)) {
                Files.createFile(LOG_PATH);
            }

            log(Level.INFO, "LoggerUtil initialized", null);
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    /**
     * Основной метод логирования с поддержкой исключений
     *
     * @param level уровень логирования
     * @param message текст сообщения
     * @param throwable исключение для логирования (может быть null)
     */
    public static void log(Level level, String message, Throwable throwable) {
        String logEntry = createLogEntry(level, message, throwable);

        // Вывод в консоль
        if (level == Level.ERROR) {
            System.err.println(logEntry);
        } else {
            System.out.println(logEntry);
        }

        // Запись в файл
        writeToFile(logEntry);
    }

    /**
     * Создает форматированную запись лога
     *
     * @param level уровень логирования
     * @param message текст сообщения
     * @param throwable исключение для включения в лог
     * @return форматированная строка лога
     */
    private static String createLogEntry(Level level, String message, Throwable throwable) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        String threadName = Thread.currentThread().getName();

        StringBuilder entry = new StringBuilder();
        entry.append(String.format("[%s] %s %s - %s",
                timestamp, level.getLevelName(), threadName, message));

        if (throwable != null) {
            entry.append("\n").append(getStackTrace(throwable));
        }

        return entry.toString();
    }

    /**
     * Форматирует стектрейс исключения для записи в лог
     *
     * @param throwable исключение для форматирования
     * @return строка с форматированным стектрейсом
     */
    private static String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage()).append("\n");

        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }

        // Если есть cause, добавляем и его
        Throwable cause = throwable.getCause();
        if (cause != null) {
            sb.append("Caused by: ").append(getStackTrace(cause));
        }

        return sb.toString();
    }

    /**
     * Записывает запись лога в файл
     *
     * @param logEntry строка лога для записи
     */
    private static void writeToFile(String logEntry) {
        try {
            Files.write(LOG_PATH, (logEntry + "\n").getBytes(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    // Методы для удобного использования

    /**
     * Логирует информационное сообщение
     *
     * @param message текст сообщения
     */
    public static void info(String message) {
        log(Level.INFO, message, null);
    }

    /**
     * Логирует предупреждающее сообщение
     *
     * @param message текст сообщения
     */
    public static void warn(String message) {
        log(Level.WARN, message, null);
    }

    /**
     * Логирует сообщение об ошибке
     *
     * @param message текст сообщения
     */
    public static void error(String message) {
        log(Level.ERROR, message, null);
    }

    /**
     * Логирует сообщение об ошибке с исключением
     *
     * @param message текст сообщения
     * @param throwable исключение для логирования
     */
    public static void error(String message, Throwable throwable) {
        log(Level.ERROR, message, throwable);
    }

    /**
     * Логирует отладочное сообщение
     *
     * @param message текст сообщения
     */
    public static void debug(String message) {
        log(Level.DEBUG, message, null);
    }
}
