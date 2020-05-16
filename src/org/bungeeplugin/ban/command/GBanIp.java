package org.bungeeplugin.ban.command;

import java.util.ArrayList;
import java.util.List;

import org.bungeeplugin.ban.BanPlugin;
import org.bungeeplugin.ban.ban.AlreadyBannedException;
import org.bungeeplugin.ban.ban.BanIp;
import org.bungeeutils.BungeeCommand;
import org.bungeeutils.io.SendMessage;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;

public class GBanIp extends BungeeCommand {
	
	public GBanIp() {
		super("gban-ip", BanPlugin.permission);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			String ip = null;
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
			if (player != null) {
				if (sender instanceof ConsoleCommandSender || !player.hasPermission(BanPlugin.permission)) ip = player.getAddress().getAddress().getHostAddress();
				else {
					SendMessage.send(sender, ChatColor.RED + "The player is a moderator");
					return;
				}
			} else {
				String[] bytes = args[0].split("\\.");
				if (bytes.length == 4) {
					for (String number : bytes) {
						try {
							int value = Integer.parseInt(number);
							if (value < 0 || value > 255) throw new IllegalArgumentException(number);
						} catch (IllegalArgumentException ex) {
							SendMessage.send(sender, ChatColor.RED + "Invalid IP address or unknown player");
							return;
						}
					}
					ip = args[0];
				} else {
					SendMessage.send(sender, ChatColor.RED + "Invalid IP address or unknown player");
					return;
				}
			}
			
			long time = 0;
			String reason = ChatColor.BLACK + "Banned by an operator";
			if (args.length >= 2) {
				try {
					time = Unit.getTime(args[1]);
				} catch (IllegalArgumentException ex) {
					SendMessage.send(sender, ChatColor.RED + "Invalid command, usage: /gban-ip OR-ip /gban <address> [forlife|time<s|m|h|d|M|y>] [reason ...]");
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
				BanIp.ban(ip, time, reason);
				SendMessage.send(sender, "Banned " + ip + ": " + reason);
			} catch (AlreadyBannedException ex) {
				SendMessage.send(sender, ChatColor.RED + "The address is already banned");
			}
		} else {
			List<BanIp> bans = BanIp.getBans();
			List<String> list = new ArrayList<String>();
			for (BanIp ban : bans) list.add(ban.getIp() + ": " + ban.getReason());
			SendMessage.sendStringList(sender, list);
			SendMessage.send(sender, "Total banned addresses: " + list.size());
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