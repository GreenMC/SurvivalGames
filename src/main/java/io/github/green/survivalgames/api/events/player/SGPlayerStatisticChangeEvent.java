package io.github.green.survivalgames.api.events.player;

import io.github.green.survivalgames.api.StatsStorage;
import io.github.green.survivalgames.api.events.SGEvent;
import io.github.green.survivalgames.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * @author Despical
 * <p>
 * Created at 29.01.2022
 */
public class SGPlayerStatisticChangeEvent extends SGEvent {

	private static final HandlerList HANDLERS = new HandlerList();
	private final Player player;
	private final StatsStorage.StatisticType statisticType;
	private final int number;

	public SGPlayerStatisticChangeEvent(Arena eventArena, Player player, StatsStorage.StatisticType statisticType, int number) {
		super(eventArena);
		this.player = player;
		this.statisticType = statisticType;
		this.number = number;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public Player getPlayer() {
		return player;
	}

	public StatsStorage.StatisticType getStatisticType() {
		return statisticType;
	}

	public int getNumber() {
		return number;
	}
}