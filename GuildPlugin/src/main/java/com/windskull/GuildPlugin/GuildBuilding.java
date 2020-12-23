package com.windskull.GuildPlugin;

import java.util.ArrayList;

import org.bukkit.Material;

import com.windskull.GuildItems.GuildItem;

public class GuildBuilding
{

	public Material materialIcon;
	public String buildingName;
	public String schematicName;
	public int maxBuildingLevel;

	public ArrayList<ArrayList<GuildItem>> itemsforBuilding;

	public void paste(Guild g)
	{

	}
@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof GuildBuilding)
		{
			GuildBuilding gb = (GuildBuilding) obj;
			return buildingName.equals(gb.buildingName);
		}
		return false;
	}	
	
	@Override
	public int hashCode()
	{
		return buildingName.hashCode();
	}
}
