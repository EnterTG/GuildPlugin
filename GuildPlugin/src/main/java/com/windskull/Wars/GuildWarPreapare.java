package com.windskull.Wars;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildPlayer;
import com.windskull.GuildPlugin.GuildPluginMain;
import com.windskull.Inventory.Inventories.Inventory_GuildMenu_WarPanel;
import com.windskull.Managers.GuildsManager;

public class GuildWarPreapare implements Listener
{
	private static final int queuePlayerNeed = 1;
	
	private final Guild guild;
	private List<GuildPlayer> queuePlayers = new ArrayList<>(); 
	
	private boolean locked = false;
	
	private List<Inventory_GuildMenu_WarPanel> watchers = new ArrayList<Inventory_GuildMenu_WarPanel>();
	
	public GuildQueueStatus guildQueueStatus = GuildQueueStatus.NOINQUEUE;
	
	public GuildWarPreapare(Guild g) 
	{
		this.guild = g;
		
	}
	
	public GuildPlayer[] getPlayersQueue()
	{
		return queuePlayers.toArray(new GuildPlayer[queuePlayers.size()]);
	}
	
	
	public boolean isInQueue(GuildPlayer player)
	{
		return queuePlayers.contains(player);
	}
	
	public void register(Inventory_GuildMenu_WarPanel wp)
	{
		watchers.add(wp);
	}
	
	public void unregister(Inventory_GuildMenu_WarPanel wp)
	{
		watchers.remove(wp);
	}
	
	public void notifyAllInventorys()
	{
		watchers.forEach(e -> e.updateQueue());
	}
	
	public synchronized PlayerQueueJoinResponse joinLeaveQueue(GuildPlayer player)
	{
		if(!locked)
		{
			PlayerQueueJoinResponse b;
			if(queuePlayers.contains(player)) 
			{
				queuePlayers.remove(player); 
				b = PlayerQueueJoinResponse.LEAVE;
			}
			else
			{
				queuePlayers.add(player); 
				b = PlayerQueueJoinResponse.JOINED;
			}
			notifyAllInventorys();
			return b;
		}
		return PlayerQueueJoinResponse.CANTJOIN;
	}
	
	public void startQueue()
	{
		if(queuePlayers.size() == queuePlayerNeed)
		{
			locked = true;
			//startQueue();
			//System.out.println("Add guild prepare to queue: " + getGuild());
			GuildPluginMain.server.getPluginManager().registerEvents(this, GuildPluginMain.main);
			GuildsWarsQueue.getInstance().joinQueue(this);
		}
		
	}
	public void stopQueue()
	{
		locked = false;
		PlayerQuitEvent.getHandlerList().unregister(this);
		GuildsWarsQueue.getInstance().leaveQueue(this);
	}
	 
	public boolean isLocked()
	{
		return locked;
	}
	public Guild getGuild()
	{
		return guild;
	}
	
	public void updateMMR()
	{
		watchers.forEach(w -> w.updateMMR());
	}
	
	/*
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if(queuePlayers.size() > 0)
		{
			Optional<GuildPlayer> player = queuePlayers.stream().filter(gp -> gp.getPlayeruuid().equals(e.getPlayer().getUniqueId())).findFirst();
			if(player.isPresent())
			{
				player.get().init();
			}
		}
	}*/
	
	public void onPlayerLeave(PlayerQuitEvent e)
	{
		Optional<GuildPlayer> player = queuePlayers.stream().filter(gp -> gp.getPlayeruuid().equals(e.getPlayer().getUniqueId())).findFirst();		
		if(player.isPresent()) 
		{
			switch (guildQueueStatus) 
			{
				case INQUEUE:
					GuildsWarsQueue.getInstance().leaveQueue(this);
					queuePlayers.forEach(pl -> { if(pl.getPlayer() != null) pl.getPlayer().sendMessage(GuildsManager._ItemsColorNamePrimal + "Opuszczono kolejke");});
					break;
				default: 
					break;
			}
			queuePlayers.remove(player.get());
		}
	}
	
	@Override
	public int hashCode() {

		return guild.hashCode();
	}
}
