package com.windskull.GuildItems;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuildItemString implements GuildItem
{
	public GuildItemString(String name,String mat, int amount) {
		super();
		this.mat = mat.toLowerCase();
		this.amount = amount;
		this.name = name;
	}

	String name;
	String mat;
	int amount;
	
	
	@Override
	public boolean checkInv(Inventory inv) 
	{
		return Arrays.stream(inv.getContents()).filter(is ->is != null && is.getType().toString().toLowerCase().contains(mat)).collect(Collectors.summingInt(ItemStack::getAmount)) >= amount;
	}
	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public String getName() {
		return name;
	}
	@Override
	public void removeItems(Inventory inv) {
		int toremove = amount;
		//System.out.print("Remove items");
		for(ItemStack item : inv.getContents())
		{
			if(toremove > 0)
			{
				if(item != null && item.getType() != null && item.getType().toString().toLowerCase().contains(mat))
					if(toremove >= item.getAmount())
					{
						//System.out.println("Remove item: " + item);
						inv.remove(item);//item.setType(Material.AIR);
						toremove -= item.getAmount();
						
					}
					else
					{
						item.setAmount(item.getAmount()-toremove);
						toremove = 0;
					}
			}
			else
				break;	
		}
		
	}
	
}
