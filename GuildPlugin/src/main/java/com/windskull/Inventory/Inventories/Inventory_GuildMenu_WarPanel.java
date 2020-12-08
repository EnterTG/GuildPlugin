package com.windskull.Inventory.Inventories;

import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.windskull.GuildPlugin.GuildPlayer;
import com.windskull.GuildPlugin.GuildRanks;
import com.windskull.Items.ItemsCreator;
import com.windskull.Managers.GuildsManager;
import com.windskull.Wars.GuildWarPreapare;

public class Inventory_GuildMenu_WarPanel extends Inventory_GuildMenu {

	public Inventory_GuildMenu_WarPanel(Player p) 
	{
		super(p);
		guildPlayer.getGuild().guildWars.register(this);
		createInventory();
		
	}
	
	private static final int[] warsFields = {10,11,12,13,14};
	

	
	
	@Override
	public boolean onInventoryClose(Player whoClosed) 
	{
		guildPlayer.getGuild().guildWars.unregister(this);
		return super.onInventoryClose(whoClosed);
		
	}
		
	public void createInventory()
	{
		updateQueue();
		updateJoinLeaveButton();
		updateMMR();
		
		if(guildPlayer.getRang().equals(GuildRanks.Owner) || guildPlayer.getRang().equals(GuildRanks.Capitan) )
		{
			this.setItem(17, ItemsCreator.getItemStack(Material.GRINDSTONE , getQueueStatusName()), e -> startMatchSearch());
		}
	}

	private String getQueueStatusName()
	{
		GuildWarPreapare gwp =guildPlayer.getGuild().guildWars;
		switch (gwp.guildQueueStatus) {
			case INMATCH:
				return GuildsManager._ItemsColorNamePrimal + "Mecz trwa";
			case INQUEUE:
				return GuildsManager._ItemsColorNamePrimal + "Wyszukiwanie aktywne";
			case NOINQUEUE:
				return GuildsManager._ItemsColorNamePrimal + "Rozpocznij wyszukiwanie";
			default:
				return GuildsManager._ItemsColorNamePrimal + "Cos jest nie tak";
		}
	}
	
	private void startMatchSearch()
	{
		GuildWarPreapare gwp =guildPlayer.getGuild().guildWars;
		System.out.println(guildPlayer);
		System.out.println(guildPlayer.getGuild());
		switch (gwp.guildQueueStatus) 
		{
			case INMATCH:
				player.sendMessage(GuildsManager._GlobalPrefix + "Mecz trwa");
				break;
			case INQUEUE:
				player.sendMessage(GuildsManager._GlobalPrefix + "Wyszukiwanie przerwane");
				guildPlayer.getGuild().guildWars.stopQueue();
				break;
			case NOINQUEUE:
				player.sendMessage(GuildsManager._GlobalPrefix + "Wyszukiwanie rozpoczete");
				guildPlayer.getGuild().guildWars.startQueue();
				break;
		}
		
		this.setItem(17, ItemsCreator.getItemStack(Material.GRINDSTONE , getQueueStatusName()), e -> startMatchSearch());
	}
	
	public void updateMMR()
	{
		this.setItem(4, ItemsCreator.getItemStack(Material.GOLD_BLOCK , GuildsManager._ItemsColorNamePrimal+ "MMR: " + GuildsManager._ItemsColorNameSecond+ guildPlayer.getGuild().getMmr()));
	}
	
	public void updateQueue()
	{
		GuildPlayer[] queuePlayers = guildPlayer.getGuild().guildWars.getPlayersQueue();
		
		
		
		IntStream.range(0, warsFields.length).forEach( i -> 
		{
			if(i < queuePlayers.length)
			{
				GuildPlayer gp = queuePlayers[i];
				if(gp.getPlayer() == null)   gp.init();
				setItem(warsFields[i], ItemsCreator.getItemStack(
						gp.getRang().equals(GuildRanks.Member) ? Material.IRON_SWORD : gp.getRang().equals(GuildRanks.Capitan) ? Material.GOLDEN_SWORD : Material.DIAMOND_SWORD,
								GuildsManager._ItemsColorNamePrimal + gp.getPlayer().getName()));
			}
			else
				setItem(warsFields[i], ItemsCreator.getItemStack(Material.GRAY_STAINED_GLASS_PANE,GuildsManager._ItemsColorNamePrimal + "Wolne miejsce"));
		});
		
		if(guildPlayer.getRang().equals(GuildRanks.Owner) || guildPlayer.getRang().equals(GuildRanks.Capitan) )
		{
			this.setItem(17, ItemsCreator.getItemStack(Material.GRINDSTONE , getQueueStatusName()), e -> startMatchSearch());
		}
	}
	
	public void updateJoinLeaveButton()
	{
		GuildWarPreapare gwp =guildPlayer.getGuild().guildWars;
		
		this.setItem(16,
				ItemsCreator.getItemStack(gwp.isInQueue(guildPlayer) ? Material.RED_BANNER : gwp.isLocked() ? Material.YELLOW_BANNER:  Material.GREEN_BANNER ,
						gwp.isInQueue(guildPlayer) ? GuildsManager._ItemsColorNamePrimal + "Opusc grupe":  GuildsManager._ItemsColorNamePrimal + "Dolacz do grupy"),
							e -> joinLeaveWar((Player) e.getWhoClicked()));
	}
	
	
	private void joinLeaveWar(Player p)
	{
		switch(guildPlayer.getGuild().guildWars.joinLeaveQueue(guildPlayer))
		{
			case CANTJOIN:
				p.sendMessage(GuildsManager._GlobalPrefix + "Nie mozesz dolaczyc do grupy");
				break;
			case JOINED:
				p.sendMessage(GuildsManager._GlobalPrefix + "Dolaczyles do grupy");
				break;
			case LEAVE:
				p.sendMessage(GuildsManager._GlobalPrefix + "Opusciles grupe");
				break;
		}
		updateJoinLeaveButton();
	}
}
