package com.windskull.GuildPlugin;

import org.bukkit.entity.Player;

public class GuildPlayer {

	public GuildPlayer(Player player, GuildRanks rang, Guild guild) {
		super();
		this.player = player;
		this.rang = rang;
		this.guild = guild;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the rang
	 */
	public GuildRanks getRang() {
		return rang;
	}

	/**
	 * @param rang the rang to set
	 */
	public void setRang(GuildRanks rang) {
		this.rang = rang;
	}

	/**
	 * @return the guild
	 */
	public Guild getGuild() {
		return guild;
	}

	/**
	 * @param guild the guild to set
	 */
	public void setGuild(Guild guild) {
		this.guild = guild;
	}
	public int id;
	private Player player;
	private GuildRanks rang;
	private Guild guild;
	

}
