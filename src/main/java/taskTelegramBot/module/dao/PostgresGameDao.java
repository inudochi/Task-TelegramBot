package taskTelegramBot.module.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static taskTelegramBot.module.service.LoggerUtil.*;

/**
 * Реализация DAO для хранения данных об играх в базе данных PostgreSQL
 *
 * <p>Обеспечивает взаимодействие с реляционной базой данных PostgreSQL,
 * включая создание таблицы, выполнение SQL-запросов и обработку транзакций.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class PostgresGameDao implements GameDao {
    private final Connection connection;

    /**
     * Конструктор устанавливает соединение с PostgreSQL и создает таблицу при необходимости
     *
     * @throws SQLException если подключение к базе данных не удалось
     */
    public PostgresGameDao() throws SQLException {
        try {
            info("Connecting to PostgreSQL...");
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/games_db",
                    "postgres",
                    "1"
            );
            info("PostgreSQL connection established");
            createTable();
        } catch (SQLException e) {
            error("Error connecting to PostgreSQL", e);
            throw e;
        }
    }

    /**
     * Создает таблицу 'games' в базе данных если она не существует
     *
     * @throws SQLException если создание таблицы не удалось
     */
    private void createTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS games (" +
                    "id SERIAL PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "genre VARCHAR(50), " +
                    "min_players INT, " +
                    "max_players INT, " +
                    "status VARCHAR(20), " +
                    "last_played TIMESTAMP)");
            info("Table 'games' created or already exists");
        } catch (SQLException e) {
            error("Error creating table in PostgreSQL", e);
            throw e;
        }
    }

    /**
     * Добавляет новую игру в базу данных PostgreSQL
     *
     * @param game игра для добавления
     * @throws RuntimeException если добавление не удалось
     */
    @Override
    public void addGame(Game game) {
        String sql = "INSERT INTO games (title, genre, min_players, max_players, status, last_played) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, game.getTitle());
            ps.setString(2, game.getGenre());
            ps.setInt(3, game.getMinPlayers());
            ps.setInt(4, game.getMaxPlayers());
            ps.setString(5, game.getStatus());

            // Обрабатываем last_played при добавлении новой игры
            if (game.getLastPlayed() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(game.getLastPlayed()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    game.setId(rs.getInt(1));
                    info("Game added to PostgreSQL, ID: " + game.getId() + ", title: " + game.getTitle());
                }
            }
        } catch (SQLException e) {
            error("Error adding game to PostgreSQL: " + game.getTitle(), e);
            throw new RuntimeException("DB error", e);
        }
    }

    /**
     * Возвращает список всех игр из базы данных PostgreSQL
     *
     * @return список всех игр
     * @throws RuntimeException если загрузка не удалась
     */
    @Override
    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM games")) {
            while (rs.next()) games.add(mapRowToGame(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Error loading games", e);
        }
        return games;
    }

    /**
     * Находит игру по идентификатору в базе данных PostgreSQL
     *
     * @param id идентификатор игры для поиска
     * @return найденная игра или null если игра не найдена
     * @throws RuntimeException если поиск не удался
     */
    @Override
    public Game getGameById(int id) {
        String sql = "SELECT * FROM games WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToGame(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding game", e);
        }
        return null;
    }

    /**
     * Обновляет информацию об игре в базе данных PostgreSQL
     *
     * @param game игра с обновленными данными
     * @throws RuntimeException если обновление не удалось
     */
    @Override
    public void updateGame(Game game) {
        String sql = "UPDATE games SET title=?, genre=?, min_players=?, max_players=?, status=?, last_played=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, game.getTitle());
            ps.setString(2, game.getGenre());
            ps.setInt(3, game.getMinPlayers());
            ps.setInt(4, game.getMaxPlayers());
            ps.setString(5, game.getStatus());

            // Правильно обрабатываем last_played (может быть null)
            if (game.getLastPlayed() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(game.getLastPlayed()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            ps.setInt(7, game.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating game", e);
        }
    }

    /**
     * Удаляет игру из базы данных PostgreSQL по идентификатору
     *
     * @param id идентификатор игры для удаления
     * @throws RuntimeException если удаление не удалось
     */
    @Override
    public void deleteGame(int id) {
        String sql = "DELETE FROM games WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting game", e);
        }
    }

    /**
     * Выполняет поиск игр по запросу в базе данных PostgreSQL
     *
     * @param query поисковый запрос
     * @return список игр, соответствующих запросу
     * @throws RuntimeException если поиск не удался
     */
    @Override
    public List<Game> searchGames(String query) {
        String sql = "SELECT * FROM games WHERE title ILIKE ? OR genre ILIKE ?";
        List<Game> results = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRowToGame(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Search error", e);
        }
        return results;
    }

    /**
     * Возвращает игры указанного жанра, подходящие для заданного количества игроков
     *
     * @param genre жанр для фильтрации
     * @param players количество игроков
     * @return список игр
     * @throws RuntimeException если поиск не удался
     */
    @Override
    public List<Game> getGamesByGenreAndPlayers(String genre, int players) {
        String sql = "SELECT * FROM games WHERE genre = ? AND max_players >= ?";
        List<Game> results = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, genre);
            ps.setInt(2, players);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRowToGame(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Search error", e);
        }
        return results;
    }

    /**
     * Преобразует строку результата SQL-запроса в объект Game
     *
     * @param rs ResultSet с данными игры
     * @return объект Game
     * @throws SQLException если преобразование не удалось
     */
    private Game mapRowToGame(ResultSet rs) throws SQLException {
        Game game = new Game(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getInt("min_players"),
                rs.getInt("max_players")
        );

        // Обработка NULL для last_played
        Timestamp lastPlayedTimestamp = rs.getTimestamp("last_played");
        if (lastPlayedTimestamp != null) {
            game.setLastPlayed(lastPlayedTimestamp.toLocalDateTime());
        }

        game.setStatus(rs.getString("status"));
        return game;
    }
}