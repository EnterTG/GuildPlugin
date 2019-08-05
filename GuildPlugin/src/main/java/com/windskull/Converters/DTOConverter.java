package com.windskull.Converters;

import java.util.List;

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
		g.id = guild.getId();
		g.setAllOfflinePlayer(guild.getAllGuildPlayer());
		return g;
	}
	
	
	public static GuildPlayer getGuildPlayer(Guild guild,DTO_GuildPlayer gplayer)
	{
		Server server = GuildPluginMain.server;
		
		Player p = server.getPlayer(gplayer.getPlayeruuid());
		if(p != null)
		{
			GuildPlayer gp = new GuildPlayer(p,gplayer.getRang(),guild);
			gp.id = gplayer.getId();
			return gp;
		}
		else
			return null;
	}
	
	public static DTO_Guild convertGuildToDTO(Guild g)
	{
		DTO_Guild dg = new DTO_Guild();
		dg.setName(g.getName());
		dg.setTag(g.getTag());
		dg.setOpis(g.getOpis());
		dg.setId(g.id);
		List<DTO_GuildPlayer> allguildpalyers = g.getAllOfflinePlayer();
		g.getAllGuildPlayers().forEach(p -> allguildpalyers.add(convertGuildPlayerToDTO(dg,p)));
		dg.setAllGuildPlayer(allguildpalyers);
		return dg;
	}
	
	
	public static DTO_GuildPlayer convertGuildPlayerToDTO(DTO_Guild dguild,GuildPlayer gp)
	{
		DTO_GuildPlayer dgp = new DTO_GuildPlayer();
		dgp.setPlayeruuid(gp.getPlayer().getUniqueId());
		dgp.setRang(gp.getRang());
		dgp.setGuild(dguild);
		dgp.setId(gp.id);
		return dgp;
	}
	
	
	
	
}
