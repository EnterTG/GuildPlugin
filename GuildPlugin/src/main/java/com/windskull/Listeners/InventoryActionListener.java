package com.windskull.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import com.windskull.Inventory.InventoryGui;

public class InventoryActionListener implements Listener{

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		try
		{
			if(e.getInventory() != null && e.getClickedInventory() != null)
				if(e.getInventory().getHolder() instanceof InventoryGui)
				{
					InventoryGui i = (InventoryGui) e.getInventory().getHolder();
					//System.out.println("Type: " + e.getClickedInventory().getType());
					if( e.getClickedInventory().getType().equals(InventoryType.PLAYER) && i.blockPlayerInventoryClick()) {e.setCancelled(true);return;}
				
					
					e.setCancelled(i.onInventoryGuiClick((Player) e.getWhoClicked(), e.getSlot(), e.getClickedInventory().getItem(e.getSlot())));
					i.getButton(e.getSlot()).onClick(e);
					
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e)
	{
		if(e.getInventory().getHolder() instanceof InventoryGui)
		{
			InventoryGui i = (InventoryGui) e.getInventory().getHolder();
			i.onInventoryOpen((Player) e.getPlayer());
		}
	}
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		if(e.getInventory().getHolder() instanceof InventoryGui)
		{
			InventoryGui i = (InventoryGui) e.getInventory().getHolder();
			i.onInventoryClose((Player) e.getPlayer());
		}
	}
}
