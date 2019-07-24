package com.windskull.GuildPlugin;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.simpleorm.EbeanHandler;
import com.mengcraft.simpleorm.EbeanManager;
import com.windskull.DTO.DTO_Guild;
import com.windskull.DTO.DTO_GuildPlayer;
import com.windskull.Listeners.GuildPlayerJoinServerListener;
import com.windskull.Listeners.PlayerJoinListener;

public class GuildPluginMain extends JavaPlugin{

	
	public static Server server; 
	public static EbeanServer eserver;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable()
	{
		EbeanManager manager = getServer().getServicesManager()
				.getRegistration(EbeanManager.class)
				.getProvider();
		EbeanHandler handler = manager.getHandler(this);
		if (handler.isNotInitialized()) 
		{
			handler.define(DTO_GuildPlayer.class);
			handler.define(DTO_Guild.class);
			try {
				handler.initialize();
			} catch(Exception e) {
				// Do what you want to do.
				System.out.println(e.getMessage());
				return;
			}
		}
		handler.reflect();
		handler.install();
		eserver = handler.getServer();
		
		server = this.getServer();
		PluginManager pm = server.getPluginManager();
		
		pm.registerEvents(new GuildPlayerJoinServerListener(), this);
		pm.registerEvents(new PlayerJoinListener(), this);
		
	}
}
