package taskTelegramBot.module.dao;

/**
 * Композитный интерфейс для операций с данными об играх
 *
 * <p>Объединяет функциональность чтения и записи данных,
 * предоставляя полный набор операций CRUD для управления играми.</p>
 *
 * @author Чекрыгин И.И.
 * @version 1.0
 */
public interface GameDao extends GameReader, GameWriter {
    // Наследует все методы из GameReader и GameWriter
}