package io.github.green.survivalgames.api.events;

import io.github.green.survivalgames.arena.Arena;
import org.bukkit.event.Event;

/**
 * @author Despical
 * <p>
 * Created at 29.01.2022
 */
public abstract class SGEvent extends Event {

	protected Arena arena;

	public SGEvent(Arena eventArena) {
		arena = eventArena;
	}

	public Arena getArena() {
		return arena;
	}
}