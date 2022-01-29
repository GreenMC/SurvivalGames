package io.github.green.survivalgames.api;

import io.github.green.survivalgames.ConfigPreferences;
import io.github.green.survivalgames.Main;
import io.github.green.survivalgames.user.data.MysqlManager;
import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.sorter.SortUtils;
import me.despical.commons.util.LogUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Despical
 * <p>
 * Created at 29.01.2022
 */
public class StatsStorage {

	private static final Main plugin = JavaPlugin.getPlugin(Main.class);

	public static Map<UUID, Integer> getStats(StatisticType stat) {
		if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
			try (Connection connection = plugin.getMysqlDatabase().getConnection()) {
				Statement statement = connection.createStatement();
				ResultSet set = statement.executeQuery("SELECT UUID, " + stat.getName() + " FROM " + ((MysqlManager) plugin.getUserManager().getDatabase()).getTableName() + " ORDER BY " + stat.getName());
				Map<UUID, Integer> column = new HashMap<>();

				while (set.next()) {
					column.put(UUID.fromString(set.getString("UUID")), set.getInt(stat.getName()));
				}

				return column;
			} catch (SQLException exception) {
				plugin.getLogger().log(Level.WARNING, "SQL Exception occurred! " + exception.getSQLState() + " (" + exception.getErrorCode() + ")");
				LogUtils.sendConsoleMessage("Cannot get contents from MySQL database!");
				LogUtils.sendConsoleMessage("Check configuration of mysql.yml file or disable it in config.yml");
				return null;
			}
		}

		FileConfiguration config = ConfigUtils.getConfig(plugin, "stats");
		Map<UUID, Integer> stats = new HashMap<>();

		for (String string : config.getKeys(false)) {
			stats.put(UUID.fromString(string), config.getInt(string + "." + stat.getName()));
		}

		return SortUtils.sortByValue(stats);
	}

	public static int getUserStats(Player player, StatisticType statisticType) {
		return plugin.getUserManager().getUser(player).getStat(statisticType);
	}

	public enum StatisticType {
		KILLS("kills"), DEATHS("deaths"), GAMES_PLAYED("gamesplayed"), HIGHEST_SCORE("highestscore"),
		LOSES("loses"), WINS("wins"), LOCAL_KILLS("local_kills", false), LOCAL_DEATHS("local_deaths", false),
		LOCAL_KILL_STREAK("local_kill_streak", false);

		private final String name;
		private final boolean persistent;

		StatisticType(String name) {
			this(name, true);
		}

		StatisticType(String name, boolean persistent) {
			this.name = name;
			this.persistent = persistent;
		}

		public String getName() {
			return name;
		}

		public boolean isPersistent() {
			return persistent;
		}
	}
}