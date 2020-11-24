package com.windskull.GuildPlugin;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.simpleorm.EbeanHandler;
import com.mengcraft.simpleorm.EbeanManager;
import com.windskull.Inventory.Inventories.Inventory_GuildMenu_Member;
import com.windskull.Inventory.Inventories.Inventory_GuildMenu_NewPlayer;
import com.windskull.Inventory.Inventories.Inventory_GuildMenu_Owner;
import com.windskull.Listeners.GuildPlayerJoinServerListener;
import com.windskull.Listeners.InventoryActionListener;
import com.windskull.Listeners.PlayerJoinListener;
import com.windskull.Managers.GuildsManager;

public class GuildPluginMain extends JavaPlugin{

	

	public static Server server; 
	public static EbeanServer eserver;
	public static JavaPlugin main;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable()
	{
		main = this;
		;
		EbeanManager manager = getServer().getServicesManager()
				.getRegistration(EbeanManager.class)
				.getProvider();
		EbeanHandler handler = manager.getHandler(this);//EbeanManager.DEFAULT.getHandler(this);//
		if (handler.isNotInitialized()) 
		{
			handler.define(GuildPlayer.class);
			handler.define(Guild.class);
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
		pm.registerEvents(new InventoryActionListener(), this);
		
		
		loadGuild();
		
		disaplayAllGuilds();
	}
	
	@Override
	public void onDisable()
	{
		GuildsManager.getGuildManager().getAllGuild().forEach(g -> eserver.update(g));
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		Player player = ((Player)sender);
		GuildPlayer guildPlayer = GuildsManager.getGuildManager().getGuildPlayer(player);
		if (guildPlayer == null) 
			player.openInventory(new Inventory_GuildMenu_NewPlayer(player).getInventory());
		else if(guildPlayer.getRang().equals(GuildRanks.Owner))
			player.openInventory(new Inventory_GuildMenu_Owner(player).getInventory());
		else
			player.openInventory(new Inventory_GuildMenu_Member(player).getInventory());
		
		
		//((Player)sender).openInventory(new Inventory_GuildMenu((Player)sender).getInventory());
		return super.onCommand(sender, command, label, args);
	}



	private void loadGuild()
	{
		GuildsManager gm = GuildsManager.getGuildManager();
		eserver.find(Guild.class).findList().forEach( (Guild dg) -> gm.addNewGuild(dg));
	}
	
	
	private void disaplayAllGuilds()
	{
		GuildsManager gm = GuildsManager.getGuildManager();
		gm.getAllGuild().forEach( g -> System.out.println(g.toString()));
		
	}
}
