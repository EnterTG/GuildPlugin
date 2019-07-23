package com.windskull.GuildPlugin;

import org.bukkit.entity.Player;

public class GuildPlayer {

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

	private Player player;
	private GuildRanks rang;
	private Guild guild;
	
	public GuildPlayer()
	{

	}

}
