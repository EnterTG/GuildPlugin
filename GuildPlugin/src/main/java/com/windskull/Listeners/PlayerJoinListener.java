package com.windskull.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.windskull.GuildPlugin.GuildPlayer;
import com.windskull.Managers.GuildsManager;

public class PlayerJoinListener implements Listener
{

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		GuildPlayer gp = GuildsManager.getGuildManager().getGuildPlayer(e.getPlayer());
		if (gp != null)
		{
			gp.getGuild().playerLogIn(gp);
			/*
			 * GuildPlayer my = (GuildPlayer)GuildPluginMain.eserver.find(GuildPlayer.class).where().eq("playeruuid",
			 * (Object)e.getPlayer().getUniqueId()).findUnique();
			 * if (my != null) {
			 * Guild g = GuildsManager.getGuildManager().findPlayerGuild(my);
			 * //GuildPlayer gp = new GuildPlayer(e.getPlayer(), my.getRang(), g);\
			 * System.out.println();
			 * System.out.println("G " + g);
			 * System.out.println("My " + my);
			 * System.out.println();
			 * g.playerLogIn(my);
			 * }
			 */
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		GuildPlayer gp = GuildsManager.getGuildManager().getGuildPlayer(e.getPlayer());
		if (gp != null)
		{
			gp.getGuild().playerLogOut(gp);
		}
	}
}
