package network.starplum.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import network.starplum.QTeam;
import network.starplum.interfaces.IInvite;
import network.starplum.interfaces.ITeam;

public class InviteManager {

	private final List<IInvite> invites;
	private final JavaPlugin javaPlugin;

	public InviteManager(JavaPlugin javaPlugin) {
		this.invites = new ArrayList<>();
		this.javaPlugin = javaPlugin;
	}

	public void sendInvite(IInvite invite) {
		if(!this.invites.contains(invite)) {
			invite.getTargetPlayer().sendMessage(QTeam.PREFIX_COLOR + " You got invited to: " + ChatColor.DARK_PURPLE  + invite.getTeam().getTeamName() + ChatColor.RESET + " by: " + Bukkit.getOfflinePlayer(invite.getTeam().getOwner()).getName());
			invite.getTargetPlayer().sendMessage(QTeam.PREFIX_COLOR + " You can accept the invite with: " + ChatColor.GRAY + "/clans join " + invite.getTeam().getTeamName());
			this.invites.add(invite);
		}
	}

	public boolean hasNoInvite(Player player){
		return this.invites.stream().noneMatch(invite -> invite.getTargetPlayer().equals(player));
	}

	public boolean accept(Player player, String clanName){
		if(this.hasNoInvite(player)){
			return false;
		}
		if(this.invites.stream().noneMatch(invite -> invite.getTeam().getTeamName().equals(clanName))){
			return false;
		}
		IInvite invite = this.invites.stream()
				.filter(streamInvite -> streamInvite.getTargetPlayer().equals(player)
						&& streamInvite.getTeam().getTeamName().equals(clanName))
				.findFirst()
				.orElse(null);
		if(invite == null){
			return false;
		}
		ITeam team = invite.getTeam();
		List<UUID> members = team.getMembers();
		members.add(player.getUniqueId());
		QTeam.updateClanMembers(team, members);
		Player owner = Bukkit.getPlayer(team.getOwner());
		if(owner != null){ Bukkit.getScheduler().runTask(this.javaPlugin, () -> owner.sendMessage(QTeam.PREFIX_COLOR + " " + player.getName() + " accepted your invitation.")); }
		this.invites.removeIf(streamInvite -> streamInvite.getTargetPlayer().equals(player));
		return true;
	}

}
