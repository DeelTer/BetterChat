package ru.deelter.chat.language;

import ru.deelter.chat.bukkit.BetterChat;

public class SQLiteStorage extends SQLStorage {
    @Override
    protected String getJdbcUrl() {
        return "jdbc:sqlite:" + BetterChat.getInstance().getDataFolder() + "/languages.db";
    }

    @Override
    protected String getCreateTableQuery() {
        return "CREATE TABLE IF NOT EXISTS player_languages (" +
                "player_id VARCHAR(36) NOT NULL PRIMARY KEY," +
                "speaker_language VARCHAR(8)," +
                "known_languages TEXT NOT NULL," +
                "primary_language VARCHAR(8)," +
                "toggle_mode INTEGER NOT NULL DEFAULT 0" +
                ");";
    }
}