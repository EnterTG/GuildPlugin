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
	private Map<Player, GuildPlayer> allGuildPlayers = new HashMap<Player, GuildPlayer>();
	public Map<Player, PlayerInvitations> playersInvitations = new HashMap<Player, PlayerInvitations>();
	private GuildsManager() {
	}
	public void deleteGuild(Guild g)
	{
		this.allGuilds.remove(g);
	}
	public void deleteGuildPlayer(GuildPlayer gp)
	{
		allGuildPlayers.remove(gp.getPlayer());
	}
	public void addNewGuild(Guild g) {
		this.allGuilds.add(g);
	}

	public List<Guild> getAllGuild() {
		return this.allGuilds;
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
		
		Optional<Optional<GuildPlayer>> guildPlayer = this.allGuilds.parallelStream().map( g -> g.getAllGuildPlayer().parallelStream().filter(pg -> pg.getPlayeruuid().compareTo(p.getUniqueId()) == 0).findAny() ).findAny();
		if (guildPlayer.isPresent() && guildPlayer.get().isPresent() ) {
			return guildPlayer.get().get();
		}
		return null;
	}
	public static GuildsManager getGuildManager() {
		if (gm == null) {
			gm = new GuildsManager();
		}
		return gm;
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

