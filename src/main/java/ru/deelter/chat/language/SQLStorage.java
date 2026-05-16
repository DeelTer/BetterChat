package ru.deelter.chat.language;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.deelter.chat.bukkit.BetterChat;

import java.sql.*;
import java.util.UUID;

public abstract class SQLStorage implements LanguageStorage {

	protected HikariDataSource dataSource;
	protected final Gson gson = new GsonBuilder().create();

	protected abstract String getJdbcUrl();

	protected abstract String getCreateTableQuery();

	@Override
	public void init() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(getJdbcUrl());
		config.setMaximumPoolSize(4);
		config.setMinimumIdle(1);
		config.setConnectionTimeout(5000);
		config.setPoolName("betterchat-lang");
		this.dataSource = new HikariDataSource(config);
		createTable();
	}

	private void createTable() {
		try (Connection conn = dataSource.getConnection();
		     Statement st = conn.createStatement()) {
			st.execute(getCreateTableQuery());
		} catch (SQLException e) {
			BetterChat.getInstance().getLogger().severe("Failed to create language table: " + e.getMessage());
		}
	}

	@Override
	public void shutdown() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
		}
	}

	@Override
	public @Nullable LanguagePreference load(@NotNull UUID playerId) {
		String sql = "SELECT speaker_language, known_languages, primary_language, toggle_mode FROM player_languages WHERE player_id = ?";
		try (Connection conn = dataSource.getConnection();
		     PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, playerId.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				LanguagePreference pref = new LanguagePreference();
				pref.setPlayerId(playerId);
				pref.setSpeakerLanguage(rs.getString("speaker_language"));
				pref.setKnownLanguages(gson.fromJson(rs.getString("known_languages"), new com.google.gson.reflect.TypeToken<java.util.List<String>>() {
				}.getType()));
				pref.setPrimaryLanguage(rs.getString("primary_language"));
				pref.setToggleMode(rs.getBoolean("toggle_mode"));
				return pref;
			}
		} catch (SQLException e) {
			BetterChat.getInstance().getLogger().warning("Failed to load language preference: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void save(@NotNull LanguagePreference pref) {
		// Пробуем UPDATE сначала, если не обновилось — INSERT
		String updateSql = "UPDATE player_languages SET speaker_language = ?, known_languages = ?, primary_language = ?, toggle_mode = ? WHERE player_id = ?";
		try (Connection conn = dataSource.getConnection();
		     PreparedStatement ps = conn.prepareStatement(updateSql)) {
			ps.setString(1, pref.getSpeakerLanguage());
			ps.setString(2, gson.toJson(pref.getKnownLanguages()));
			ps.setString(3, pref.getPrimaryLanguage());
			ps.setBoolean(4, pref.isToggleMode());
			ps.setString(5, pref.getPlayerId().toString());

			if (ps.executeUpdate() == 0) {
				// Записи нет — делаем INSERT
				String insertSql = "INSERT INTO player_languages (player_id, speaker_language, known_languages, primary_language, toggle_mode) VALUES (?, ?, ?, ?, ?)";
				try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
					insertPs.setString(1, pref.getPlayerId().toString());
					insertPs.setString(2, pref.getSpeakerLanguage());
					insertPs.setString(3, gson.toJson(pref.getKnownLanguages()));
					insertPs.setString(4, pref.getPrimaryLanguage());
					insertPs.setBoolean(5, pref.isToggleMode());
					insertPs.executeUpdate();
				}
			}
		} catch (SQLException e) {
			BetterChat.getInstance().getLogger().warning("Failed to save language preference: " + e.getMessage());
		}
	}

	@Override
	public void delete(@NotNull UUID playerId) {
		String sql = "DELETE FROM player_languages WHERE player_id = ?";
		try (Connection conn = dataSource.getConnection();
		     PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, playerId.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			BetterChat.getInstance().getLogger().warning("Failed to delete language preference: " + e.getMessage());
		}
	}
}