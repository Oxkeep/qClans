package network.starplum.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import network.starplum.interfaces.ITeam;

public interface ScoreboardManager {

	void removeTeam(ITeam team);
	void registerTeam(ITeam team);
	void removePlayer(ITeam team, OfflinePlayer offlinePlayer);
	void removePlayer(ITeam team, Player player);
	void removePlayer(ITeam team, String entry);
	void addPlayer(ITeam clan, OfflinePlayer player);
	void addPlayer(ITeam clan, Player player);
	void addPlayer(ITeam clan, String entry);
	Scoreboard getScoreboard();

}
