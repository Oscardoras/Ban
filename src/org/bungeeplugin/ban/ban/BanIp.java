package org.bungeeplugin.ban.ban;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bungeeplugin.ban.BanPlugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class BanIp {
	
	protected String ip;
	protected long time;
	protected String reason;
	
	private BanIp(String ip, long time, String reason) {
		this.ip = ip;
		this.time = time;
		this.reason = reason;
	}
	
	public String getIp() {
		return ip;
	}
	
	public long getTime() {
		return time;
	}
	
	public String getReason() {
		return reason;
	}
	
	
	public static BanIp getBan(String banned) {
		String ip = banned.replaceAll("\\.", "_");
		
		Configuration config = BanPlugin.bans.getAsYaml();
		if (config.contains("ip." + ip)) {
			Configuration section = config.getSection("ip." + ip);
			if (section.contains("time") && section.contains("reason")) {
				long time = section.getLong("time");
				if (new Date().getTime() < time || time == 0) return new BanIp(banned, time, section.getString("reason"));
				else {
					try {
						pardon(banned);
					} catch (NotBannedException ex) {}
				}
			}
		}
		
		return null;
	}
	
	public static List<BanIp> getBans() {
		List<BanIp> bans = new ArrayList<BanIp>();
		
		Configuration config = BanPlugin.bans.getAsYaml();
		for (String ip : config.getSection("ip").getKeys()) {
			BanIp ban = getBan(ip.replaceAll("_", "\\."));
			if (ban != null) bans.add(ban);
		}
		
		return bans;
	}
	
	public static void ban(String banned, long time, String reason) throws AlreadyBannedException {
		String ip = banned.replaceAll("\\.", "_");
		if (time == 0 || new Date().getTime() < time) {
			
			Configuration config = BanPlugin.bans.getAsYaml();
			if (!config.contains("ip." + ip + ".time") || !config.contains("ip." + ip + ".reason")) {
				config.set("ip." + ip + ".time", time);
				config.set("ip." + ip + ".reason", reason);
				BanPlugin.bans.save();
			} else throw new AlreadyBannedException();
			
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				if (player.getAddress().getHostString().equals(banned)) {
					player.disconnect(new TextComponent("You are banned from this server"));
				}
			}
		} else throw new IllegalArgumentException();
	}
	
	public static void pardon(String banned) throws NotBannedException {
		String ip = banned.replaceAll("\\.", "_");
		
		Configuration config = BanPlugin.bans.getAsYaml();
		if (config.contains("ip." + ip + ".time") && config.contains("ip." + ip + ".reason")) {
			config.set("ip." + ip, null);
			BanPlugin.bans.save();
		} else throw new NotBannedException();
	}
	
}