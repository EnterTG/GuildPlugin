package com.windskull.Managers;

import java.util.ArrayList;
import java.util.List;

import com.windskull.GuildPlugin.DiplomacyType;
import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildDiplomacy;
import com.windskull.GuildPlugin.GuildPluginMain;
import com.windskull.GuildPlugin.StorageType;
import com.windskull.Inventory.Inventories.Inventory_GuildStorage;

public class GuildManager
{

	public final Guild guild;

	public Inventory_GuildStorage storage;// = new ArrayList<Inventory_GuildStorage>();
	public Inventory_GuildStorage safe;// = new ArrayList<Inventory_GuildStorage>();

	public List<Guild> allyProporsal = new ArrayList<Guild>(); 
	
	public GuildManager(Guild guild)
	{
		this.guild = guild;
		guild.getAllGuildStorage().forEach(gs ->
		{
			if (gs.getStorageType() == StorageType.SAFE)
			{
				if (safe == null)
				{
					safe = new Inventory_GuildStorage(gs);
				} else
				{
					safe.addNext(new Inventory_GuildStorage(gs));
				}
			} else
				if (storage == null)
			{
				storage = new Inventory_GuildStorage(gs);
			} else
			{
				storage.addNext(new Inventory_GuildStorage(gs));
			}
		});
	}

	public boolean containsAllyInvitation(Guild g)
	{
		return allyProporsal.contains(g);
	}
	
	
	public boolean addAlyInvitation(Guild g)
	{
		return allyProporsal.add(g);
	}
	
	
	public boolean tryCreateAlly(Guild g)
	{
		if(allyProporsal.contains(g) && guild.getAllDiplomacy().stream().filter(gd -> gd.getDiplomacyType() == DiplomacyType.PEACE).toArray().length <= 4)
		{
			GuildDiplomacy gd = new GuildDiplomacy();
			gd.setDeclared(g);
			gd.setReciver(guild);
			gd.setDiplomacyType(DiplomacyType.PEACE);
			
			GuildDiplomacy gd2 = new GuildDiplomacy();
			gd2.setDeclared(guild);
			gd2.setReciver(g);
			gd2.setDiplomacyType(DiplomacyType.PEACE);
			
			//GuildPluginMain.eserver.save(gd);
			//GuildPluginMain.eserver.save(gd2);
			g.getAllDiplomacy().add(gd);
			guild.getAllDiplomacy().add(gd2);
			GuildPluginMain.eserver.save(g);
			GuildPluginMain.eserver.save(guild);
			return true;
		}
		else return false;
	}
	
	
	
	public void save()
	{
		storage.saveInventory();
		safe.saveInventory();
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return "Storage: " + storage + System.lineSeparator() + "Safe: " + safe;
	}
}
