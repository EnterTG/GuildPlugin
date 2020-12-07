package com.windskull.Listeners;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildRanks;
import com.windskull.Inventory.Inventories.Inventory_GuildMenu_Buildings;
import com.windskull.Inventory.Inventories.Inventory_GuildMenu_WarPanel;
import com.windskull.Managers.GuildsManager;

public class PlayerInteractWithBlock implements Listener
{
	@FunctionalInterface
	public interface InventoryCreator
	{
		public void exec(Player p);
	}
	
	public PlayerInteractWithBlock()
	{
		interactableBlocks.put(Material.TRAPPED_CHEST,p ->{if(checkPlayerPosition(p)) p.openInventory(GuildsManager.getGuildManager().getGuildManager(GuildsManager.getGuildManager().getGuildPlayer(p).getGuild()).storage.getInventory());});
		interactableBlocks.put(Material.CARTOGRAPHY_TABLE, p -> {if(checkPlayerPosition(p)) p.openInventory(new Inventory_GuildMenu_WarPanel(p).getInventory());});
		interactableBlocks.put(Material.SMITHING_TABLE, p -> {if(checkPlayerPosition(p) && checkPlayerPerrmision(p,GuildRanks.Owner)) p.openInventory(new Inventory_GuildMenu_Buildings(p).getInventory());});
	}
	
	public HashMap<Material,InventoryCreator> interactableBlocks = new HashMap<Material, PlayerInteractWithBlock.InventoryCreator>();
	
	public boolean checkPlayerPerrmision(Player p, GuildRanks... guildRanks )
	{
		return Arrays.stream(guildRanks).anyMatch(gr -> gr == GuildsManager.getGuildManager().getGuildPlayer(p).getRang());
	}
	
	public boolean checkPlayerPosition(Player p)
	{
		Location loc = p.getLocation();
		Guild g =  GuildsManager.getGuildManager().getGuildPlayer(p).getGuild();
		if(g == null ) return false;
		
		RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
		
		if(g.getGuildRegionId() != null && manager.hasRegion(g.getGuildRegionId()) && manager.getRegion(g.getGuildRegionId()).contains(BukkitAdapter.asBlockVector(loc)))
			return true;
		else
			return false;
	}
	
	@EventHandler
	public void opPlayerInteractWithBlokc(PlayerInteractEvent e)
	{
		
		Player p = e.getPlayer();
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			InventoryCreator creator = interactableBlocks.get(e.getClickedBlock().getType());
			if(creator != null)
			{
				e.setCancelled(true);
				creator.exec(p);
			}
		}
	}

}
