package com.windskull.GuildPlugin;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.simpleorm.EbeanHandler;
import com.mengcraft.simpleorm.EbeanManager;

public class GuildPluginMain extends JavaPlugin{

	
	public static Server server; 

	@Override
	public void onEnable()
	{
		EbeanManager manager = getServer().getServicesManager()
				.getRegistration(EbeanManager.class)
				.getProvider();
		EbeanHandler handler = manager.getHandler(this);
		if (handler.isNotInitialized()) 
		{
			//handler.define(MyClass.class);
			
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
		EbeanServer eserver = handler.getServer();
		
		server = this.getServer();
		
	}
}
