package com.windskull.Inventory.Inventories;

import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.windskull.GuildPlugin.GuildPlayer;
import com.windskull.Inventory.InventoryGui;
import com.windskull.Inventory.PageButton;
import com.windskull.Items.ItemsCreator;
import com.windskull.Items.SkullCreator;
import com.windskull.Managers.GuildsManager;

public abstract class Inventory_GuildMenu extends InventoryGui
{

	protected static final int[] playerContainer = new int[] {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};

	protected String name;
	protected String tag;

	protected int page = 0;

	protected GuildPlayer guildPlayer;
	protected Player player;

	public Inventory_GuildMenu(Player p)
	{
		this.player = p;
		this.guildPlayer = GuildsManager.getGuildManager().getGuildPlayer(p);
		this.initInventory();
	}

	protected void initInventory()
	{
		if (inventory == null)
		{
			this.inventory = Bukkit.createInventory(this, 36, "Gildia");
		}
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

	@Override
	public boolean blockPlayerInventoryClick()
	{
		return true;
	}

	protected void clearInventory()
	{
		IntStream.range(0, this.inventory.getSize()).forEach(i -> this.inventory.setItem(i, new ItemStack(Material.AIR)));
		page = 0;
		buttons.clear();
	}

	protected void buttons_NextPrevPage(int collectionSize,PageButton p)
	{
		ItemStack arrowright;
		//int collectionSize = collectionSize;// GuildPluginMain.server.getOnlinePlayers().size();
		if (collectionSize > (this.page + 1) * 14)
		{
			arrowright = this.getSkullItem(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM0ZWYwNjM4NTM3MjIyYjIwZjQ4MDY5NGRhZGMwZjg1ZmJlMDc1OWQ1ODFhYTdmY2RmMmU0MzEzOTM3NzE1OCJ9fX0=",
				"Nastepna strona");
			this.setItem(1, arrowright, e ->
			{
				++this.page;
				p.execute();
			});
		} else
		{
			this.setItem(1, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE));
		}
		if (this.page > 0)
		{
			arrowright = this.getSkullItem(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdhYWNhZDE5M2UyMjI2OTcxZWQ5NTMwMmRiYTQzMzQzOGJlNDY0NGZiYWI1ZWJmODE4MDU0MDYxNjY3ZmJlMiJ9fX0=",
				"Poprzednia strona");
			this.setItem(0, arrowright, e ->
			{
				--this.page;
				p.execute();
			});
		} else
		{
			this.setItem(0, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE));
		}
	}

	protected ItemStack getSkullItem(String base64String, String name)
	{
		ItemStack item = SkullCreator.itemFromBase64(base64String);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		item.setItemMeta(im);
		/*
		 * SkullMeta sm = (SkullMeta)item.getItemMeta();
		 * sm.setOwningPlayer(Bukkit.getOfflinePlayer((UUID)UUID.fromString(uuidString)));
		 * 
		 * item.setItemMeta((ItemMeta)sm);
		 */
		return item;
	}

}
