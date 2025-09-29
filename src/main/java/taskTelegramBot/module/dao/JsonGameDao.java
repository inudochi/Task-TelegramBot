package taskTelegramBot.module.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static taskTelegramBot.module.service.LoggerUtil.*;

/**
 * Реализация DAO для хранения данных об играх в JSON-файле
 *
 * <p>Обеспечивает сохранение и загрузку данных об играх в формате JSON,
 * включая поддержку работы с датами и обработку ошибок файловой системы.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class JsonGameDao implements GameDao {
    private static final String FILE_PATH = "games.json";
    private final ObjectMapper objectMapper;
    private final File dataFile;

    /**
     * Конструктор инициализирует JSON DAO и проверяет наличие файла данных
     */
    public JsonGameDao() {
        info("Initializing JSON DAO, file: " + FILE_PATH);
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.dataFile = new File(FILE_PATH);
        initializeDataFile();
    }

    /**
     * Инициализирует файл данных: создает новый или проверяет существующий
     *
     * @throws RuntimeException если инициализация файла не удалась
     */
    private void initializeDataFile() {
        try {
            if (!dataFile.exists()) {
                info("JSON file doesn't exist, creating new: " + FILE_PATH);
                dataFile.createNewFile();
                objectMapper.writeValue(dataFile, new ArrayList<Game>());
                info("Created new JSON file: " + FILE_PATH);
            } else {
                debug("JSON file already exists: " + FILE_PATH);
                // Проверяем, не corrupted ли файл
                if (dataFile.length() > 0) {
                    try {
                        // Пробуем прочитать чтобы проверить валидность
                        objectMapper.readValue(dataFile, new TypeReference<List<Game>>() {});
                        debug("JSON file is valid");
                    } catch (IOException e) {
                        warn("JSON file is corrupted, creating new one");
                        dataFile.delete();
                        dataFile.createNewFile();
                        objectMapper.writeValue(dataFile, new ArrayList<Game>());
                    }
                }
            }
        } catch (IOException e) {
            error("Error initializing JSON file", e);
            throw new RuntimeException("Failed to initialize data file", e);
        }
    }

    /**
     * Читает список игр из JSON-файла
     *
     * @return список игр из файла или пустой список при ошибке
     */
    private List<Game> readGamesFromFile() {
        try {
            if (dataFile.length() == 0) {
                debug("JSON file is empty, returning empty list");
                return new ArrayList<>();
            }

            List<Game> games = objectMapper.readValue(dataFile, new TypeReference<List<Game>>() {});
            debug("Loaded games from JSON: " + games.size());
            return games;
        } catch (IOException e) {
            error("Error reading from JSON file", e);

            // Попробуем прочитать как строку для диагностики
            try {
                String content = new String(Files.readAllBytes(dataFile.toPath()));
                debug("JSON file content: " + content);
            } catch (IOException ex) {
                debug("Could not read file content for diagnostics");
            }

            return new ArrayList<>();
        }
    }

    /**
     * Записывает список игр в JSON-файл
     *
     * @param games список игр для сохранения
     * @throws RuntimeException если запись в файл не удалась
     */
    private void writeGamesToFile(List<Game> games) {
        try {
            objectMapper.writeValue(dataFile, games);
            debug("Games written to JSON, count: " + games.size());
        } catch (IOException e) {
            error("Error writing to JSON file", e);
            throw new RuntimeException("Failed to write games to file", e);
        }
    }

    /**
     * Выводит в консоль информацию о содержимом JSON-файла для отладки
     */
    public void checkJsonFileContent() {
        try {
            System.out.println("Checking JSON file content:");
            System.out.println("Path: " + dataFile.getAbsolutePath());
            System.out.println("File size: " + dataFile.length() + " bytes");
            System.out.println("Exists: " + dataFile.exists());

            if (dataFile.exists() && dataFile.length() > 0) {
                String content = new String(Files.readAllBytes(dataFile.toPath()));
                System.out.println("Content: " + content);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    /**
     * Добавляет новую игру в JSON-файл
     *
     * @param game игра для добавления
     * @throws RuntimeException если добавление не удалось
     */
    @Override
    public void addGame(Game game) {
        try {
            info("Adding game to JSON: " + game.getTitle());
            List<Game> games = getAllGames();
            int newId = games.isEmpty() ? 1 : games.stream().mapToInt(Game::getId).max().getAsInt() + 1;
            game.setId(newId);
            games.add(game);
            writeGamesToFile(games);
            info("Game added to JSON, ID: " + newId + ", title: " + game.getTitle());

            // Проверяем, что запись прошла успешно
            checkJsonFileContent();

        } catch (Exception e) {
            error("Error adding game to JSON: " + game.getTitle(), e);
            throw new RuntimeException("JSON add error", e);
        }
    }

    /**
     * Возвращает список всех игр из JSON-файла
     *
     * @return список всех игр или пустой список при ошибке
     */
    @Override
    public List<Game> getAllGames() {
        debug("Loading all games from JSON");
        try {
            if (dataFile.length() == 0) {
                debug("JSON file is empty, returning empty list");
                return new ArrayList<>();
            }

            List<Game> games = objectMapper.readValue(dataFile, new TypeReference<List<Game>>() {});
            debug("Loaded games from JSON: " + games.size());

            // Логируем первые несколько игр для отладки
            if (!games.isEmpty()) {
                for (int i = 0; i < Math.min(3, games.size()); i++) {
                    Game game = games.get(i);
                    debug("Game #" + (i+1) + ": " + game.getTitle() + " (ID: " + game.getId() + ")");
                }
            }

            return games;
        } catch (IOException e) {
            warn("Error reading JSON file: " + e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            error("Unexpected error while reading JSON", e);
            return new ArrayList<>();
        }
    }

    /**
     * Находит игру по идентификатору в JSON-файле
     *
     * @param id идентификатор игры для поиска
     * @return найденная игра или null если игра не найдена
     */
    @Override
    public Game getGameById(int id) {
        debug("Searching for game in JSON by ID: " + id);
        return readGamesFromFile().stream()
                .filter(game -> game.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Обновляет информацию об игре в JSON-файле
     *
     * @param updatedGame игра с обновленными данными
     * @throws RuntimeException если обновление не удалось
     */
    @Override
    public void updateGame(Game updatedGame) {
        try {
            info("Updating game in JSON, ID: " + updatedGame.getId() + ", title: " + updatedGame.getTitle());
            List<Game> games = readGamesFromFile();
            games = games.stream()
                    .map(game -> game.getId() == updatedGame.getId() ? updatedGame : game)
                    .collect(Collectors.toList());
            writeGamesToFile(games);
            info("Game updated in JSON, ID: " + updatedGame.getId());
        } catch (Exception e) {
            error("Error updating game in JSON, ID: " + updatedGame.getId(), e);
            throw new RuntimeException("JSON update error", e);
        }
    }

    /**
     * Удаляет игру из JSON-файла по идентификатору
     *
     * @param id идентификатор игры для удаления
     * @throws RuntimeException если удаление не удалось
     */
    @Override
    public void deleteGame(int id) {
        try {
            info("Deleting game from JSON, ID: " + id);
            List<Game> games = readGamesFromFile();
            int initialSize = games.size();
            games.removeIf(game -> game.getId() == id);

            if (games.size() < initialSize) {
                writeGamesToFile(games);
                info("Game deleted from JSON, ID: " + id);
            } else {
                warn("Game not found when deleting from JSON, ID: " + id);
            }
        } catch (Exception e) {
            error("Error deleting game from JSON, ID: " + id, e);
            throw new RuntimeException("JSON delete error", e);
        }
    }

    /**
     * Выполняет поиск игр по запросу в JSON-файле
     *
     * @param query поисковый запрос
     * @return список игр, соответствующих запросу
     */
    @Override
    public List<Game> searchGames(String query) {
        debug("Searching games in JSON by query: " + query);
        String lowerCaseQuery = query.toLowerCase();
        List<Game> results = readGamesFromFile().stream()
                .filter(game -> game.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        game.getGenre().toLowerCase().contains(lowerCaseQuery))
                .collect(Collectors.toList());
        debug("Found games for query '" + query + "': " + results.size());
        return results;
    }

    /**
     * Возвращает игры указанного жанра, подходящие для заданного количества игроков
     *
     * @param genre жанр для фильтрации
     * @param players количество игроков
     * @return список игр, отсортированный по убыванию максимального количества игроков
     */
    @Override
    public List<Game> getGamesByGenreAndPlayers(String genre, int players) {
        debug("Searching games in JSON by genre: " + genre + ", players: " + players);
        List<Game> results = readGamesFromFile().stream()
                .filter(game -> game.getGenre().equalsIgnoreCase(genre))
                .filter(game -> game.getMaxPlayers() >= players)
                .sorted((g1, g2) -> Integer.compare(g2.getMaxPlayers(), g1.getMaxPlayers()))
                .collect(Collectors.toList());
        debug("Found games for genre '" + genre + "' with " + players + "+ players: " + results.size());
        return results;
    }
}