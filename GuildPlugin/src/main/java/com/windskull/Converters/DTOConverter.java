package com.windskull.Converters;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.windskull.DTO.DTO_Guild;
import com.windskull.DTO.DTO_GuildPlayer;
import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildPlayer;
import com.windskull.GuildPlugin.GuildPluginMain;

public class DTOConverter {

	
	
	public static Guild getGuildFromDTO(DTO_Guild guild)
	{
		Guild g = new Guild();
		
		g.setName(guild.getName());
		g.setOpis(guild.getOpis());
		g.setTag(guild.getTag());
		g.setAllOfflinePlayer(guild.getAllGuildPlayer());
		return g;
	}
	
	
	public GuildPlayer getGuildPlayer(Guild guild,DTO_GuildPlayer gplayer)
	{
		Server server = GuildPluginMain.server;
		
		Player p = server.getPlayer(gplayer.getPlayeruuid());
		if(p != null)
		{
			GuildPlayer gp = new GuildPlayer();
			gp.setGuild(guild);
			gp.setPlayer(p);
			gp.setRang(gplayer.getRang());
			return gp;
		}
		else
			return null;
	}
	
	
	
}
