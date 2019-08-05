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
import com.windskull.Converters.DTOConverter;
import com.windskull.DTO.DTO_Guild;
import com.windskull.DTO.DTO_GuildPlayer;
import com.windskull.Listeners.GuildPlayerJoinServerListener;
import com.windskull.Listeners.PlayerJoinListener;
import com.windskull.Managers.GuildsManager;

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
		
		
		loadGuild();
		
		disaplayAllGuilds();
	}
	
	@Override
	public void onDisable()
	{
		GuildsManager.getGuildManager().getAllGuild().forEach(g -> eserver.update(DTOConverter.convertGuildToDTO(g)));
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		
			Player p = (Player)sender;
			switch (args[0]) {
			case "create":
				if(args.length >= 4)
				{
					Guild g = new Guild();
					g.setName(args[1]);
					g.setOpis(args[2]);
					g.setTag(args[3]);
					
					GuildPlayer gp = new GuildPlayer(p,GuildRanks.Owner,g);
					g.addNewPlayer(gp);
					
					DTO_Guild dg = DTOConverter.convertGuildToDTO(g);
					eserver.save(dg);
					GuildsManager.getGuildManager().addNewGuild(DTOConverter.getGuildFromDTO(dg));
					System.out.print("ID: " + dg.getId());
				
				}
				break;
			case "zmien":
				Guild guild = GuildsManager.getGuildManager().getGuildPlayer(p).getGuild();
				guild.setOpis("TEST");
				disaplayAllGuilds();
			default:
				break;
			}
		
		
		
		return super.onCommand(sender, command, label, args);
	}



	private void loadGuild()
	{
		GuildsManager gm = GuildsManager.getGuildManager();
		eserver.find(DTO_Guild.class).findList().forEach( (DTO_Guild dg) -> gm.addNewGuild(DTOConverter.getGuildFromDTO(dg)));
	}
	
	
	private void disaplayAllGuilds()
	{
		GuildsManager gm = GuildsManager.getGuildManager();
		gm.getAllGuild().forEach( g -> System.out.println(g.toString()));
		
	}
}
