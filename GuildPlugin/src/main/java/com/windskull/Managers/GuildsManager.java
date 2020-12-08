package com.windskull.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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


	private Map<UUID, GuildPlayer> allGuildPlayers = new HashMap<UUID, GuildPlayer>();
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
		allGuildPlayers.remove(gp.getPlayeruuid());
	}
	public void addNewGuild(Guild g) 
	{
		this.allGuilds.add(g);
		g.getAllGuildPlayer().forEach(gp -> allGuildPlayers.put(gp.getPlayeruuid(), gp));
		this.allGuildsManagers.put(g, new GuildManager(g));
	}

	public List<Guild> getAllGuild() {
		return this.allGuilds;
	}

	public Map<UUID, GuildPlayer> getAllGuildPlayers() {
		return allGuildPlayers;
	}
	public void setAllGuildPlayers(Map<UUID, GuildPlayer> allGuildPlayers) {
		this.allGuildPlayers = allGuildPlayers;
	}
	
	
	
	public GuildManager getGuildManager(Guild g)
	{
		if(allGuildsManagers.containsKey(g) )
			return allGuildsManagers.get(g);
		else
		{
			allGuildsManagers.put(g, new GuildManager(g));
			return allGuildsManagers.get(g);
		}
	}
	public Guild getPlayerGuild(Player p)
	{
		GuildPlayer gp = getGuildPlayer(p);
		if(gp != null) return gp.getGuild();
		return null;
	}
	
	public void addGuildPlayer(GuildPlayer gp)
	{
		allGuildPlayers.put(gp.getPlayeruuid(), gp);
	}
	
	public GuildPlayer getGuildPlayer(Player p)
	{
		return allGuildPlayers.get(p.getUniqueId());
	}

	//Check guilds name ana tag
		
	public boolean isGuildExist(String s) {
		return this.allGuilds.parallelStream().anyMatch(g -> g.getName().equals(s));
	}
	public boolean isTAGExist(String s) {
		return this.allGuilds.parallelStream().anyMatch(g -> g.getTag().equals(s));
	}

	//Guild invitations
	
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

