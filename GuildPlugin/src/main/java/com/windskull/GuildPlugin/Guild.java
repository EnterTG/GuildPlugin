package com.windskull.GuildPlugin;

import java.util.ArrayList;
import java.util.List;

import com.windskull.DTO.DTO_GuildPlayer;
import com.windskull.Events.GuildPlayerLogInEvent;

public class Guild {

	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the opis
	 */
	public String getOpis() {
		return description;
	}

	/**
	 * @param opis the opis to set
	 */
	public void setOpis(String opis) {
		this.description = opis;
	}

	/**
	 * @return the allGuildPlayers
	 */
	public List<GuildPlayer> getAllGuildPlayers() {
		return allGuildPlayers;
	}

	/**
	 * @param allGuildPlayers the allGuildPlayers to set
	 */
	public void setAllGuildPlayers(List<GuildPlayer> allGuildPlayers) {
		this.allGuildPlayers = allGuildPlayers;
	}
	/**
	 * @return the allOfflinePlayer
	 */
	public List<DTO_GuildPlayer> getAllOfflinePlayer() {
		return allOfflinePlayer;
	}

	/**
	 * @param allOfflinePlayer the allOfflinePlayer to set
	 */
	public void setAllOfflinePlayer(List<DTO_GuildPlayer> allOfflinePlayer) {
		this.allOfflinePlayer = allOfflinePlayer;
	}
	
	
	private String name;
	private String tag;
	private String description;
	
	
	private List<GuildPlayer> allGuildPlayers;
	private List<DTO_GuildPlayer> allOfflinePlayer;


	public Guild() 
	{
		allGuildPlayers = new ArrayList<GuildPlayer>();
		allOfflinePlayer = new ArrayList<>();
	}

	public void playerLogIn(DTO_GuildPlayer dtogp ,GuildPlayer g)
	{
		allGuildPlayers.add(g);
		allOfflinePlayer.remove(dtogp);
		GuildPlayerLogInEvent e = new GuildPlayerLogInEvent();
		e.setGuild(this);
		e.setPlayer(g);
		
		GuildPluginMain.server.getPluginManager().callEvent(e);
	}
	
	
	
	
	public void addNewPlayer(GuildPlayer gp)
	{
		allGuildPlayers.add(gp);
	}
	
}
