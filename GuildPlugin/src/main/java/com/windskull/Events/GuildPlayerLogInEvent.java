package com.windskull.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildPlayer;

public class GuildPlayerLogInEvent extends Event
{

	/**
	 * @return the player
	 */
	public GuildPlayer getPlayer()
	{
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(GuildPlayer player)
	{
		this.player = player;
	}

	/**
	 * @return the guild
	 */
	public Guild getGuild()
	{
		return guild;
	}

	/**
	 * @param guild the guild to set
	 */
	public void setGuild(Guild guild)
	{
		this.guild = guild;
	}

	private GuildPlayer player;
	private Guild guild;

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

}
