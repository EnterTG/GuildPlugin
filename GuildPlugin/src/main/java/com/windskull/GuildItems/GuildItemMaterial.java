package com.windskull.GuildItems;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GuildItemMaterial implements GuildItem
{
	public GuildItemMaterial(String name, Material mat, int amount)
	{
		this.mat = mat;
		this.amount = amount;
		this.name = name;
	}

	String name;
	Material mat;
	int amount;

	@Override
	public boolean checkInv(Inventory inv)
	{
		return Arrays.stream(inv.getContents()).filter(is -> is != null && is.getType().equals(mat)).collect(Collectors.summingInt(ItemStack::getAmount)) >= amount;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public int getAmount()
	{
		return amount;
	}

	@Override
	public void removeItems(Inventory inv)
	{
		int toremove = amount;
		// System.out.print("Remove items");
		for (ItemStack item : inv.getContents())
		{
			if (toremove > 0)
			{

				if (item != null && item.getType() != null && item.getType().equals(mat))
				{
					if (toremove >= item.getAmount())
					{
						// System.out.println("Remove item: " + item);
						// item.setType(Material.AIR);
						inv.remove(item);

						toremove -= item.getAmount();
					} else
					{
						item.setAmount(item.getAmount() - toremove);
						toremove = 0;
					}
				}
			} else
			{
				break;
			}
		}

	}
}
