package org.bungeeplugin.ban.ban;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bungeeplugin.ban.BanPlugin;
import org.bungeeutils.OfflinePlayer;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class BanPlayer {
	
	protected OfflinePlayer offlinePlayer;
	protected long time;
	protected String reason;
	
	private BanPlayer(OfflinePlayer offlinePlayer, long time, String reason) {
		this.offlinePlayer = offlinePlayer;
		this.time = time;
		this.reason = reason;
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return offlinePlayer;
	}
	
	public long getTime() {
		return time;
	}
	
	public String getReason() {
		return reason;
	}
	
	
	public static BanPlayer getBan(OfflinePlayer banned) {
		UUID playerUUID = banned.getUUID();
		Configuration config = BanPlugin.bans.getAsYaml();
		
		if (config.contains("player." + playerUUID.toString())) {
			Configuration section = config.getSection("player." + playerUUID.toString());
			if (section.contains("name") && section.contains("time") && section.contains("reason")) {
				long time = section.getLong("time");
				if (new Date().getTime() < time || time == 0) {
					return new BanPlayer(new OfflinePlayer(section.getString("name")), time, section.getString("reason"));
				} else {
					try {
						pardon(banned);
					} catch (NotBannedException ex) {}
					return null;
				}
			}
		}
		
		return null;
	}
	
	public static List<BanPlayer> getBans() {
		List<BanPlayer> bans = new ArrayList<BanPlayer>();
		
		Configuration config = BanPlugin.bans.getAsYaml();
		if (config.contains("player")) {
			for (String player : config.getSection("player").getKeys()) {
				UUID uuid = UUID.fromString(player);
				Configuration section = config.getSection("player." + uuid.toString());
				if (section.contains("name") && section.contains("time") && section.contains("reason")) {
					OfflinePlayer banned = new OfflinePlayer(section.getString("name"));
					long time = section.getLong("time");
					if (new Date().getTime() < time || time == 0) {
						bans.add(new BanPlayer(banned, time, section.getString("reason")));
					} else {
						try {
							pardon(banned);
						} catch ( NotBannedException ex) {}
					}
				}
			}
		}
		
		return bans;
	}
	
	public static void ban(OfflinePlayer banned, long time, String reason) throws AlreadyBannedException {
		UUID playerUUID = banned.getUUID();
		if (time == 0 || new Date().getTime() < time) {
			Configuration config = BanPlugin.bans.getAsYaml();
			if (config.contains("player." + playerUUID.toString())) {
				Configuration section = config.getSection("player." + playerUUID.toString());
				if (section.contains("name") && section.contains("time") && section.contains("reason")) throw new AlreadyBannedException();
			}
			
			config.set("player." + playerUUID.toString() + ".name", banned.getName());
			config.set("player." + playerUUID.toString() + ".time", time);
			config.set("player." + playerUUID.toString() + ".reason", reason);
			BanPlugin.bans.save();
			
			ProxiedPlayer player = banned.getProxiedPlayer();
			if (player != null) player.disconnect(new TextComponent("You are banned from this server"));
		} else throw new IllegalArgumentException();
	}
	
	public static void pardon(OfflinePlayer banned) throws NotBannedException {
		UUID playerUUID = banned.getUUID();
		
		Configuration config = BanPlugin.bans.getAsYaml();
		if (config.contains("player." + playerUUID.toString())) {
			Configuration section = config.getSection("player." + playerUUID.toString());
			if (section.contains("name") && section.contains("time") && section.contains("reason")) {
				config.set("player." + playerUUID.toString(), null);
				BanPlugin.bans.save();
			} else throw new NotBannedException();
		} else throw new NotBannedException();
	}
	
}