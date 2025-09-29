package taskTelegramBot.module.dao;

import java.sql.SQLException;

/**
 * Фабрика для создания экземпляров DAO в зависимости от типа источника данных
 *
 * <p>Реализует паттерн Фабрика для инкапсуляции логики создания
 * объектов доступа к данным и обеспечения легкого переключения
 * между различными источниками хранения.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public class GameDaoFactory {

    /**
     * Перечисление поддерживаемых типов источников данных
     */
    public enum DataSourceType {JSON, POSTGRES }

    /**
     * Создает экземпляр DAO для указанного типа источника данных
     *
     * @param type тип источника данных
     * @return экземпляр GameDao для работы с указанным источником
     * @throws RuntimeException если тип источника неизвестен или произошла ошибка подключения
     */
    public static GameDao createDao(DataSourceType type) {
        switch (type) {
            case JSON:
                return new JsonGameDao();
            case POSTGRES:
                try {
                    return new PostgresGameDao();
                } catch (SQLException e) {
                    throw new RuntimeException("Ошибка подключения к PostgreSQL", e);
                }
            default:
                throw new IllegalArgumentException("Неизвестный тип источника");
        }
    }
}