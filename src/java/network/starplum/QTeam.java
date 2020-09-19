package network.starplum;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import network.starplum.commands.TeamCommand;
import network.starplum.config.ConfigManager;
import network.starplum.database.DatabaseCredentials;
import network.starplum.database.DatabaseManager;
import network.starplum.interfaces.ITeam;
import network.starplum.managers.DisabledScoreboardManager;
import network.starplum.managers.InviteManager;
import network.starplum.managers.ScoreboardManager;

public class QTeam extends JavaPlugin {

	public static final String PREFIX_COLOR = "§5§lqTeam §8●§r";
	public static final String PREFIX = "qTeam ●";
	private static InviteManager inviteManager;
	private static JavaPlugin plugin;
	private static ConfigManager configManager;
	private static DatabaseManager databaseManager;
	private static ScoreboardManager scoreboardManager;
	private static QTeam qTeam;

	@Override
	public void onEnable() {
		super.getCommand("team").setExecutor(new TeamCommand());
		qTeam = this;
		plugin = this;
		configManager = new ConfigManager(this);
		configManager.loadConfig();
		databaseManager = new DatabaseManager(this.getDatabaseCredentials());
		inviteManager = new InviteManager(this);
		if(!databaseManager.canConnect()){
			super.getLogger().warning("Can't connect to the database. Are the credentials correctly set?");
			super.getLogger().warning("This plugin requires a database! Shutting down...");
			super.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if(!databaseManager.doesTableExist()){
			super.getLogger().info("The clans table does not exist, creating it...");
			databaseManager.createTable();
			super.getLogger().info("Finished initializing.");
		}
		
		/**
		 * Enable this if you want, but qTeam has a custom tablist listener.
		 */
		/**
		if(configManager.getConfig().setClanTagTabPrefix()) {
			Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
				scoreboardManager = new EnabledScoreboardManager(this);
				Bukkit.getPluginManager().registerEvents(new ScoreboardUpdateListener(), this);
				ScoreboardUtil.updateScoreboard();
			});
		} else { scoreboardManager = new DisabledScoreboardManager(); }
		*/
		scoreboardManager = new DisabledScoreboardManager();
	}

	private DatabaseCredentials getDatabaseCredentials(){
		return new DatabaseCredentials() {
			@Override
			public int getPort() { return configManager.getConfig().getDbPort(); }
			@Override
			public String getHost() { return configManager.getConfig().getDbHost(); }
			@Override
			public String getDatabase() { return configManager.getConfig().getDbDb(); }
			@Override
			public String getUser() { return configManager.getConfig().getDbUser(); }
			@Override
			public String getPassword() { return configManager.getConfig().getDbPassword(); }
		};
	}

	public static ConfigManager getConfigManager() { return configManager; }
	public static DatabaseManager getDatabaseManager() { return databaseManager; }
	public static InviteManager getInviteManager() { return inviteManager; }
	public static JavaPlugin getPlugin() { return plugin; }
	public static List<ITeam> getClans() { return databaseManager.getClans(); }
	public static ITeam getTeam(String clanName) { return databaseManager.getClan(clanName); }
	public static ITeam getTeam(UUID member) { return databaseManager.getClan(member); }
	public static ITeam getTeam(Player player) { return databaseManager.getClan(player.getUniqueId()); }
	public static ITeam getTeam(int clanId) { return databaseManager.getTeam(clanId); }
	public static void updateClanMembers(ITeam clan, List<UUID> members) { databaseManager.updateMembers(clan.getTeamId(), members); }
	public static ScoreboardManager getScoreboardManager() { return scoreboardManager; }
	public static QTeam getqTeam() { return qTeam; }
	
	/**
	 * Will return you a scoreboard with teams and prefixes according to the players clans.
	 * @return @{@link Scoreboard} a scoreboard by default or an empty scoreboard if the clan prefixes are disabled
	 */
	public static Scoreboard getClansScoreboard() { return scoreboardManager.getScoreboard(); }

}
