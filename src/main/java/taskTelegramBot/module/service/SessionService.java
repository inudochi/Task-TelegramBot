package taskTelegramBot.module.service;

import taskTelegramBot.module.dao.Game;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления игровыми сессиями
 *
 * <p>Обеспечивает функциональность планирования будущих игровых сессий
 * и поиска игр, подходящих для определенного количества игроков.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class SessionService {
    private final GameService gameService;

    /**
     * Конструктор инициализирует сервис игр
     *
     * @param gameService сервис для работы с играми
     */
    public SessionService(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Планирует будущую игровую сессию для указанной игры
     *
     * <p>Устанавливает время последней игровой сессии в текущее время
     * и обновляет статус игры. В будущем может быть расширена для
     * отправки уведомлений и управления календарем сессий.</p>
     *
     * @param gameId идентификатор игры
     * @param sessionTime дата и время планируемой сессии
     * @throws IllegalArgumentException если игра не найдена или дата в прошлом
     */
    public void planFutureSession(int gameId, LocalDateTime sessionTime) {
        Game game = gameService.getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        if (sessionTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Session date cannot be in the past");
        }

        game.setLastPlayed(LocalDateTime.now());
        gameService.updateGame(game);
    }

    /**
     * Возвращает игры, подходящие для указанного количества игроков
     *
     * <p>Фильтрует игры по максимальному количеству игроков,
     * возвращая только те игры, в которые можно играть с заданным
     * количеством участников</p>
     *
     * @param players количество игроков
     * @return список подходящих игр
     */
    public List<Game> getGamesSuitableForPlayers(int players) {
        return gameService.getAllGames().stream()
                .filter(game -> game.getMaxPlayers() >= players)
                .collect(Collectors.toList());
    }
}