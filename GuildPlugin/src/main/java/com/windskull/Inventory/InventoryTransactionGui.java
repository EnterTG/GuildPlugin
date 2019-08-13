/*
 * Decompiled with CFR 0.145.
 */
package com.windskull.Inventory;

import org.bukkit.entity.Player;



public abstract class InventoryTransactionGui
extends InventoryGui {
    protected InventoryGui previousGui;
    protected InventoryTransaction transactionAccept;
    protected Player player;

    public InventoryTransactionGui(InventoryGui previousGui, InventoryTransaction transactionAccept, Player player, String name) {
        this.previousGui = previousGui;
        this.transactionAccept = transactionAccept;
        this.player = player;
    }
}

