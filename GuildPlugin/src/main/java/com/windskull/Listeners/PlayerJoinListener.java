package com.windskull.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildPlayer;
import com.windskull.GuildPlugin.GuildPluginMain;
import com.windskull.Managers.GuildsManager;

public class PlayerJoinListener implements Listener
{

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{

        GuildPlayer my = (GuildPlayer)GuildPluginMain.eserver.find(GuildPlayer.class).where().eq("playeruuid", (Object)e.getPlayer().getUniqueId()).findUnique();
        if (my != null) {
            Guild g = GuildsManager.getGuildManager().findPlayerGuild(my);
            //GuildPlayer gp = new GuildPlayer(e.getPlayer(), my.getRang(), g);
            System.out.append("G " + g);
            System.out.append("My " + my);
            g.playerLogIn(my);
        }
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		GuildPlayer gp = GuildsManager.getGuildManager().getGuildPlayer(e.getPlayer());
		if(gp != null)
		{
			gp.getGuild().playerLogOut(gp);
		}
	}
}
