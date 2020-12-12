/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 */
package com.windskull.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.windskull.Items.ItemsCreator;

public class InventoryTransactionGui_Input
	extends InventoryTransactionGui
{
	public String output;

	public InventoryTransactionGui_Input(InventoryGui previousGui, InventoryTransaction transactionAccept, Player player, String name)
	{
		super(previousGui, transactionAccept, player, name);
		this.createInventory(name);
	}

	private void createInventory(String name)
	{
		this.inventory = Bukkit.createInventory(this, InventoryType.ANVIL, name);
		this.setItem(0, ItemsCreator.getItemStack(Material.GREEN_STAINED_GLASS_PANE, "Akceptuj"), e ->
		{
			this.grabInput();
			if (this.transactionAccept.transactionAccepted(this))
			{
				this.player.openInventory(this.previousGui.getInventory());
			}
		});
		this.setItem(1, ItemsCreator.getItemStack(Material.RED_STAINED_GLASS_PANE, "Cofnij"), e -> this.player.openInventory(this.previousGui.getInventory()));
		System.out.print("Inventory: " + this.inventory);
	}

	private void grabInput()
	{
		System.out.print(this.output);
	}

	@Override
	public boolean blockPlayerInventoryClick()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onInventoryGuiClick(Player whoClicked, int slot, ItemStack clickedItem)
	{
		return true;
	}

	@Override
	public boolean onInventoryOpen(Player whoOpen)
	{
		return false;
	}

	@Override
	public boolean onInventoryClose(Player whoClosed)
	{
		return false;
	}
}
