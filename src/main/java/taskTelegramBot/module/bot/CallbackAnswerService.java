package taskTelegramBot.module.bot;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Сервис для ответов на callback запросы от inline-кнопок
 *
 * <p>Обеспечивает корректную обработку нажатий кнопок в Telegram,
 * включая отображение уведомлений и предотвращение "висящих" запросов.
 * Отвечает только за техническую сторону ответов на callback.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class CallbackAnswerService {
    private final AbsSender bot;

    /**
     * Конструктор сервиса ответов на callback-запросы
     *
     * @param bot экземпляр бота для отправки ответов
     */
    public CallbackAnswerService(AbsSender bot) {
        this.bot = bot;
    }

    /**
     * Отправляет ответ на callback-запрос без показа уведомления
     *
     * @param callbackQueryId идентификатор callback-запроса
     * @param text текст ответа (не показывается пользователю)
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
     * Отправляет ответ на callback-запрос с показом уведомления
     *
     * @param callbackQueryId идентификатор callback-запроса
     * @param text текст уведомления (показывается пользователю)
     */
    public void answerWithAlert(String callbackQueryId, String text) {
        try {
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            answer.setCallbackQueryId(callbackQueryId);
            answer.setText(text);
            answer.setShowAlert(true);
            bot.execute(answer);
        } catch (Exception e) {
            System.err.println("Ошибка ответа на callback: " + e.getMessage());
        }
    }
}