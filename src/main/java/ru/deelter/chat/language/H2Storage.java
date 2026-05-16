package ru.deelter.chat.language;

import ru.deelter.chat.bukkit.BetterChat;

public class H2Storage extends SQLStorage {
    @Override
    protected String getJdbcUrl() {
        return "jdbc:h2:file:" + BetterChat.getInstance().getDataFolder() + "/languages;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false";
    }

    @Override
    protected String getCreateTableQuery() {
        return "CREATE TABLE IF NOT EXISTS player_languages (" +
                "player_id VARCHAR(36) NOT NULL PRIMARY KEY," +
                "speaker_language VARCHAR(8)," +
                "known_languages TEXT NOT NULL," +
                "primary_language VARCHAR(8)," +
                "toggle_mode BOOLEAN NOT NULL DEFAULT FALSE" +
                ");";
    }
}