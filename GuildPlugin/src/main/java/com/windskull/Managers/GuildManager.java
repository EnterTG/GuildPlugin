package com.windskull.Managers;

import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.StorageType;
import com.windskull.Inventory.Inventories.Inventory_GuildStorage;

public class GuildManager {

	public final Guild guild;
	
	public  Inventory_GuildStorage storage ;//= new ArrayList<Inventory_GuildStorage>();
	public Inventory_GuildStorage safe;// = new ArrayList<Inventory_GuildStorage>();
	
	public GuildManager(Guild guild) 
	{
		this.guild = guild;
		guild.getAllGuildStorage().forEach(gs -> 
		{
			if(gs.getStorageType() == StorageType.SAFE)
				if(safe == null)safe = new Inventory_GuildStorage(gs); 
				else safe.addNext(new Inventory_GuildStorage(gs));
			else 
				if(storage == null ) storage = new Inventory_GuildStorage(gs);
				else storage.addNext(new Inventory_GuildStorage(gs));
		});	
	}
	
	public void save()
	{
		storage.saveInventory();
		safe.saveInventory();
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Storage: " + storage + System.lineSeparator() + "Safe: " + safe;
	}
}
