package org.bungeeplugin.ban.command;

import java.util.ArrayList;
import java.util.List;

import org.bungeeplugin.ban.BanPlugin;
import org.bungeeplugin.ban.ban.AlreadyBannedException;
import org.bungeeplugin.ban.ban.BanPlayer;
import org.bungeeutils.BungeeCommand;
import org.bungeeutils.OfflinePlayer;
import org.bungeeutils.io.SendMessage;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;

public class GBan extends BungeeCommand {
	
	public GBan() {
		super("gban", BanPlugin.permission);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			OfflinePlayer offlinePlayer = new OfflinePlayer(args[0]); 
			if (offlinePlayer.getUUID() != null) {
				if (sender instanceof ConsoleCommandSender || !offlinePlayer.hasPermission(BanPlugin.permission)) {
					long time = 0;
					String reason = ChatColor.BLACK + "Banned by an operator";
					if (args.length >= 2) {
						try {
							time = Unit.getTime(args[1]);
						} catch (IllegalArgumentException ex) {
							SendMessage.send(sender, ChatColor.RED + "Invalid command, usage: /gban OR /gban <player> [forlife|time<s|m|h|d|M|y>] [reason ...]");
							return;
						}
						if (args.length >= 3) {
							reason = "";
							int i = 0;
							for (String arg : args) {
								if (i >= 2) reason = reason + " " + arg;
								i++;
							}
						}
					}
					try {
						BanPlayer.ban(offlinePlayer, time, reason);
						SendMessage.send(sender, "Banned " + offlinePlayer.getName() + ": " + reason);
					} catch (AlreadyBannedException ex) {
						SendMessage.send(sender, ChatColor.RED + "The player is already banned");
					}
				} else SendMessage.send(sender, ChatColor.RED + "The player is a moderator");
			} else SendMessage.send(sender, ChatColor.RED + "Player not found");
		} else {
			List<BanPlayer> bans = BanPlayer.getBans();
			List<String> list = new ArrayList<String>();
			for (BanPlayer ban : bans) list.add(ban.getOfflinePlayer().getName() + ": " + ban.getReason());
			SendMessage.sendStringList(sender, list);
			SendMessage.send(sender, "Total banned players: " + list.size());
		}
	}
	
	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		List<String> list = new ArrayList<String>();
		if (args.length == 1) {
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				list.add(player.getName());
			}
		} else if (args.length == 2) {
			list.add("forlife");
		}
		return list;
	}
	
}