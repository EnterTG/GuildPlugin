package com.windskull.GuildItems;

import org.bukkit.inventory.Inventory;

public interface GuildItem
{
	boolean checkInv(Inventory inv);
	String getName();
	int getAmount();
	void removeItems(Inventory inv);
}
