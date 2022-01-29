package io.github.green.survivalgames;

import io.github.green.survivalgames.handlers.ChatManager;
import io.github.green.survivalgames.user.UserManager;
import me.despical.commons.compat.VersionResolver;
import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.database.MysqlDatabase;
import me.despical.commons.exception.ExceptionLogHandler;
import me.despical.commons.scoreboard.ScoreboardLib;
import me.despical.commons.util.Collections;
import me.despical.commons.util.JavaVersion;
import me.despical.commons.util.LogUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * @author Despical
 * <p>
 * Created at 29.01.2022
 */
public class Main extends JavaPlugin {

	private boolean forceDisable = false;

	private ChatManager chatManager;
	private ConfigPreferences configPreferences;
	private ExceptionLogHandler exceptionLogHandler;
	private UserManager userManager;
	private MysqlDatabase database;

	@Override
	public void onEnable() {
		if (forceDisable = !validateIfPluginShouldStart()) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		exceptionLogHandler = new ExceptionLogHandler(this);
		exceptionLogHandler.setMainPackage("io.github.green.survivalgames");
		exceptionLogHandler.addBlacklistedClass("io.github.green.survivalgames.user.data.MysqlManager", "me.despical.commons.database.MysqlDatabase");
		exceptionLogHandler.setRecordMessage("[SG] We have found a bug in the code. Create an issue on our GitHub repo with the following error given above!");

		getServer().getLogger().addHandler(exceptionLogHandler);

		long start = System.currentTimeMillis();
		configPreferences = new ConfigPreferences(this);

		if (configPreferences.getOption(ConfigPreferences.Option.DEBUG_ENABLED)) {
			LogUtils.enableLogging();
			LogUtils.log("Initialization started.");
		}

		initializeFiles();
		initializeClasses();

		LogUtils.log("Initialization finished took {0} ms.", System.currentTimeMillis() - start);
	}

	@Override
	public void onDisable() {
		if (forceDisable) return;

		LogUtils.log("System disable initialized");
		long start = System.currentTimeMillis();

		getServer().getLogger().removeHandler(exceptionLogHandler);

		if (configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
			database.shutdownConnPool();
		}

		if (configPreferences.getOption(ConfigPreferences.Option.DEBUG_ENABLED)) {
			LogUtils.disableLogging();
		}


		LogUtils.log("System disable finished took {0} ms", System.currentTimeMillis() - start);
	}

	private void initializeClasses() {
		ScoreboardLib.setPluginInstance(this);

		this.chatManager = new ChatManager(this);

		if (configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
			this.database = new MysqlDatabase(ConfigUtils.getConfig(this, "mysql"));
		}

		this.userManager = new UserManager(this);
	}

	private void initializeFiles() {
		Collections.streamOf("arenas", "bungee", "rewards", "stats", "items", "mysql", "messages").filter(name -> !new File(getDataFolder(), name + ".yml").exists()).forEach(name -> saveResource(name + ".yml", false));
	}

	private boolean validateIfPluginShouldStart() {
		if (!VersionResolver.isCurrentBetween(VersionResolver.ServerVersion.v1_8_R1, VersionResolver.ServerVersion.v1_18_R1)) {
			LogUtils.sendConsoleMessage("&cYour server version isn't supported by Survival Games!");
			LogUtils.sendConsoleMessage("&cMaybe you consider changing your server version?");
			return false;
		}

		if (JavaVersion.getCurrentVersion().isAt(JavaVersion.JAVA_8)) {
			LogUtils.sendConsoleMessage("&cThis plugin won't support Java 8 in future updates.");
			LogUtils.sendConsoleMessage("&cSo, maybe consider to update your version, right?");
		}

		try {
			Class.forName("org.spigotmc.SpigotConfig");
		} catch (Exception exception) {
			LogUtils.sendConsoleMessage("&cYour server software is not supported by Survival Games!");
			LogUtils.sendConsoleMessage("&cWe support only Spigot and Spigot forks only! Shutting off.");
			return false;
		}

		return true;
	}

	public ChatManager getChatManager() {
		return chatManager;
	}

	public ConfigPreferences getConfigPreferences() {
		return configPreferences;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public MysqlDatabase getMysqlDatabase() {
		return database;
	}
}