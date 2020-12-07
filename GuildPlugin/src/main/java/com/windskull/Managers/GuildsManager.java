package com.windskull.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;

import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildPlayer;

import net.md_5.bungee.api.ChatColor;

public class GuildsManager {
	
	public static String _GuidlPermissionItemName = "Prawo zajecia ziemi";
	public static String _GlobalPrefix = ChatColor.translateAlternateColorCodes('&',"&6[&7Guilds&6]&7 ");
	public static ChatColor _ItemsColorNamePrimal = ChatColor.GRAY;
	public static ChatColor _ItemsColorNameSecond = ChatColor.GOLD;
	
	public static final int _MaxGuildLvl = 2;
	
	private static GuildsManager gm;
	private List<Guild> allGuilds = new ArrayList<Guild>();
	private Map<Guild,GuildManager> allGuildsManagers = new HashMap<Guild, GuildManager>();


	private Map<Player, GuildPlayer> allGuildPlayers = new HashMap<Player, GuildPlayer>();
	public Map<Player, PlayerInvitations> playersInvitations = new HashMap<Player, PlayerInvitations>();
	
	
	private GuildsManager() 
	{
		
	}
	
	public static GuildsManager getGuildManager() {
		if (gm == null) {
			gm = new GuildsManager();
		}
		return gm;
	}
	
	
	public void deleteGuild(Guild g)
	{
		this.allGuildsManagers.remove(g);
		this.allGuilds.remove(g);
	}
	public void deleteGuildPlayer(GuildPlayer gp)
	{
		allGuildPlayers.remove(gp.getPlayer());
	}
	public void addNewGuild(Guild g) 
	{
		this.allGuilds.add(g);
		this.allGuildsManagers.put(g, new GuildManager(g));
	}

	public List<Guild> getAllGuild() {
		return this.allGuilds;
	}

	public Map<Player, GuildPlayer> getAllGuildPlayers() {
		return allGuildPlayers;
	}
	public void setAllGuildPlayers(Map<Player, GuildPlayer> allGuildPlayers) {
		this.allGuildPlayers = allGuildPlayers;
	}
	public GuildManager getGuildManager(Guild g)
	{
		if( allGuildsManagers.containsKey(g) )
			return allGuildsManagers.get(g);
		else
		{
			allGuildsManagers.put(g, new GuildManager(g));
			return allGuildsManagers.get(g);
		}
		//return allGuildsManagers.get(g);
	}
	
	public Guild findPlayerGuild(GuildPlayer p) {
		Optional<Guild> guild = this.allGuilds.parallelStream().filter(g -> g.getAllGuildPlayer().parallelStream().anyMatch(pg -> pg.getPlayeruuid().compareTo(p.getPlayeruuid()) == 0)).findFirst();
		if (guild.isPresent()) {
			return guild.get();
		}
		return null;
	}
	public Guild findPlayerGuild(Player p) {
		Optional<Guild> guild = this.allGuilds.parallelStream().filter(g -> g.getAllGuildPlayer().parallelStream().anyMatch(pg -> pg.getPlayeruuid().compareTo(p.getUniqueId()) == 0)).findFirst();
		if (guild.isPresent()) {
			return guild.get();
		}
		return null;
	}
	
	
	
	public GuildPlayer getGuildPlayer(Player p)
	{
		/*if(allGuildPlayers.containsKey(p)) return allGuildPlayers.get(p);
		else
		{
			GuildPlayer my = (GuildPlayer)GuildPluginMain.eserver.find(GuildPlayer.class).where().eq("playeruuid", (Object)p.getUniqueId()).findUnique();
			if(my != null)
			{
				allGuildPlayers.put(p, my);
				return my;
			}
			else
				return null;
		}*/
		
		
		
		//UUID playerUUID = p.getUniqueId();
		//this.allGuilds.parallelStream().filter(g -> g.getAllOnlineGuildPlayers().parallelStream().filter(gp -> gp.getPlayeruuid().equals(playerUUID)).findFirst().isPresent()).findFirst();
		if(allGuildPlayers.containsKey(p)) return allGuildPlayers.get(p);
		Optional<Optional<GuildPlayer>> guildPlayer = this.allGuilds.parallelStream().map( g -> g.getAllGuildPlayer().parallelStream().filter(pg -> pg.getPlayeruuid().equals(p.getUniqueId())).findAny() ).findAny();
		if (guildPlayer.isPresent() && guildPlayer.get().isPresent() ) {
			allGuildPlayers.put(p, guildPlayer.get().get());
			return guildPlayer.get().get();
		}
		return null;
	}


	public void addGuildPlayer(Player p, GuildPlayer gp) {
		this.allGuildPlayers.put(p, gp);
	}

	/*public GuildPlayer getGuildPlayer(Player p) {
		return this.allGuildPlayers.get((Object)p);
	}*/
		
	public boolean isGuildExist(String s) {
		return this.allGuilds.parallelStream().anyMatch(g -> g.getName().equals(s));
	}
	public boolean isTAGExist(String s) {
		return this.allGuilds.parallelStream().anyMatch(g -> g.getTag().equals(s));
	}
	public Map<Guild, GuildManager> getAllGuildsManagers() {
		return allGuildsManagers;
	}
	public void setAllGuildsManagers(Map<Guild, GuildManager> allGuildsManagers) {
		this.allGuildsManagers = allGuildsManagers;
	}
	public void addGuildInvitation(Player p, Guild g)
	{
		if(playersInvitations.containsKey(p)) playersInvitations.get(p).addInvitation(g);
		else {
			PlayerInvitations pi = new PlayerInvitations();
			pi.addInvitation(g);
			playersInvitations.put(p, pi);
		}
	}
	
	public PlayerInvitations getPlayerInvitation(Player p)
	{
		return playersInvitations.get(p);
	}
	
	public int getInvitationSize(Player p)
	{
		return playersInvitations.containsKey(p) ? playersInvitations.get(p).getInvitationAmount() : 0;
	}
}

