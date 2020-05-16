package org.bungeeplugin.ban.command;

import java.util.ArrayList;
import java.util.List;

import org.bungeeplugin.ban.BanPlugin;
import org.bungeeplugin.ban.ban.BanIp;
import org.bungeeplugin.ban.ban.NotBannedException;
import org.bungeeutils.BungeeCommand;
import org.bungeeutils.io.SendMessage;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class GPardonIp extends BungeeCommand {
	
	public GPardonIp() {
		super("gpardon-ip", BanPlugin.permission, "gpardon-ip [adress]");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			String ip = args[0];
			try {
				BanIp.pardon(ip);
				SendMessage.send(sender, "Pardoned " + ip);
			} catch (NotBannedException ex) {
				SendMessage.send(sender, ChatColor.RED + "The address isn't banned");
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
			for (BanIp banIp : BanIp.getBans()) list.add(banIp.getIp());
		}
		return list;
	}
	
}