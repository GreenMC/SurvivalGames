package io.github.green.survivalgames.user;

import io.github.green.survivalgames.Main;
import io.github.green.survivalgames.api.StatsStorage;
import io.github.green.survivalgames.api.events.player.SGPlayerStatisticChangeEvent;
import io.github.green.survivalgames.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Despical
 * <p>
 * Created at 29.01.2022
 */
public class User {

	private final static Main plugin = JavaPlugin.getPlugin(Main.class);

	private final UUID uuid;
	private final Player player;
	private final Map<StatsStorage.StatisticType, Integer> stats;

	private boolean spectator;

	public User(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId();
		this.stats = new EnumMap<>(StatsStorage.StatisticType.class);
	}

	public Arena getArena() {
		return /*ArenaRegistry.getArena(player)*/ null;
	}

	public Player getPlayer() {
		return player;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public boolean isSpectator() {
		return spectator;
	}

	public void setSpectator(boolean spectating) {
		spectator = spectating;
	}

	public int getStat(StatsStorage.StatisticType statisticType) {
		Integer statistic = stats.get(statisticType);

		if (statistic == null) {
			stats.put(statisticType, 0);
			return 0;
		}

		return statistic;
	}

	public void setStat(StatsStorage.StatisticType stat, int i) {
		stats.put(stat, i);

		plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().getPluginManager().callEvent(new SGPlayerStatisticChangeEvent(getArena(), player, stat, i)));
	}

	public void addStat(StatsStorage.StatisticType stat, int i) {
		setStat(stat, getStat(stat) + i);
	}

	public void resetStats() {
		for (StatsStorage.StatisticType statistic : StatsStorage.StatisticType.values()) {
			if (!statistic.isPersistent()) continue;

			setStat(statistic, 0);
		}
	}
}