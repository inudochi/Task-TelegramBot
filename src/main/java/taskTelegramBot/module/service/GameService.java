package taskTelegramBot.module.service;

import taskTelegramBot.module.dao.Game;
import taskTelegramBot.module.dao.GameDao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static taskTelegramBot.module.service.LoggerUtil.debug;
import static taskTelegramBot.module.service.LoggerUtil.error;

/**
 * Основной сервисный класс для управления бизнес-логикой работы с играми
 *
 * <p>Инкапсулирует всю бизнес-логику приложения, включая CRUD-операции,
 * валидацию, фильтрацию и специализированные методы для работы с играми.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class GameService {
    private final GameDao gameDao;
    private final ValidationService validationService;

    /**
     * Конструктор инициализирует DAO и сервис валидации
     *
     * @param gameDao объект доступа к данным игр
     */
    public GameService(GameDao gameDao) {
        this.gameDao = gameDao;
        this.validationService = new ValidationService();
    }

    // region Основные CRUD операции

    /**
     * Добавляет новую игру в коллекцию
     *
     * @param game игра для добавления
     */
    public void addGame(Game game) {
        gameDao.addGame(game);
    }

    /**
     * Возвращает список всех игр с обновленными статусами
     *
     * <p>Автоматически обновляет статусы игр перед возвратом</p>
     *
     * @return список всех игр с актуальными статусами
     */
    public List<Game> getAllGames() {
        List<Game> games = gameDao.getAllGames();
        return updateGamesStatus(games); // Один вызов вместо forEach
    }

    /**
     * Находит игру по идентификатору с обновлением статуса
     *
     * @param id идентификатор игры для поиска
     * @return найденная игра с актуальным статусом или null если не найдена
     */
    public Game getGameById(int id) {
        Game game = gameDao.getGameById(id);
        if (game != null) {
            GameStatusUpdater.updateGameStatus(game);
        }
        return game;
    }

    /**
     * Обновляет информацию об игре с предварительным обновлением статуса
     *
     * @param game игра с обновленными данными
     */
    public void updateGame(Game game) {
        GameStatusUpdater.updateGameStatus(game);
        gameDao.updateGame(game);
    }

    /**
     * Удаляет игру из коллекции по идентификатору
     *
     * @param id идентификатор игры для удаления
     */
    public void deleteGame(int id) {
        gameDao.deleteGame(id);
    }

    // endregion

    // region Специализированные методы для бота (не используются в desktop)

    /**
     * Создает новую игру с полной валидацией данных
     *
     * <p>Используется преимущественно Telegram-ботом для создания игр
     * с предварительной проверкой всех входных параметров</p>
     *
     * @param title название игры
     * @param genre жанр игры
     * @param minPlayers минимальное количество игроков
     * @param maxPlayers максимальное количество игроков
     * @return созданная игра
     * @throws IllegalArgumentException если данные не прошли валидацию
     */
    public Game createGameWithValidation(String title, String genre, int minPlayers, int maxPlayers) {
        if (!validationService.validateGameInput(title, genre, minPlayers, maxPlayers)) {
            throw new IllegalArgumentException("Invalid game data");
        }
        Game game = new Game(0, title, genre, minPlayers, maxPlayers);
        gameDao.addGame(game);
        return game;
    }

    /**
     * Обновляет конкретное поле игры с валидацией
     *
     * <p>Позволяет обновлять отдельные поля игры с проверкой корректности
     * новых значений. Используется ботом для пошагового редактирования.</p>
     *
     * @param gameId идентификатор редактируемой игры
     * @param field название поля для обновления
     * @param newValue новое значение поля
     * @return обновленная игра
     * @throws IllegalArgumentException если игра не найдена или данные невалидны
     */
    public Game updateGameField(int gameId, String field, String newValue) {
        Game game = getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }

        switch (field) {
            case "title":
                if (!validationService.validateTitle(newValue)) {
                    throw new IllegalArgumentException("Title must contain 2 to 100 characters");
                }
                game.setTitle(newValue);
                break;
            case "genre":
                if (!validationService.validateGenre(newValue)) {
                    throw new IllegalArgumentException("Choose genre from allowed list");
                }
                game.setGenre(newValue);
                break;
            case "minPlayers":
                int minPlayers = Integer.parseInt(newValue);
                if (!validationService.validateMinPlayers(minPlayers)) {
                    throw new IllegalArgumentException("Minimum players must be between 1 and 16");
                }
                game.setMinPlayers(minPlayers);
                break;
            case "maxPlayers":
                int maxPlayers = Integer.parseInt(newValue);
                if (!validationService.validateMaxPlayers(game.getMinPlayers(), maxPlayers)) {
                    throw new IllegalArgumentException("Maximum players must be ≥ minimum and ≤ 16");
                }
                game.setMaxPlayers(maxPlayers);
                break;
            default:
                throw new IllegalArgumentException("Unknown field for editing");
        }

        updateGame(game);
        return game;
    }

    /**
     * Регистрирует игровую сессию для указанной игры
     *
     * <p>Устанавливает текущее время как время последней игровой сессии
     * и обновляет статус игры</p>
     *
     * @param gameId идентификатор игры
     * @throws IllegalArgumentException если игра не найдена
     */
    public void planSession(int gameId) {
        Game game = getGameById(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game not found");
        }
        game.logPlaySession();
        updateGame(game);
    }

    // endregion

    // region Поиск и фильтрация

    /**
     * Выполняет поиск игр по текстовому запросу
     *
     * <p>Поиск осуществляется по названию и жанру игры без учета регистра</p>
     *
     * @param query поисковый запрос
     * @return список найденных игр с обновленными статусами
     */
    public List<Game> searchGames(String query) {
        List<Game> results = gameDao.searchGames(query);
        return updateGamesStatus(results);
    }

    /**
     * Возвращает игры указанного жанра, подходящие для заданного количества игроков
     *
     * @param genre жанр для фильтрации
     * @param players количество игроков
     * @return список игр с обновленными статусами
     */
    public List<Game> getGamesByGenreAndPlayers(String genre, int players) {
        List<Game> results = gameDao.getGamesByGenreAndPlayers(genre, players);
        return updateGamesStatus(results);
    }

    /**
     * Возвращает список одиночных игр (для 1 игрока)
     *
     * @return список одиночных игр
     */
    public List<Game> getSinglePlayerGames() {
        return getAllGames().stream()
                .filter(g -> g.getMaxPlayers() == 1)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список многопользовательских игр (для 2+ игроков)
     *
     * @return список многопользовательских игр
     */
    public List<Game> getMultiplayerGames() {
        return getAllGames().stream()
                .filter(g -> g.getMaxPlayers() > 1)
                .collect(Collectors.toList());
    }

    // endregion

    // region Случайная игра

    /**
     * Возвращает случайную игру из коллекции
     *
     * <p>Предпочтение отдается играм, в которые не играли более 2 недель.
     * Если таких игр нет, возвращается любая случайная игра из коллекции.</p>
     *
     * @return случайная игра или null если коллекция пуста
     */
    public Game getRandomGame() {
        List<Game> games = getAllGames().stream()
                .filter(g -> g.getLastPlayed() == null ||
                        g.getLastPlayed().isBefore(LocalDateTime.now().minusWeeks(2)))
                .collect(Collectors.toList());

        if (games.isEmpty()) {
            List<Game> allGames = getAllGames();
            return allGames.isEmpty() ? null : allGames.get(new Random().nextInt(allGames.size()));
        }
        return games.get(new Random().nextInt(games.size()));
    }

    // endregion

    // region Служебные методы

    /**
     * Обновляет статусы всех игр в переданном списке
     *
     * @param games список игр для обновления статусов
     * @return список игр с обновленными статусами
     */
    private List<Game> updateGamesStatus(List<Game> games) {
        games.forEach(GameStatusUpdater::updateGameStatus);
        return games;
    }

    /**
     * Массово обновляет статусы всех игр в коллекции
     *
     * <p>Обходит все игры в базе данных и обновляет их статусы
     * в соответствии с временем последней игровой сессии.
     * Игры, в которые не играли более 3 месяцев, помечаются как "Неактивно".</p>
     *
     * @throws RuntimeException если обновление не удалось
     */
    public void updateAllGamesStatus() {
        try {
            List<Game> games = gameDao.getAllGames();
            int updatedCount = 0;

            for (Game game : games) {
                String oldStatus = game.getStatus();
                GameStatusUpdater.updateGameStatus(game);
                if (!game.getStatus().equals(oldStatus)) {
                    gameDao.updateGame(game);
                    updatedCount++;
                }
            }
            debug("Statuses updated: " + updatedCount);
        } catch (Exception e) {
            error("Failed to update game statuses", e);
            throw new RuntimeException("Failed to update game statuses", e);
        }
    }

    // endregion
}