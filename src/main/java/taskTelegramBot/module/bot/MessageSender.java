package taskTelegramBot.module.bot;

import taskTelegramBot.module.dao.Game;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для отправки сообщений и управления взаимодействием с пользователем
 *
 * <p>Обеспечивает отправку различных типов сообщений в Telegram чат,
 * включая текстовые сообщения, сообщения с клавиатурами и ответы
 * на callback-запросы. Содержит методы для формирования основных
 * экранов интерфейса бота.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class MessageSender {
    private final TelegramLongPollingBot bot;
    private final KeyboardFactory keyboardFactory;

    /**
     * Конструктор инициализирует отправитель сообщений с ботом
     *
     * @param bot экземпляр Telegram бота для отправки сообщений
     */
    public MessageSender(TelegramLongPollingBot bot) {
        this.bot = bot;
        this.keyboardFactory = new KeyboardFactory();
    }

    /**
     * Возвращает экземпляр бота
     *
     * @return экземпляр TelegramLongPollingBot
     */
    public TelegramLongPollingBot getBot() {
        return bot;
    }

    /**
     * Отправляет простое текстовое сообщение
     *
     * @param chatId идентификатор чата
     * @param text текст сообщения
     */
    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        executeMessage(message);
    }

    /**
     * Отправляет сообщение с inline-клавиатурой
     *
     * @param chatId идентификатор чата
     * @param text текст сообщения
     * @param keyboard inline-клавиатура
     */
    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);
        executeMessage(message);
    }

    /**
     * Отправляет приветственное сообщение с выбором источника данных
     *
     * @param chatId идентификатор чата
     */
    public void sendWelcomeMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Добро пожаловать в Game Manager Bot!\n\nВыберите источник данных:");
        message.setReplyMarkup(keyboardFactory.createSourceSelectionKeyboard());
        executeMessage(message);
    }

    /**
     * Отправляет главное меню со списком игр
     *
     * @param chatId идентификатор чата
     * @param games список игр для отображения
     */
    public void sendMainMenu(Long chatId, List<Game> games) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(formatGamesList(games));
        message.setReplyMarkup(createMainMenuKeyboard());
        executeMessage(message);
    }

    /**
     * Отправляет экран выбора жанра
     *
     * @param chatId идентификатор чата
     */
    public void sendGenreSelection(Long chatId) {
        sendMessage(chatId, "Выберите жанр:", keyboardFactory.createGenreSelectionKeyboard());
    }

    /**
     * Отправляет список игр с действиями
     *
     * @param chatId идентификатор чата
     * @param games список игр
     * @param actionPrefix префикс для callback-данных
     */
    public void sendGamesList(Long chatId, List<Game> games, String actionPrefix) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Выберите игру:");
        message.setReplyMarkup(keyboardFactory.createGamesListKeyboard(games, actionPrefix));
        executeMessage(message);
    }

    /**
     * Отправляет клавиатуру подтверждения действия
     *
     * @param chatId идентификатор чата
     * @param messageText текст сообщения
     */
    public void sendConfirmationKeyboard(Long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(messageText);
        message.setReplyMarkup(getKeyboardFactory().createConfirmationKeyboard());
        executeMessage(message);
    }

    /**
     * Отправляет экран выбора источника данных
     *
     * @param chatId идентификатор чата
     */
    public void sendSourceSelection(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Выберите источник данных для хранения игр:");
        message.setReplyMarkup(keyboardFactory.createSourceSelectionKeyboard());
        executeMessage(message);
    }

    /**
     * Форматирует список игр для отображения
     *
     * @param games список игр
     * @return отформатированная строка со списком игр
     */
    private String formatGamesList(List<Game> games) {
        if (games == null || games.isEmpty()) {
            return "Ваша коллекция игр пуста.\n\nВыберите действие:";
        }

        StringBuilder text = new StringBuilder();
        text.append("Ваши игры (").append(games.size()).append("):\n\n");

        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            text.append(i+1).append(". ").append(game.getTitle())
                    .append(" (").append(game.getGenre()).append(")\n");
            text.append("   ").append(game.getMinPlayers()).append("-").append(game.getMaxPlayers())
                    .append(" игроков | Статус: ").append(game.getStatus()).append("\n\n");
        }
        text.append("Выберите действие:");
        return text.toString();
    }

    /**
     * Создает клавиатуру главного меню
     *
     * @return клавиатура главного меню
     */
    private ReplyKeyboardMarkup createMainMenuKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Добавить игру");
        row1.add("Редактировать игру");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Удалить игру");
        row2.add("Запланировать сессию");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("Случайная игра");
        row3.add("Обновить статусы");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("Сменить источник");

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    /**
     * Создает inline-кнопку
     *
     * @param text текст кнопки
     * @param callbackData данные для callback
     * @return inline-кнопка
     */
    private InlineKeyboardButton createInlineButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    /**
     * Выполняет отправку сообщения
     *
     * @param message сообщение для отправки
     */
    private void executeMessage(SendMessage message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }

    /**
     * Отправляет ответ на callback-запрос
     *
     * @param callbackQueryId идентификатор callback-запроса
     * @param text текст ответа
     */
    public void answerCallbackQuery(String callbackQueryId, String text) {
        try {
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQueryId);
            answer.setText(text);
            answer.setShowAlert(false);
            bot.execute(answer);
        } catch (Exception e) {
            System.err.println("Ошибка ответа на callback: " + e.getMessage());
        }
    }

    /**
     * Возвращает фабрику клавиатур
     *
     * @return фабрика клавиатур
     */
    public KeyboardFactory getKeyboardFactory() {
        return keyboardFactory;
    }
}