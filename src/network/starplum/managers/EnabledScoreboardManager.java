package network.starplum.managers;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import network.starplum.QTeam;
import network.starplum.interfaces.ITeam;

public class EnabledScoreboardManager implements ScoreboardManager {

	private Scoreboard scoreboard;
	private final JavaPlugin plugin;

	public EnabledScoreboardManager(JavaPlugin plugin) {
		this.plugin = plugin;
		this.init();
	}

	private void init() {
		Bukkit.getScheduler().runTask(this.plugin, () -> this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard());
		List<ITeam> clans = QTeam.getTeams();
		clans.forEach(clan -> {
			Team team = scoreboard.registerNewTeam(clan.getTeamTag());
			team.setPrefix(ChatColor.GRAY + "[" + ChatColor.AQUA + clan.getTeamTag() + ChatColor.GRAY + "] ");
			clan.getMembers().forEach(member -> team.addEntry(Bukkit.getOfflinePlayer(member).getName()));
		});
	}

	@Override
	public void removeTeam(ITeam clan) {
		Team team = this.scoreboard.getTeam(clan.getTeamTag());
		if(team != null) { team.unregister(); }
	}

	@Override
	public void registerTeam(ITeam clan) {
		Team team = this.scoreboard.getTeam(clan.getTeamTag());
		if (team == null) {
			team = this.scoreboard.registerNewTeam(clan.getTeamTag());
		}
		for (UUID uuid : clan.getMembers()) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
			if(!team.hasEntry(offlinePlayer.getName())){
				team.addEntry(offlinePlayer.getName());
			}
		}
	}

	@Override
	public void removePlayer(ITeam clan, OfflinePlayer offlinePlayer) { this.removePlayer(clan, offlinePlayer.getName()); }
	@Override
	public void removePlayer(ITeam clan, Player player) { this.removePlayer(clan, player.getName()); }

	@Override
	public void removePlayer(ITeam clan, String entry) {
		Team team = this.scoreboard.getTeam(clan.getTeamTag());
		if (team == null) { return; }
		if (team.hasEntry(entry)) { team.removeEntry(entry); }
	}

	@Override
	public void addPlayer(ITeam clan, OfflinePlayer player) { this.addPlayer(clan, player.getName()); }
	@Override
	public void addPlayer(ITeam clan, Player player) { this.addPlayer(clan, player.getName()); }
	@Override
	public void addPlayer(ITeam clan, String entry) {
		Team team = this.scoreboard.getTeam(clan.getTeamTag());
		if (team == null) { return; }
		if (!team.hasEntry(entry)) { team.addEntry(entry); }
	}

	@Override
	public Scoreboard getScoreboard() { return scoreboard; }

}