package com.windskull.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.windskull.Converters.DTOConverter;
import com.windskull.DTO.DTO_GuildPlayer;
import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildPlayer;
import com.windskull.GuildPlugin.GuildPluginMain;
import com.windskull.Managers.GuildsManager;

public class PlayerJoinListener implements Listener
{

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		DTO_GuildPlayer my = GuildPluginMain.eserver.find(DTO_GuildPlayer.class).where().eq("playeruuid", e.getPlayer().getUniqueId()).findUnique();
		if(my != null)
		{
			Guild g = GuildsManager.getGuildManager().findPlayerGuild(my);
			
			GuildPlayer gp = DTOConverter.getGuildPlayer(g, my);
			//gp.id = my.getId();
			g.playerLogIn(my, gp);
			GuildsManager.getGuildManager().addGuildPlayer(e.getPlayer(),gp);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		GuildPlayer gp = GuildsManager.getGuildManager().getGuildPlayer(e.getPlayer());
		if(gp != null)
		{
			DTO_GuildPlayer dgp =DTOConverter.convertGuildPlayerToDTO(DTOConverter.convertGuildToDTO(gp.getGuild()), gp);
			gp.getGuild().playerLogOut(gp, dgp);
		}
	}
}
