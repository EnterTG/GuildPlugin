/*
 * Decompiled with CFR 0.145.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package com.windskull.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;



public class ItemsCreator {
	public static ItemStack getClean(Material mat) {
		return ItemsCreator.getItemStack(mat, " ");
	}

	public static ItemStack getGlassPlane() {
		return ItemsCreator.getClean(Material.GLASS_PANE);
	}

	public static ItemStack getItemStack(Material mat, String name) {
		return ItemsCreator.getItemStack(1, mat, name);
	}

	public static ItemStack getItemStack(int amount, Material mat, String name) {
		ItemStack item = new ItemStack(mat, amount);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack getPlayerHead(Player p)
	{
		ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
		
		SkullMeta playerheadmeta = (SkullMeta) playerhead.getItemMeta();
		playerheadmeta.setOwningPlayer(p);
		playerheadmeta.setDisplayName(p.getName());
		playerhead.setItemMeta(playerheadmeta);
		return playerhead;
	}	
	public static ItemStack getPlayerHead(Player p,String shortDesc)
	{
		ItemStack playerhead = new ItemStack(Material.PLAYER_HEAD);
		
		SkullMeta playerheadmeta = (SkullMeta) playerhead.getItemMeta();
		playerheadmeta.setOwningPlayer(p);
		playerheadmeta.setDisplayName(p.getName());
		
		List<String> lore = new ArrayList<String>();
		lore.add(shortDesc);
		playerheadmeta.setLore(lore);
		playerhead.setItemMeta(playerheadmeta);
		return playerhead;
	}
	
	
	
	public static ItemStack getSoulsAmount(int amount)
	{
		ItemStack item = new ItemStack(Material.GHAST_TEAR);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName("Lzy ksiezyca: " + amount);
		im.setLore(Arrays.asList("3 lzy ksiezyca ocala cie","przed przemiana w ducha","w przypdaku smierci"));
		item.setItemMeta(im);
		return item;
	}
}

