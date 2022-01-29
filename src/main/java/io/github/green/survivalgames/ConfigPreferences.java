package io.github.green.survivalgames;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Despical
 * <p>
 * Created at 29.01.2022
 */
public class ConfigPreferences {

	private final Main plugin;
	private final Map<Option, Boolean> options;
	private final Map<IntOption, Integer> intOptions;

	public ConfigPreferences(Main plugin) {
		this.plugin = plugin;
		this.options = new HashMap<>();
		this.intOptions = new HashMap<>();
		this.plugin.saveDefaultConfig();
		this.loadOptions();
	}

	public boolean getOption(Option option) {
		return options.get(option);
	}

	public int getIntOption(IntOption option) {
		return intOptions.get(option);
	}

	private void loadOptions() {
		for (Option option : Option.values()) {
			options.put(option, plugin.getConfig().getBoolean(option.path, option.def));
		}

		for (IntOption option : IntOption.values()) {
			intOptions.put(option, plugin.getConfig().getInt(option.path, option.def));
		}
	}

	public enum IntOption {
		;

		private final String path;
		private final int def;

		IntOption(String path, int def) {
			this.path = path;
			this.def = def;
		}

		public String getPath() {
			return path;
		}

		public int getDefault() {
			return def;
		}
	}

	public enum Option {
		DEBUG_ENABLED("Debug-Mode", false), DATABASE_ENABLED("Database-Enabled", false);

		private final String path;
		private final boolean def;

		Option(String path) {
			this(path, true);
		}

		Option(String path, boolean def) {
			this.path = path;
			this.def = def;
		}

		public String getPath() {
			return path;
		}

		public boolean getDefault() {
			return def;
		}
	}
}