package com.windskull.Managers;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;

import com.google.common.base.Preconditions;
import com.windskull.GuildItems.GuildItem;
import com.windskull.GuildItems.GuildItemMaterial;
import com.windskull.GuildItems.GuildItemString;
import com.windskull.GuildPlugin.GuildBuilding;

public class GuildsBuildingsManager 
{
	private static GuildsBuildingsManager sing;
	
	public HashMap<String,GuildBuilding> guildsBuildings = new HashMap<String, GuildBuilding>();
	
	private GuildsBuildingsManager()
	{
		GuildBuilding farm = new GuildBuilding();
		farm.buildingName = "farm";
		farm.maxBuildingLevel = 2;
		farm.schematicName = "building_farm";
		farm.materialIcon = Material.IRON_HOE;
		ArrayList<ArrayList<GuildItem>> itemsforBuilding = new ArrayList<ArrayList<GuildItem>>();
		
		ArrayList<GuildItem> lv1 = new ArrayList<GuildItem>();
		GuildItem gi = new GuildItemMaterial("Kamien", Material.COBBLESTONE	, 120);
		lv1.add(gi);
		gi = new GuildItemString("Drewno", "WOOD", 120);
		lv1.add(gi);
		itemsforBuilding.add(lv1);
		
		lv1 = new ArrayList<GuildItem>();
		gi = new GuildItemMaterial("Kamien", Material.COBBLESTONE	, 150);
		lv1.add(gi);
		gi = new GuildItemString("Drewno", "WOOD", 150);
		lv1.add(gi);
		itemsforBuilding.add(lv1);
		
		farm.itemsforBuilding = itemsforBuilding;
		
		
		
		guildsBuildings.put("farm", farm);
		
	}
	
	public static GuildsBuildingsManager getInstance()
	{
		if(sing == null) sing = new GuildsBuildingsManager();
		return sing;
	}
	
	
	public GuildBuilding getGuildBuilding(String s)
	{
		Preconditions.checkNotNull(guildsBuildings);
		GuildBuilding gb = guildsBuildings.get(s);
		Preconditions.checkNotNull(gb);
		return gb;
	}
}
