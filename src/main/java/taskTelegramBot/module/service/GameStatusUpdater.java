package taskTelegramBot.module.service;

import taskTelegramBot.module.dao.Game;
import java.time.LocalDateTime;

/**
 * Утилитарный класс для обновления статусов игр
 *
 * <p>Содержит логику определения активности игр на основе
 * времени последней игровой сессии. Устраняет дублирование кода
 * в различных частях приложения.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class GameStatusUpdater {

    /**
     * Приватный конструктор для предотвращения создания экземпляров
     */
    private GameStatusUpdater() {
        // utility class
    }

    /**
     * Обновляет статус игры на основе времени последней сессии
     *
     * <p>Игры, в которые не играли более 3 месяцев, помечаются как "Неактивно".
     * Все остальные игры получают статус "Активно".</p>
     *
     * @param game игра для обновления статуса
     */
    public static void updateGameStatus(Game game) {
        if (shouldBeInactive(game)) {
            game.setStatus("Неактивно");
        } else {
            game.setStatus("Активно");
        }
    }

    /**
     * Проверяет, должна ли игра быть помечена как неактивная
     *
     * <p>Игра считается неактивной если с момента последней
     * игровой сессии прошло более 3 месяцев</p>
     *
     * @param game игра для проверки
     * @return true если игра должна быть неактивной, false в противном случае
     */
    public static boolean shouldBeInactive(Game game) {
        return game.getLastPlayed() != null &&
                game.getLastPlayed().isBefore(LocalDateTime.now().minusMonths(3));
    }
}