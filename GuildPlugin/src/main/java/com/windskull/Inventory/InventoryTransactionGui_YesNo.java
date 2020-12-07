/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 */
package com.windskull.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.windskull.Items.ItemsCreator;


public class InventoryTransactionGui_YesNo
extends InventoryTransactionGui {
	private boolean reopen;
    public InventoryTransactionGui_YesNo(InventoryGui previousGui, InventoryTransaction transactionAccept, Player player, String name,boolean b) {
        super(previousGui, transactionAccept, player, name);
        this.createInventory(name);
        reopen = b;
    }

    private void createInventory(String name) {
        this.inventory = Bukkit.createInventory((InventoryHolder)this, (int)9, (String)name);
        this.setItem(2, ItemsCreator.getItemStack(Material.GREEN_STAINED_GLASS_PANE, "Akceptuj"), e -> {
            this.transactionAccept.transactionAccepted(this);
            if(reopen)this.player.openInventory(this.previousGui.getInventory());
            else player.closeInventory();
        });
        this.setItem(5, ItemsCreator.getItemStack(Material.RED_STAINED_GLASS_PANE, "Cofnij"), e -> this.player.openInventory(this.previousGui.getInventory()));
    }
	@Override
	public boolean blockPlayerInventoryClick() {
		// TODO Auto-generated method stub
		return true;
	}
    @Override
    public boolean onInventoryGuiClick(Player whoClicked, int slot, ItemStack clickedItem) {
        return true;
    }

    @Override
    public boolean onInventoryOpen(Player whoOpen) {
        return false;
    }

    @Override
    public boolean onInventoryClose(Player whoClosed) {
        return false;
    }
}

