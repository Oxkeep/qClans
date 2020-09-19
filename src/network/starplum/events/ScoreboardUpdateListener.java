package network.starplum.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import network.starplum.QTeam;
import network.starplum.util.ScoreboardUtil;

public class ScoreboardUpdateListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event){
		if(QTeam.getConfigManager().getConfig().flushScoreboardOnJoin()){
			Bukkit.getOnlinePlayers().forEach(ScoreboardUtil::flushScoreboard);
		}
	}

}
