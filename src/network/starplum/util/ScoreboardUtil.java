package network.starplum.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import network.starplum.QTeam;
import network.starplum.interfaces.ITeam;

public class ScoreboardUtil {

	@Deprecated
	public static void removeClan(ITeam clan) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team = scoreboard.getTeam(clan.getTeamTag());
		if(team == null) { return; } 
		team.unregister();
	}

	@Deprecated
	public static void updateScoreboard(ITeam clan) {
		Bukkit.getScheduler().runTaskAsynchronously(QTeam.getqTeam(), () -> {
			Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
			Team team = scoreboard.getTeam(clan.getTeamTag());
			if(team == null) { team = scoreboard.registerNewTeam(clan.getTeamTag()); }
			team.setPrefix(ChatColor.GRAY + "[" + ChatColor.AQUA + clan.getTeamTag() + ChatColor.GRAY + "] ");
			for (UUID uuid : clan.getMembers()) {
				if(team.hasEntry(Bukkit.getOfflinePlayer(uuid).getName())) { continue; }
				team.addEntry(Bukkit.getOfflinePlayer(uuid).getName());
			}

			for (Team otherTeam : scoreboard.getTeams()) {
				if(otherTeam.equals(team)) { continue; }
				for (UUID uuid : clan.getMembers()) {
					if(team.hasEntry(Bukkit.getOfflinePlayer(uuid).getName())){ team.removeEntry(Bukkit.getOfflinePlayer(uuid).getName()); }
				}
			}
		});
	}

	public static void flushScoreboard(Player player) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		scoreboard.getTeams().forEach(Team::unregister);
		scoreboard.clearSlot(DisplaySlot.PLAYER_LIST);
		player.setScoreboard(scoreboard);
	}

	public static void updateScoreboard(){
		if(QTeam.getConfigManager().getConfig().setClanTagTabPrefix()){
			Scoreboard scoreboard = QTeam.getTeamsScoreboard();
			Bukkit.getOnlinePlayers().forEach(player -> player.setScoreboard(scoreboard));
		}
	}

}
