package io.github.green.survivalgames.user.data;

import io.github.green.survivalgames.Main;
import io.github.green.survivalgames.api.StatsStorage;
import io.github.green.survivalgames.user.User;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Despical
 * <p>
 * Created at 29.01.2022
 */
public interface UserDatabase {

	Main plugin = JavaPlugin.getPlugin(Main.class);

	void saveStatistic(User user, StatsStorage.StatisticType stat);

	void saveAllStatistic(User user);

	void loadStatistics(User user);

}