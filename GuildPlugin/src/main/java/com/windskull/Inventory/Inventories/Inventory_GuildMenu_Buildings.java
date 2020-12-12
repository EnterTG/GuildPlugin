package com.windskull.Inventory.Inventories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.windskull.GuildItems.GuildItem;
import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.Guild.TMP_GuildBuilding;
import com.windskull.GuildPlugin.GuildBuilding;
import com.windskull.Items.ItemBuilder;
import com.windskull.Items.ItemsCreator;
import com.windskull.Managers.GuildsBuildingsManager;
import com.windskull.Managers.GuildsManager;
import com.windskull.Misc.SchematicOperations;

public class Inventory_GuildMenu_Buildings extends Inventory_GuildMenu
{

	public Inventory_GuildMenu_Buildings(Player p)
	{
		super(p);
		createInventory();
	}

	private void createInventory()
	{

		// Entry<String, GuildBuilding>[] buildings = (Entry<String, GuildBuilding>[])
		// GuildBuildingsManager.getInstance().guildsBuildings.entrySet().toArray();
		List<Map.Entry<String, GuildBuilding>> buildings = new ArrayList<>(GuildsBuildingsManager.getInstance().guildsBuildings.entrySet());
		IntStream.range(0, buildings.size()).forEach(i ->
		{
			GuildBuilding gb = buildings.get(i).getValue();

			this.setItem(playerContainer[i], getGuilBuildingItem(gb), s -> upgradeBuilding(playerContainer[i], gb));
		});

	}

	private ItemStack getGuilBuildingItem(GuildBuilding gb)
	{
		int blvl = getBuildingLevel(gb.buildingName);
		ItemBuilder ib = new ItemBuilder(
			ItemsCreator.getItemStack(gb.materialIcon, GuildsManager._ItemsColorNamePrimal + gb.buildingName + ": " + GuildsManager._ItemsColorNameSecond + blvl));
		if (blvl < gb.maxBuildingLevel)
		{
			gb.itemsforBuilding.get(blvl)
				.forEach(gi -> ib.lore(GuildsManager._ItemsColorNamePrimal + gi.getName() + ": " + GuildsManager._ItemsColorNameSecond + gi.getAmount()));
		} else
		{
			ib.lore(GuildsManager._ItemsColorNamePrimal + "Maksymalny poziom budynku.");
		}
		return ib.make();
	}

	private int getBuildingLevel(String name)
	{
		Guild g = guildPlayer.getGuild();
		return g.guildBuildingsMaped.stream().filter(s -> s.buildingName.equals(name)).findFirst().orElse(g.new TMP_GuildBuilding(name, 0)).buildingLevel;
	}

	private void upgradeBuilding(int slot, GuildBuilding gb)
	{
		// System.out.println("Start upgrade building");
		Guild g = guildPlayer.getGuild();
		Optional<TMP_GuildBuilding> tmpGuildBuilding = g.guildBuildingsMaped.stream().filter(s -> s.buildingName.equals(gb.buildingName)).findFirst();
		if (tmpGuildBuilding.isPresent())
		{
			TMP_GuildBuilding tmpb = tmpGuildBuilding.get();
			Inventory inv = player.getInventory();
			// System.out.println("Building level: " + tmpGuildBuilding.get().buildingLevel +" Max level: " + gb.maxBuildingLevel);
			if (tmpb.buildingLevel < gb.maxBuildingLevel)
			{
				ArrayList<GuildItem> buildings = gb.itemsforBuilding.get(tmpb.buildingLevel);
				if (buildings.stream().anyMatch(gi -> !gi.checkInv(inv)))
				{
					player.sendMessage(GuildsManager._GlobalPrefix + "Brak wymaganych zasobow");
					return;
				}

				buildings.stream().forEach(gi -> gi.removeItems(inv));
				setItem(slot, getGuilBuildingItem(gb), s -> upgradeBuilding(slot, gb));
				player.sendMessage(GuildsManager._GlobalPrefix + "Ulepszono budynek: " + GuildsManager._ItemsColorNameSecond + gb.buildingName);
				// System.out.println("Upgrade building");
				tmpGuildBuilding.get().buildingLevel += 1;
				Clipboard clipboard = SchematicOperations.getClipboard(gb.schematicName);
				if (clipboard == null)
				{
					player.sendMessage(GuildsManager._GlobalPrefix + "Nie udalo sie wkleic budynku");
				} else
				{
					try
					{
						SchematicOperations.pasteSchematic(clipboard, g.getGuildLocation());
					} catch (IOException e)
					{
						player.sendMessage(GuildsManager._GlobalPrefix + "Nie udalo sie wkleic budynku");
						e.printStackTrace();
					}
				}
			}
		} else
		{
			// System.out.println("Add new building");
			player.sendMessage(GuildsManager._GlobalPrefix + "Wybudowano budynek: " + GuildsManager._ItemsColorNameSecond + gb.buildingName);
			g.guildBuildingsMaped.add(g.new TMP_GuildBuilding(gb.buildingName, 1));
			Clipboard clipboard = SchematicOperations.getClipboard(gb.schematicName);
			if (clipboard == null)
			{
				player.sendMessage(GuildsManager._GlobalPrefix + "Nie udalo sie wkleic budynku");
			} else
			{
				try
				{
					SchematicOperations.pasteSchematic(clipboard, g.getGuildLocation());
				} catch (IOException e)
				{
					player.sendMessage(GuildsManager._GlobalPrefix + "Nie udalo sie wkleic budynku");
					e.printStackTrace();
				}
			}
		}
		this.setItem(slot, getGuilBuildingItem(gb), s -> upgradeBuilding(slot, gb));
	}

}
