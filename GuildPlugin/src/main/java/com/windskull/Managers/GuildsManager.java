package com.windskull.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.windskull.DTO.DTO_GuildPlayer;
import com.windskull.GuildPlugin.Guild;

public class GuildsManager {

	private static GuildsManager gm;
	
	private GuildsManager()
	{
		
	}
	
	private  List<Guild> allGuilds = new ArrayList<>();

	public void addNewGuild(Guild g)
	{
		allGuilds.add(g);
	}
	
	public List<Guild> getAllGuild()
	{
		return allGuilds;
	}
	
	public Guild findPlayerGuild(DTO_GuildPlayer p)
	{
		Optional<Guild> guild = allGuilds.parallelStream().filter( g -> g.getAllOfflinePlayer().parallelStream().anyMatch(pg -> pg.equals(p))).findFirst();
		if(guild.isPresent())
			return guild.get();
		else
			return null;
	}
	
	
	public static GuildsManager getGuildManager()
	{
		if(gm == null) gm = new GuildsManager();
		return gm;
	}
	
}
