package me.oscardoras.ban;

import me.oscardoras.ban.ban.BanIp;
import me.oscardoras.ban.ban.BanPlayer;
import me.oscardoras.ban.command.GBan;
import me.oscardoras.ban.command.GBanIp;
import me.oscardoras.ban.command.GKick;
import me.oscardoras.ban.command.GPardon;
import me.oscardoras.ban.command.GPardonIp;
import me.oscardoras.bungeeutils.BungeePlugin;
import me.oscardoras.bungeeutils.OfflinePlayer;
import me.oscardoras.bungeeutils.io.DataFile;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;

public final class BanPlugin extends BungeePlugin {
	
	public static BanPlugin plugin;
	
	public BanPlugin() {
		plugin = this;
	}
	
	
	public final static DataFile bans = new DataFile("plugins/Ban/bans.yml");
	public final static String permission = "ban.moderation";
	
	@Override
	public void onEnable() {
		PluginManager manager = ProxyServer.getInstance().getPluginManager();
		manager.registerListener(this, this);
		
		manager.registerCommand(this, new GBan());
		manager.registerCommand(this, new GBanIp());
		manager.registerCommand(this, new GKick());
		manager.registerCommand(this, new GPardon());
		manager.registerCommand(this, new GPardonIp());
	}
	
	@EventHandler
	public void onLogin(LoginEvent e) {
		PendingConnection player = e.getConnection();
		String reason = null;
		
		BanIp banIp = BanIp.getBan(player.getAddress().getAddress().getHostAddress());
		if (banIp != null) reason = banIp.getReason();
		OfflinePlayer offlinePlayer = new OfflinePlayer(player.getName());
		if (offlinePlayer.getUUID() != null) {
			BanPlayer banPlayer = BanPlayer.getBan(offlinePlayer);
			if (banPlayer != null) reason = banPlayer.getReason();
		}
		
		if (reason != null) {
			e.setCancelled(true);
			e.setCancelReason(new TextComponent("You are banned from this server.\nReason: " + reason));
		}
	}
	
}