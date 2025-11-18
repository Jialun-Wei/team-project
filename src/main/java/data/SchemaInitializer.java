package data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class SchemaInitializer {
    private static final String DDL = """
        PRAGMA foreign_keys = ON;
        CREATE TABLE IF NOT EXISTS users (
          id            INTEGER PRIMARY KEY AUTOINCREMENT,
          username      TEXT NOT NULL UNIQUE,
          password      TEXT NOT NULL,
          created_at    TEXT NOT NULL DEFAULT (datetime('now'))
        );
        CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
        """;

    public static void ensureSchema(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(DDL);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize new database schema!", e);
        }
    }
}
