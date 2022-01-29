package io.github.green.survivalgames.handlers;

import io.github.green.survivalgames.Main;
import io.github.green.survivalgames.arena.Arena;
import me.clip.placeholderapi.PlaceholderAPI;
import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.string.StringFormatUtils;
import me.despical.commons.util.Strings;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Despical
 * <p>
 * Created at 29.01.2022
 */
public class ChatManager {

	private String prefix;

	private final Main plugin;
	private FileConfiguration config;

	public ChatManager(Main plugin) {
		this.plugin = plugin;
		this.config = ConfigUtils.getConfig(plugin, "messages");
		this.prefix = message("In-Game.Plugin-Prefix");
	}

	public String coloredRawMessage(String message) {
		return Strings.format(message);
	}

	public String prefixedRawMessage(String message) {
		return prefix + coloredRawMessage(message);
	}

	public String message(String message) {
		return coloredRawMessage(config.getString(message));
	}

	public String prefixedMessage(String path) {
		return prefix + message(path);
	}

	public String message(String message, Player player) {
		String returnString = config.getString(message);

		if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			returnString = PlaceholderAPI.setPlaceholders(player, returnString);
		}

		return coloredRawMessage(returnString);
	}

	public String prefixedMessage(String message, Player player) {
		return prefix + message(message, player);
	}

	public String formatMessage(Arena arena, String message, Player player) {
		String returnString = message;

		returnString = StringUtils.replace(returnString, "%player%", player.getName());
		returnString = formatPlaceholders(returnString, arena);

		if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			returnString = PlaceholderAPI.setPlaceholders(player, returnString);
		}

		return coloredRawMessage(returnString);
	}

	public String prefixedFormattedMessage(Arena arena, String message, Player player) {
		return prefix + formatMessage(arena, message, player);
	}

	private String formatPlaceholders(String message, Arena arena) {
		String returnString = message;

//		returnString = StringUtils.replace(returnString, "%arena%", arena.getMapName());
//		returnString = StringUtils.replace(returnString, "%time%", Integer.toString(arena.getTimer()));
//		returnString = StringUtils.replace(returnString, "%formatted_time%", StringFormatUtils.formatIntoMMSS(arena.getTimer()));
//		returnString = StringUtils.replace(returnString, "%players%", Integer.toString(arena.getPlayers().size()));
//		returnString = StringUtils.replace(returnString, "%maxplayers%", Integer.toString(arena.getMaximumPlayers()));
//		returnString = StringUtils.replace(returnString, "%minplayers%", Integer.toString(arena.getMinimumPlayers()));
		return returnString;
	}

	public String formatMessage(Arena arena, String message, int i) {
		String returnString = message;

		returnString = StringUtils.replace(returnString, "%number%", Integer.toString(i));
		returnString = formatPlaceholders(returnString, arena);
		return coloredRawMessage(returnString);
	}

	public String prefixedFormattedPathMessage(Arena arena, String path, int i) {
		return prefix + formatMessage(arena, message(path), i);
	}

	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}

	public void broadcastAction(Arena arena, Player player, ActionType action) {
		if (plugin.getUserManager().getUser(player).isSpectator()) return;

		String message;

		switch (action) {
			case JOIN:
				message = formatMessage(arena, message("In-Game.Messages.Join"), player);
				break;
			case LEAVE:
				message = formatMessage(arena, message("In-Game.Messages.Leave"), player);
				break;
			default:
				return;
		}

//		arena.broadcastMessage(prefix + message);
	}

	public void reloadConfig() {
		plugin.reloadConfig();

		config = ConfigUtils.getConfig(plugin, "messages");
		prefix = message("In-Game.Plugin-Prefix");
	}

	public enum ActionType {
		JOIN, LEAVE
	}
}