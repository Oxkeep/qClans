package network.starplum.managers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import network.starplum.interfaces.ITeam;

public class DisabledScoreboardManager implements ScoreboardManager {

	@Override public void removeTeam(ITeam team) {}
	@Override public void registerTeam(ITeam team) {}
	@Override public void removePlayer(ITeam team, OfflinePlayer offlinePlayer) {}
	@Override public void removePlayer(ITeam team, Player player) {}
	@Override public void removePlayer(ITeam team, String entry) {}
	@Override public void addPlayer(ITeam team, OfflinePlayer player) {}
	@Override public void addPlayer(ITeam team, Player player) {}
	@Override public void addPlayer(ITeam team, String entry) {}
	@Override public Scoreboard getScoreboard() { return Bukkit.getScoreboardManager().getNewScoreboard(); }

}

