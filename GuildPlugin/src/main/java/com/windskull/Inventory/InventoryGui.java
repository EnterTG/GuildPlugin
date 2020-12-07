package com.windskull.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryGui
implements InventoryHolder {
    protected Map<Integer, InventoryButton> buttons = new HashMap<Integer, InventoryButton>();
    protected Inventory inventory;

    public abstract boolean onInventoryGuiClick(Player var1, int var2, ItemStack var3);

    public abstract boolean onInventoryOpen(Player var1);

    public abstract boolean onInventoryClose(Player var1);

    public abstract boolean blockPlayerInventoryClick();
    
    
    public Inventory getInventory() {
        return this.inventory;
    }

    protected void setItem(int slot, ItemStack item, InventoryButton action) {
        this.setItem(slot, item);
        if (action != null) {
            this.buttons.put(slot, action);
        }
    }

    protected void setItem(int slot, ItemStack item) {
        this.inventory.setItem(slot, item);
    }

    public InventoryButton getButton(int slot) {
        return Optional.ofNullable(this.buttons.get(slot)).orElse(e -> {});
    }
    
    
    
    
}

