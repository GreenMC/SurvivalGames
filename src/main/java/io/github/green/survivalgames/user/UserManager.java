package io.github.green.survivalgames.user;

import io.github.green.survivalgames.ConfigPreferences;
import io.github.green.survivalgames.Main;
import io.github.green.survivalgames.api.StatsStorage;
import io.github.green.survivalgames.user.data.FileStats;
import io.github.green.survivalgames.user.data.MysqlManager;
import io.github.green.survivalgames.user.data.UserDatabase;
import me.despical.commons.util.LogUtils;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Despical
 * <p>
 * Created at 29.01.2022
 */
public class UserManager {

	private final Set<User> users;
	private final UserDatabase database;

	public UserManager(Main plugin) {
		this.users = new HashSet<>();
		this.database = plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED) ? new MysqlManager() : new FileStats();

		plugin.getServer().getOnlinePlayers().stream().map(this::getUser).forEach(this::loadStatistics);
	}

	public User getUser(Player player) {
		UUID uuid = player.getUniqueId();

		for (User user : users) {
			if (user.getUniqueId().equals(uuid)) {
				return user;
			}
		}

		LogUtils.log("Registering new user {0} ({1})", uuid, player.getName());

		User user = new User(player);
		users.add(user);
		return user;
	}

//	public Set<User> getUsers(Arena arena) {
//		return arena.getPlayers().stream().map(this::getUser).collect(Collectors.toSet());
//	}

	public void saveStatistic(User user, StatsStorage.StatisticType stat) {
		if (!stat.isPersistent()) {
			return;
		}

		database.saveStatistic(user, stat);
	}

	public void saveAllStatistic(User user) {
		database.saveAllStatistic(user);
	}

	public void loadStatistics(User user) {
		database.loadStatistics(user);
	}

	public void removeUser(Player player) {
		users.remove(getUser(player));
	}

	public UserDatabase getDatabase() {
		return database;
	}
}