package network.starplum.placeholder;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import network.starplum.QTeam;
import network.starplum.interfaces.ITeam;

public class PapiHook extends PlaceholderExpansion {

	@Override public boolean canRegister() { return QTeam.getqTeam() != null; }
	@Override public String getAuthor() { return "iDevMC"; }
	@Override public String getIdentifier() { return "iDevMC"; }
	@Override public String getRequiredPlugin() { return "QTeam"; }
	@Override public String getVersion() { return QTeam.getqTeam().getDescription().getVersion(); }

	@Override public String onPlaceholderRequest(Player player, String identifier){

		if(player == null) { return ""; }
		if(identifier.equals("team_name")) { getTeamName(player); }
		if(identifier.equals("team_prefix")) { return getTeamTag(player); }
		return null;
		
	}
	
	public String getTeamName(Player player) {
		ITeam team = QTeam.getTeam(player);
		if(team == null) { return "§7Not in any team";
		} else return team.getTeamName();
	}
	
	public String getTeamTag(Player player) {
		ITeam team = QTeam.getTeam(player);
		if(team == null) { return "§7Not in any team";
		} else return team.getTeamTag();
	}

}
