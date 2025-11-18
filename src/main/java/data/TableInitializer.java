package data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class TableInitializer {
    private static final String DDL = """
        PRAGMA foreign_keys = ON;
        CREATE TABLE IF NOT EXISTS users (
            id            INTEGER PRIMARY KEY AUTOINCREMENT,
            username      TEXT NOT NULL UNIQUE,
            password      TEXT NOT NULL,
            created_at    TEXT NOT NULL DEFAULT (datetime('now'))
        );
        CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
        
        CREATE TABLE IF NOT EXISTS expenses (
            id          INTEGER PRIMARY KEY AUTOINCREMENT,
            username    TEXT NOT NULL,
            datetime    TEXT NOT NULL,
            type        TEXT NOT NULL,
            amount      REAL NOT NULL
        );
        CREATE INDEX IF NOT EXISTS idx_expenses_username ON expenses(username);
        """;

    public static void ensureSchema(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(DDL);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize new database schema!", e);
        }
    }
}
