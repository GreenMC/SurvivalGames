package io.github.green.survivalgames.user.data;

import io.github.green.survivalgames.api.StatsStorage;
import io.github.green.survivalgames.user.User;
import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.database.MysqlDatabase;
import me.despical.commons.util.LogUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Despical
 * <p>
 * Created at 29.01.2022
 */
public class MysqlManager implements UserDatabase {

	private final MysqlDatabase database;
	private final String tableName;

	public MysqlManager() {
		this.database = plugin.getMysqlDatabase();
		this.tableName = ConfigUtils.getConfig(plugin, "mysql").getString("table", "playerstats");

		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			try (Connection connection = database.getConnection()) {
				Statement statement = connection.createStatement();
				statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tableName + "` (\n"
						+ "  `UUID` char(36) NOT NULL PRIMARY KEY,\n"
						+ "  `name` varchar(32) NOT NULL,\n"
						+ "  `kills` int(11) NOT NULL DEFAULT '0',\n"
						+ "  `deaths` int(11) NOT NULL DEFAULT '0',\n"
						+ "  `highestscore` int(11) NOT NULL DEFAULT '0',\n"
						+ "  `gamesplayed` int(11) NOT NULL DEFAULT '0',\n"
						+ "  `wins` int(11) NOT NULL DEFAULT '0',\n"
						+ "  `loses` int(11) NOT NULL DEFAULT '0'\n" + ");");
			} catch (SQLException e) {
				e.printStackTrace();
				LogUtils.sendConsoleMessage("&cCouldn't save user statistics to MySQL database!");
				LogUtils.sendConsoleMessage("&cCheck your configuration or disable MySQL option in config.yml");
			}
		});
	}

	@Override
	public void saveStatistic(User user, StatsStorage.StatisticType stat) {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			String query = "UPDATE " + tableName + " SET " + stat.getName() + "=" + user.getStat(stat) + " WHERE UUID='" + user.getUniqueId().toString() + "';";
			database.executeUpdate(query);
			LogUtils.log("Executed MySQL: " + query);
		});
	}

	@Override
	public void saveAllStatistic(User user) {
		StringBuilder update = new StringBuilder(" SET ");

		for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
			if (!stat.isPersistent()) continue;
			String name = stat.getName();
			int val = user.getStat(stat);

			if (update.toString().equalsIgnoreCase(" SET ")) {
				update.append(name).append("=").append(val);
			}

			update.append(", ").append(name).append("=").append(val);
		}

		String finalUpdate = update.toString();
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> database.executeUpdate("UPDATE " + tableName + finalUpdate + " WHERE UUID='" + user.getUniqueId().toString() + "';"));
	}

	@Override
	public void loadStatistics(User user) {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			String uuid = user.getUniqueId().toString(), playerName = user.getPlayer().getName();

			try (Connection connection = database.getConnection()) {
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery("SELECT * from " + tableName + " WHERE UUID='" + uuid + "';");

				if (result.next()) {
					LogUtils.log("MySQL Stats | Player {0} already exist. Getting Stats...", playerName);

					for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
						if (!stat.isPersistent()) continue;
						user.setStat(stat, result.getInt(stat.getName()));
					}
				} else {
					LogUtils.log("MySQL Stats | Player {0} does not exist. Creating new one...", playerName);
					statement.executeUpdate("INSERT INTO " + tableName + " (UUID,name) VALUES ('" + uuid + "','" + playerName + "');");

					for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
						if (!stat.isPersistent()) continue;

						user.setStat(stat, 0);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	public String getTableName() {
		return tableName;
	}

	public MysqlDatabase getDatabase() {
		return database;
	}
}