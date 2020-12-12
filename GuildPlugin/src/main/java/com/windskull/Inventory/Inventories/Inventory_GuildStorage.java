package com.windskull.Inventory.Inventories;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.windskull.GuildPlugin.GuildStorage;
import com.windskull.GuildPlugin.StorageType;
import com.windskull.Inventory.InventoryGui;
import com.windskull.Items.ItemsCreator;
import com.windskull.Managers.GuildsManager;

public class Inventory_GuildStorage extends InventoryGui
{
	private GuildStorage guildStorage;
	private Inventory_GuildStorage next;
	private Inventory_GuildStorage previous;

	public Inventory_GuildStorage(GuildStorage guildStorage)
	{
		this.inventory = Bukkit.createInventory(this, guildStorage.getSize() < 9 ? 9 : guildStorage.getSize(), "Magazyn");

		try
		{
			if (guildStorage != null && guildStorage.getStorageContent() != null && !guildStorage.getStorageContent().equals(""))
			{
				this.inventory.setContents(fromBase64(guildStorage.getStorageContent()).getContents());
			}
		} catch (IllegalArgumentException | IOException e)
		{
			// TODO Auto-generated catch block
			this.inventory = Bukkit.createInventory(this, 9, "Blad podczas wczytywania");
			e.printStackTrace();
		}
		IntStream.range(0, 9).forEach(i -> inventory.setItem(i, ItemsCreator.getGlassPlane()));
		this.guildStorage = guildStorage;
	}

	@Override
	public boolean onInventoryGuiClick(Player var1, int var2, ItemStack var3)
	{
		return var2 < 9 ? true : false;
	}

	@Override
	public boolean blockPlayerInventoryClick()
	{
		return false;
	}

	@Override
	public boolean onInventoryOpen(Player var1)
	{
		return false;
	}

	@Override
	public boolean onInventoryClose(Player var1)
	{
		return false;
	}

	public StorageType getStorageType()
	{
		return guildStorage.getStorageType();
	}

	public void setStorageType(StorageType storageType)
	{
		guildStorage.setStorageType(storageType);
	}

	public void saveInventory()
	{
		guildStorage.setStorageContent(toBase64(getInventory()));
		if (next != null)
		{
			next.saveInventory();
		}
	}

	public void addNext(Inventory_GuildStorage next)
	{
		if (this.next == null)
		{
			this.next = next;
			this.setItem(7, ItemsCreator.getSkullItem(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM0ZWYwNjM4NTM3MjIyYjIwZjQ4MDY5NGRhZGMwZjg1ZmJlMDc1OWQ1ODFhYTdmY2RmMmU0MzEzOTM3NzE1OCJ9fX0=",
				GuildsManager._ItemsColorNamePrimal + "Nastepna strona"), e -> goNext((Player) e.getWhoClicked()));
			next.addPrevious(this);
		} else
		{
			next.addNext(next);

		}
	}

	public void addPrevious(Inventory_GuildStorage previous)
	{
		if (previous != null)
		{
			this.previous = previous;
			this.setItem(1, ItemsCreator
				.getSkullItem(
					"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdhYWNhZDE5M2UyMjI2OTcxZWQ5NTMwMmRiYTQzMzQzOGJlNDY0NGZiYWI1ZWJmODE4MDU0MDYxNjY3ZmJlMiJ9fX0=",
					GuildsManager._ItemsColorNamePrimal + "Poprzednia strona"),
				e -> goPrevious((Player) e.getWhoClicked()));

		}
	}

	private void goNext(Player player)
	{
		player.openInventory(next.getInventory());
	}

	private void goPrevious(Player player)
	{
		player.openInventory(previous.getInventory());
	}

	// https://gist.github.com/graywolf336/8153678
	/**
	* A method to serialize an inventory to Base64 string.
	* 
	* <p />
	* 
	* Special thanks to Comphenix in the Bukkit forums or also known
	* as aadnk on GitHub.
	* 
	* <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
	* 
	* @param inventory to serialize
	* @return Base64 string of the provided inventory
	* @throws IllegalStateException
	*/
	public static String toBase64(Inventory inventory) throws IllegalStateException
	{
		try
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

			// Write the size of the inventory
			dataOutput.writeInt(inventory.getSize());

			// Save every element in the list
			for (int i = 0; i < inventory.getSize(); i++)
			{
				dataOutput.writeObject(inventory.getItem(i));
			}

			// Serialize that array
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e)
		{
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	/**
	* 
	* A method to get an {@link Inventory} from an encoded, Base64, string.
	* 
	* <p />
	* 
	* Special thanks to Comphenix in the Bukkit forums or also known
	* as aadnk on GitHub.
	* 
	* <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
	* 
	* @param data Base64 string of data containing an inventory.
	* @return Inventory created from the Base64 string.
	* @throws IOException
	*/
	public static Inventory fromBase64(String data) throws IOException
	{
		try
		{
			// System.out.println("Inventory data: " + data );
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

			// Read the serialized inventory
			for (int i = 0; i < inventory.getSize(); i++)
			{
				inventory.setItem(i, (ItemStack) dataInput.readObject());
			}

			dataInput.close();
			return inventory;
		} catch (ClassNotFoundException e)
		{
			throw new IOException("Unable to decode class type.", e);
		}
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return toBase64(getInventory());
	}

}
