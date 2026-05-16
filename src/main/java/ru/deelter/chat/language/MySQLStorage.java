package ru.deelter.chat.language;

public class MySQLStorage extends SQLStorage {
    private final String host, database, user, password;
    private final int port;

    public MySQLStorage(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    @Override
    protected String getJdbcUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&characterEncoding=utf8";
    }

    @Override
    protected String getCreateTableQuery() {
        return "CREATE TABLE IF NOT EXISTS player_languages (" +
                "player_id VARCHAR(36) NOT NULL PRIMARY KEY," +
                "speaker_language VARCHAR(8)," +
                "known_languages TEXT NOT NULL," +
                "primary_language VARCHAR(8)," +
                "toggle_mode BOOLEAN NOT NULL DEFAULT FALSE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
    }
}