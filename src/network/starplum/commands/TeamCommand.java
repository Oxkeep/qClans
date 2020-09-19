package network.starplum.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import network.starplum.QTeam;
import network.starplum.interfaces.IInvite;
import network.starplum.interfaces.ITeam;
import network.starplum.util.ScoreboardUtil;
import network.starplum.util.TreonaSound;

public class TeamCommand implements CommandExecutor, TabCompleter {
	
	protected List<String> commandArg = new ArrayList<String>();

	public TeamCommand() {
		QTeam.getqTeam().getCommand("team").setExecutor(this);
		QTeam.getqTeam().getCommand("team").setTabCompleter(this);
		
		commandArg.add("create");
		commandArg.add("stats");
		commandArg.add("join");
		commandArg.add("invite");
		commandArg.add("kick");
		commandArg.add("leave");
		commandArg.add("disband");
	}

	protected final double version = 0.1;
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		
		final CommandSender sender = arg0;
		final Player player = (Player) arg0;
		final String[] args = arg3;
		final boolean isPlayer = sender instanceof Player;
		
		final TextComponent watermark = new TextComponent("§5§lqTeam §8● §fVersion 1");
		watermark.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("").create()));
		watermark.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/team <create/delete/leave> <arg>"));
		
		final TextComponent help = new TextComponent("§5§lqTeam §8● §fHover for help !");
		help.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
				"\n" +
				"§8・ §7/team create  §8§m--§r §fCreate you own team !"          + "\n" +
				"§8・ §7/team stats   §8§m--§r §fShows the stats of a team"      + "\n" +
				"§8・ §7/team join    §8§m--§r §fAccess the invation of a team"  + "\n" +
				"§8・ §7/team invite  §8§m--§r §fInvite a player to your team"   + "\n" +
				"§8・ §7/team kick    §8§m--§r §fKick a player out of your team" + "\n" +
				"§8・ §7/team leave   §8§m--§r §fLeave your current team"        + "\n" +
				"§8・ §7/team disband §8§m--§r §fDisband your team forever. "    + 
				"\n"
				).create()));
		
		final TextComponent unknown = new TextComponent("§5§lqTeam §8● §fThat subcommand doesn't exist §4§l?!");
		unknown.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8● §fClick to get help !").create()));
		unknown.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/team help"));
		
		final TextComponent version = new TextComponent("§5§lqTeam §8● §fVersion " + this.version);
		version.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(" §7Version " + this.version).create()));
		version.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "qCrates;" + this.version));
		
		final TextComponent teamOwner = new TextComponent("§5§lqTeam §8● §fYou must be the team leader to disband the team !");
		final TextComponent noTeam    = new TextComponent("§5§lqTeam §8● §fYou must have be on a team !");
		final TextComponent cantLeave = new TextComponent("§5§lqTeam §8● §fYou can't leave your own team, you must disband it !");
		final TextComponent youLeft   = new TextComponent("§5§lqTeam §8● §fYou left the team successfully.");
		final TextComponent kickNEA   = new TextComponent("§5§lqTeam §8● §fNot enough arguments to kick a player!");
		final TextComponent inviteNEA = new TextComponent("§5§lqTeam §8● §fNot enough arguments to invite a player!");
		
		if(!isPlayer) {
			sender.sendMessage(QTeam.PREFIX_COLOR + " Only players can use this command !");
			return false;
		} else {
			
			// TODO: Just a pointer to indicate argument lenght is 0 (<command>)
			if(args.length == 0) {
				player.spigot().sendMessage(watermark);
				return true;
			// TODO: Just a pointer to indicate argument lenght is 1 (<command> arg1)
			} else if(args.length == 1) {
				
				if(args[0].contentEquals("help")) {
					player.spigot().sendMessage(help);
					return true;
				} else if(args[0].contentEquals("create")) {
					player.sendMessage("§5§lqTeam §8● §fYou can create a team with §7/team create <name> <tag>");
					return true;
				} else if(args[0].contentEquals("stats")) {
					
					ITeam clan = QTeam.getTeam(player.getUniqueId());
					if (clan == null) { player.sendMessage("§5§lqTeam §8● §fYou're not in any team, please specify using §7/team stats <team>"); return false;
					} else { this.printStats(player, clan); }
					return true;
					
				} else if(args[0].contentEquals("join")) {
					player.sendMessage("§5§lqTeam §8● §fPlease specify the team that sent you an invitation. ");
					return true;
				} else if(args[0].contentEquals("invite")) {
					player.spigot().sendMessage(inviteNEA);
					return true;
				} else if(args[0].contentEquals("kick")) {
					player.spigot().sendMessage(kickNEA);
					return true;
				} else if(args[0].contentEquals("leave")) {
					
					ITeam clan = QTeam.getTeam(player);
					if(clan == null) { player.spigot().sendMessage(noTeam); return false; }
					if(clan.getOwner().equals(player.getUniqueId())) { player.spigot().sendMessage(cantLeave); return false; }
					List<UUID> members = clan.getMembers();
					members.remove(player.getUniqueId());
					QTeam.updateClanMembers(clan, members);
					
					player.spigot().sendMessage(youLeft);
					
					Player owner = Bukkit.getPlayer(clan.getOwner());
					if(owner != null && owner.isOnline()) { owner.sendMessage("§5§lqTeam §8● §f" + player.getName() + " left your team !"); }
					QTeam.getScoreboardManager().removePlayer(clan, player);
					ScoreboardUtil.updateScoreboard();
					
					return true;
					
				} else if(args[0].contentEquals("disband")) {
					
					if(hasTeam(player)) {
						if(canDisband(player)) {
							
							ITeam clan = QTeam.getTeam(player);
							final Player clanOwner = Bukkit.getPlayer(clan.getOwner());
							
							final TextComponent disbanded = new TextComponent("§5§lqTeam §8● §fYou disbanded your team §7" + clan.getTeamName());
							disbanded.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8● §fClick to create a different team with the same name!").create()));
							disbanded.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/team create " + clan.getTeamName() + " " + clan.getTeamTag()));
							
							final TextComponent teamDisbanded = new TextComponent("§5§lqTeam §8● §f" + clanOwner.getName() + " disbanded your team !");
							
							player.spigot().sendMessage(disbanded);
							
							clan.getMembers().forEach(uuid -> {
								OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
								if (offlinePlayer.isOnline() && !offlinePlayer.getUniqueId().equals(clan.getOwner())) { offlinePlayer.getPlayer().spigot().sendMessage(teamDisbanded); }
							});
							
							QTeam.getScoreboardManager().removeTeam(clan);
							ScoreboardUtil.updateScoreboard();
							QTeam.getDatabaseManager().deleteClan(clan.getTeamId());
							
						} else { player.spigot().sendMessage(teamOwner); return false; }
					} else { player.spigot().sendMessage(noTeam); return false; }
					
				} else if(args[0].contentEquals("version")) {
					player.spigot().sendMessage(version);
					return true;
				} else { player.spigot().sendMessage(unknown); return false; }
				
			// TODO: Just a pointer to indicate argument lenght is 2 (<command> arg1, arg2)
			} else if(args.length == 2) {
				
				if(args[0].contentEquals("help")) {
					player.spigot().sendMessage(help);
					return true;
				} else if(args[0].contentEquals("create")) {
					player.sendMessage("§5§lqTeam §8● §fYou can create a team with §7/team create <name> <tag>");
					return true;
				} else if(args[0].contentEquals("stats")) {
					
					ITeam clan = QTeam.getTeam(args[1]);
					if (clan == null) { player.sendMessage("§5§lqTeam §8● §fThat team doesn't exist !"); return false;
					} else { this.printStats(player, clan); return true; }

				} else if(args[0].contentEquals("join")) {
					
					if(QTeam.getInviteManager().hasNoInvite(player)) { player.sendMessage("§5§lqTeam §8● §fYou don't have any team invites."); return false; }
					if(QTeam.getInviteManager().accept(player, args[1])) {
						player.sendMessage("§5§lqTeam §8● §fYou successfully joined §7" + args[1]);
						player.playSound(player.getLocation(), TreonaSound.LEVEL_UP.getBukkitSound(), 1, 1);
						QTeam.getScoreboardManager().addPlayer(QTeam.getTeam(player), player);
						ScoreboardUtil.updateScoreboard();
						return true;
					} else { player.sendMessage("§5§lqTeam §8● §fYou don't have an invite from this team."); return false; }
					
				} else if(args[0].contentEquals("invite")) {
					
					ITeam clan = QTeam.getTeam(player.getUniqueId());
					if(clan == null) { player.spigot().sendMessage(noTeam); }
					Player targetPlayer = Bukkit.getPlayer(args[1]);
					if(targetPlayer == null) { player.sendMessage("§5§lqTeam §8● §fThat player wasn't found."); return false; }
					if (QTeam.getTeam(targetPlayer.getUniqueId()) != null) { player.sendMessage("§5§lqTeam §8● §fThat player is already in a team."); return false;  }
					if (!clan.getOwner().equals(player.getUniqueId())) { player.sendMessage("§5§lqTeam §8● §fOnly the owner can invite people in the clan !"); return false; }
					QTeam.getInviteManager().sendInvite(new IInvite() {
						@Override public ITeam getTeam() { return clan; }
						@Override public Player getTargetPlayer() { return targetPlayer; }
					});
					player.sendMessage("§5§lqTeam §8● §fRequest sent to the player !");
					return true;
					
				} else if(args[0].contentEquals("kick")) {
					
					ITeam clan = QTeam.getTeam(player);
					if(clan == null) { player.spigot().sendMessage(noTeam); return false; }
					if(!clan.getOwner().equals(player.getUniqueId())) { player.sendMessage("§5§lqTeam §8● §fOnly the team leader can kick players !"); return false; }
					OfflinePlayer targetPlayer = Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> offlinePlayer.getName().equals(args[1])).findFirst().orElse(null);
					if(targetPlayer == null) { player.sendMessage("§5§lqTeam §8● §fThat player wasn't found."); return false; }
					if(targetPlayer.getUniqueId().equals(player.getUniqueId())) { player.sendMessage("§5§lqTeam §8● §fYou can't kick yourself from the team !"); return false; }
					if(!clan.getMembers().contains(targetPlayer.getUniqueId())) { player.sendMessage("§5§lqTeam §8● §fThat player isn't in your clan!"); return false; }
					List<UUID> members = clan.getMembers();
					members.remove(targetPlayer.getUniqueId());
					QTeam.updateClanMembers(clan, members);
					player.sendMessage("§5§lqTeam §8● §fThat player has been successfully kicked from the team !");
					if(targetPlayer.isOnline()) { targetPlayer.getPlayer().sendMessage("§5§lqTeam §8● §fYou got kicked from " + clan.getTeamName()); }
					QTeam.getScoreboardManager().removePlayer(clan, targetPlayer);
					ScoreboardUtil.updateScoreboard();
					return true;
					
				} else if(args[0].contentEquals("leave")) {
					
					ITeam clan = QTeam.getTeam(player);
					if(clan == null) { player.spigot().sendMessage(noTeam); return false; }
					if(clan.getOwner().equals(player.getUniqueId())) { player.spigot().sendMessage(cantLeave); return false; }
					List<UUID> members = clan.getMembers();
					members.remove(player.getUniqueId());
					QTeam.updateClanMembers(clan, members);
					
					player.spigot().sendMessage(youLeft);
					
					Player owner = Bukkit.getPlayer(clan.getOwner());
					if(owner != null && owner.isOnline()) { owner.sendMessage("§5§lqTeam §8● §f" + player.getName() + " left your team !"); }
					QTeam.getScoreboardManager().removePlayer(clan, player);
					ScoreboardUtil.updateScoreboard();
					return true;
					
				} else if(args[0].contentEquals("disband")) {
					
					if(hasTeam(player)) {
						if(canDisband(player)) {
							
							ITeam clan = QTeam.getTeam(player);
							final Player clanOwner = Bukkit.getPlayer(clan.getOwner());
							
							final TextComponent disbanded = new TextComponent("§5§lqTeam §8● §fYou disbanded your team §7" + clan.getTeamName());
							disbanded.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8● §fClick to create a different team with the same name!").create()));
							disbanded.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/team create " + clan.getTeamName() + " " + clan.getTeamTag()));
							
							final TextComponent teamDisbanded = new TextComponent("§5§lqTeam §8● §f" + clanOwner.getName() + " disbanded your team !");
							
							player.spigot().sendMessage(disbanded);
							
							clan.getMembers().forEach(uuid -> {
								OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
								if (offlinePlayer.isOnline() && !offlinePlayer.getUniqueId().equals(clan.getOwner())) { offlinePlayer.getPlayer().spigot().sendMessage(teamDisbanded); }
							});
							
							QTeam.getDatabaseManager().deleteClan(clan.getTeamId());
							QTeam.getScoreboardManager().removeTeam(clan);
							ScoreboardUtil.updateScoreboard();
							
							return true;
							
						} else { player.spigot().sendMessage(teamOwner); return false; }
					} else { player.spigot().sendMessage(noTeam); return false; }
					
				} else if(args[0].contentEquals("version")) {
					player.spigot().sendMessage(version);
					return true;
				} else { player.spigot().sendMessage(unknown); return false; }
				
			// TODO: Just a pointer to indicate argument lenght is 3 (<command> arg1, arg2, arg3)
			} else if(args.length == 3) {
				
				if(args[0].contentEquals("help")) {
					player.spigot().sendMessage(help);
				} else if(args[0].contentEquals("create")) {
					
					String clanName = args[1];
					if(QTeam.getDatabaseManager().isInATeam(player.getUniqueId())) { sender.sendMessage("§5§lqTeam §8● §fYou can only be in one team !"); return false; }
					if(QTeam.getDatabaseManager().isClanNameTaken(clanName)) { sender.sendMessage("§5§lqTeam §8● §fThis team name has already being used!"); return false; }
					QTeam.getDatabaseManager().createClan(clanName, args[2], player.getUniqueId());
					player.sendMessage("§5§lqTeam §8● §fYour team successfully got created !");
					player.playSound(player.getLocation(), TreonaSound.LEVEL_UP.getBukkitSound(), 1, 1);
					QTeam.getScoreboardManager().registerTeam(QTeam.getTeam(player));
					ScoreboardUtil.updateScoreboard();
					return true;
					
				} else if(args[0].contentEquals("stats")) {
					
					ITeam clan = QTeam.getTeam(args[1]);
					if (clan == null) { player.sendMessage("§5§lqTeam §8● §fThat team doesn't exist !"); return false;
					} else { this.printStats(player, clan); return true; }
					
				} else if(args[0].contentEquals("join")) {
					
					if(QTeam.getInviteManager().hasNoInvite(player)) { player.sendMessage("§5§lqTeam §8● §fYou don't have any team invites."); return false; }
					if(QTeam.getInviteManager().accept(player, args[1])) {
						player.sendMessage("§5§lqTeam §8● §fYou successfully joined §7" + args[1]);
						player.playSound(player.getLocation(), TreonaSound.LEVEL_UP.getBukkitSound(), 1, 1);
						QTeam.getScoreboardManager().addPlayer(QTeam.getTeam(player), player);
						ScoreboardUtil.updateScoreboard();
					} else { player.sendMessage("§5§lqTeam §8● §fYou don't have an invite from this team."); return true; }
					
				} else if(args[0].contentEquals("invite")) {
					
					ITeam clan = QTeam.getTeam(player.getUniqueId());
					if(clan == null) { player.spigot().sendMessage(noTeam); return false; }
					Player targetPlayer = Bukkit.getPlayer(args[1]);
					if(targetPlayer == null) { player.sendMessage(QTeam.PREFIX_COLOR + ChatColor.YELLOW + "§5§lqTeam §8● §fThis player wasn't found!"); return false; }
					if (QTeam.getTeam(targetPlayer.getUniqueId()) != null) { player.sendMessage("§5§lqTeam §8● §fThat player is already in a team."); return false; }
					if (!clan.getOwner().equals(player.getUniqueId())) { player.sendMessage("§5§lqTeam §8● §fOnly the team owner can invite people."); return false; }
					QTeam.getInviteManager().sendInvite(new IInvite() {
						@Override public ITeam getTeam() { return clan; }
						@Override public Player getTargetPlayer() { return targetPlayer; }
					});
					player.sendMessage(QTeam.PREFIX_COLOR + " request sent");
					return true;
					
				} else if(args[0].contentEquals("kick")) {
					
					ITeam clan = QTeam.getTeam(player);
					if(clan == null) { player.spigot().sendMessage(noTeam); return false; }
					if(!clan.getOwner().equals(player.getUniqueId())) { player.sendMessage("§5§lqTeam §8● §fOnly the team leader can kick players !"); return false; }
					OfflinePlayer targetPlayer = Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> offlinePlayer.getName().equals(args[1])).findFirst().orElse(null);
					if(targetPlayer == null) { player.sendMessage("§5§lqTeam §8● §fThat player wasn't found."); return false; }
					if(targetPlayer.getUniqueId().equals(player.getUniqueId())) { player.sendMessage("§5§lqTeam §8● §fYou can't kick yourself from the team !"); return false; }
					if(!clan.getMembers().contains(targetPlayer.getUniqueId())) { player.sendMessage("§5§lqTeam §8● §fThat player isn't in your clan!"); return false; }
					List<UUID> members = clan.getMembers();
					members.remove(targetPlayer.getUniqueId());
					QTeam.updateClanMembers(clan, members);
					player.sendMessage("§5§lqTeam §8● §fYou kicked the player successfully.");
					if(targetPlayer.isOnline()) { targetPlayer.getPlayer().sendMessage("§5§lqTeam §8● §fYou got kicked from " + clan.getTeamName()); return true; }
					QTeam.getScoreboardManager().removePlayer(clan, targetPlayer);
					ScoreboardUtil.updateScoreboard();
					return true;
					
				} else if(args[0].contentEquals("leave")) {
					
					ITeam clan = QTeam.getTeam(player);
					if(clan == null) { player.spigot().sendMessage(noTeam); return false; }
					if(clan.getOwner().equals(player.getUniqueId())) { player.spigot().sendMessage(cantLeave); return false; }
					List<UUID> members = clan.getMembers();
					members.remove(player.getUniqueId());
					QTeam.updateClanMembers(clan, members);
					
					player.spigot().sendMessage(youLeft);
					
					Player owner = Bukkit.getPlayer(clan.getOwner());
					if(owner != null && owner.isOnline()) { owner.sendMessage("§5§lqTeam §8● §f" + player.getName() + " left your team !"); return true; }
					QTeam.getScoreboardManager().removePlayer(clan, player);
					ScoreboardUtil.updateScoreboard();
					return true;
					
				} else if(args[0].contentEquals("disband")) {
					
					if(hasTeam(player)) {
						if(canDisband(player)) {
							
							ITeam clan = QTeam.getTeam(player);
							final Player clanOwner = Bukkit.getPlayer(clan.getOwner());
							
							final TextComponent disbanded = new TextComponent("§5§lqTeam §8● §fYou disbanded your team §7" + clan.getTeamName());
							disbanded.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§8● §fClick to create a different team with the same name!").create()));
							disbanded.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/team create " + clan.getTeamName() + " " + clan.getTeamTag()));
							
							final TextComponent teamDisbanded = new TextComponent("§5§lqTeam §8● §f" + clanOwner.getName() + " disbanded your team !");
							
							player.spigot().sendMessage(disbanded);
							
							clan.getMembers().forEach(uuid -> {
								OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
								if(offlinePlayer.isOnline() && !offlinePlayer.getUniqueId().equals(clan.getOwner())) { offlinePlayer.getPlayer().spigot().sendMessage(teamDisbanded); }
							});
							
							QTeam.getScoreboardManager().removeTeam(clan);
							ScoreboardUtil.updateScoreboard();
							QTeam.getDatabaseManager().deleteClan(clan.getTeamId());
							return true;
							
						} else { player.spigot().sendMessage(teamOwner); return false; }
					} else { player.spigot().sendMessage(noTeam); return false; }
				} else if(args[0].contentEquals("version")) {
					player.spigot().sendMessage(version);
					return true;
				} else { player.spigot().sendMessage(unknown); return false; }
			}
		} return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		if(arg0.getName().contentEquals("team") && arg3.length == 1) {
			return commandArg;
		} return null;
	}

	private static boolean canDisband(Player player) {
		ITeam clan = QTeam.getTeam(player);
		if(clan == null) {
			return false;
		} else {
			if(clan.getOwner() == player.getUniqueId()) {
				return true;
			}
		} return false;
	}
	
	private static boolean hasTeam(Player player) {
		ITeam clan = QTeam.getTeam(player);
		if(clan == null) { return false;
		} else return true;
	}
	
	private void printStats(Player player, ITeam clan) {
		player.sendMessage("§8● §fStats for the team §8・ §f" + clan.getTeamName() + " §8(§3" + clan.getTeamTag() + "§8)§r");
		player.sendMessage(ChatColor.GOLD + "§8· §fOwner §8» §7 " + Bukkit.getOfflinePlayer(clan.getOwner()).getName());
		player.sendMessage(ChatColor.GOLD + "§8· §fMembers §8» §7" + clan.getMembers().size());
	}

}
