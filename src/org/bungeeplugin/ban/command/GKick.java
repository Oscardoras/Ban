package org.bungeeplugin.ban.command;

import java.util.ArrayList;
import java.util.List;

import org.bungeeplugin.ban.BanPlugin;
import org.bungeeutils.BungeeCommand;
import org.bungeeutils.io.SendMessage;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;

public class GKick extends BungeeCommand {
	
	public GKick() {
		super("gkick", BanPlugin.permission);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
			if (player != null) {
				if (sender instanceof ConsoleCommandSender || !player.hasPermission(BanPlugin.permission)) {
					String reason;
					if (args.length >= 2) {
						reason = "";
						int i = 0;
						for (String arg : args) {
							if (i >= 1) reason = reason + " " + arg;
							i++;
						}
					} else reason = ChatColor.BLACK + "Kicked by an operator";
					player.disconnect(new TextComponent(reason));
					SendMessage.send(sender, "Kicked " + player.getName() + ": " + reason);
				} else SendMessage.send(sender, ChatColor.RED + "The player is a moderator");
			} else SendMessage.send(sender, ChatColor.RED + "That user is not online");
		} else SendMessage.send(sender, ChatColor.RED + "Not enough arguments, usage: /gkick <player> [<reason ...>]");
	}
	
	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		List<String> list = new ArrayList<String>();
		if (args.length == 1) {
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				list.add(player.getName());
			}
		}
		return list;
	}
	
}