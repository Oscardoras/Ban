package org.bungeeplugin.ban.command;

import java.util.ArrayList;
import java.util.List;

import org.bungeeplugin.ban.BanPlugin;
import org.bungeeplugin.ban.ban.BanPlayer;
import org.bungeeplugin.ban.ban.NotBannedException;
import org.bungeeutils.BungeeCommand;
import org.bungeeutils.OfflinePlayer;
import org.bungeeutils.io.SendMessage;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class GPardon extends BungeeCommand {
	
	public GPardon() {
		super("gpardon", BanPlugin.permission, "gpardon [<player>]");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			OfflinePlayer offlinePlayer = new OfflinePlayer(args[0]);
			if (offlinePlayer.getUUID() != null) {
				try {
					BanPlayer.pardon(offlinePlayer);
					SendMessage.send(sender, "Pardoned " + offlinePlayer.getName());
				} catch (NotBannedException ex) {
					SendMessage.send(sender, ChatColor.RED + "The player isn't banned");
				}
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
			for (BanPlayer ban : BanPlayer.getBans()) list.add(ban.getOfflinePlayer().getName());
		}
		return list;
	}
	
}