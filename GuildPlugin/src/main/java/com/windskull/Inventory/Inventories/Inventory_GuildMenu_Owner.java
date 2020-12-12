package com.windskull.Inventory.Inventories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.avaje.ebean.EbeanServer;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.snapshot.InvalidSnapshotException;
import com.sk89q.worldedit.world.snapshot.Snapshot;
import com.sk89q.worldedit.world.snapshot.SnapshotRestore;
import com.sk89q.worldedit.world.storage.ChunkStore;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.windskull.GuildItems.GuildItem;
import com.windskull.GuildItems.GuildItemMaterial;
import com.windskull.GuildItems.GuildItemString;
import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildPlayer;
import com.windskull.GuildPlugin.GuildPluginMain;
import com.windskull.Inventory.AnviGui.api.src.main.java.net.wesjd.anvilgui.AnvilGUI;
import com.windskull.Items.ItemBuilder;
import com.windskull.Items.ItemsCreator;
import com.windskull.Managers.GuildsManager;
import com.windskull.Misc.SchematicOperations;

public class Inventory_GuildMenu_Owner extends Inventory_GuildMenu
{

	private static HashMap<Integer, List<GuildItem>> guildItems;

	public Inventory_GuildMenu_Owner(Player p)
	{
		super(p);
		// To config load
		if (guildItems == null)
		{
			guildItems = new HashMap<>();
			List<GuildItem> lvl1 = new ArrayList<>();
			lvl1.add(new GuildItemMaterial("Kamien", Material.COBBLESTONE, 360));
			lvl1.add(new GuildItemString("Drewno", "WOOD", 200));
			guildItems.put(1, lvl1);

			List<GuildItem> lvl2 = new ArrayList<>();
			lvl2.add(new GuildItemMaterial("Kamien", Material.COBBLESTONE, 600));
			lvl2.add(new GuildItemString("Drewno", "WOOD", 300));
			guildItems.put(2, lvl2);
		}
		createInventory();
	}

	protected void createInventory()
	{
		clearInventory();
		createInventory_Owner();
	}

	private void createInventory_Owner()
	{
		int guildLevel = guildPlayer.getGuild().getGuildLevel();
		// System.out.println("GuildManager: " + GuildsManager.getGuildManager().getGuildManager(guildPlayer.getGuild()));
		this.setItem(11, ItemsCreator.getItemStack(Material.PLAYER_HEAD, GuildsManager._ItemsColorNamePrimal + "Dodaj gracza"), e -> this.createInventory_AddPlayer());
		this.setItem(13, ItemsCreator.getItemStack(Material.SKELETON_SKULL, GuildsManager._ItemsColorNamePrimal + "Usun gracza"), e -> this.createInventory_DeletePlayer());
		this.setItem(15, ItemsCreator.getItemStack(Material.BARRIER, GuildsManager._ItemsColorNamePrimal + "Rozwiaz gildie"), e -> this.deleteGuildButton());
		
		this.setItem(0, ItemsCreator.getItemStack(Material.CHEST, GuildsManager._ItemsColorNamePrimal + "Storage"),
			e -> player.openInventory(GuildsManager.getGuildManager().getGuildManager(guildPlayer.getGuild()).storage.getInventory()));

		this.setItem(1, ItemsCreator.getItemStack(Material.BROWN_BED, GuildsManager._ItemsColorNamePrimal + "Otworz menu budynkow"), e -> this.openBuildingsMenu());
		this.setItem(2, ItemsCreator.getItemStack(Material.DIAMOND_SWORD, GuildsManager._ItemsColorNamePrimal + "Otworz menu budynkow"),
			e -> player.openInventory(new Inventory_GuildMenu_WarPanel(player).getInventory()));
		this.setItem(3, ItemsCreator.getItemStack(Material.BROWN_BED, GuildsManager._ItemsColorNamePrimal + "Otworz menu dyplomacji"), e -> this.openDiplomacy());
		if (guildLevel == 0)
		{
			ItemBuilder ib = new ItemBuilder(ItemsCreator.getItemStack(Material.GRASS_BLOCK, GuildsManager._ItemsColorNamePrimal + "Zajmij ziemie"))
				.lore(GuildsManager._ItemsColorNamePrimal + "Wymagane zasoby:");
			guildItems.get(1).forEach(gi -> ib.lore(GuildsManager._ItemsColorNamePrimal + gi.getName() + ": " + gi.getAmount()));
			this.setItem(22, ib.make(), e -> this.createGuilldBase());
		} else if (guildLevel < GuildsManager._MaxGuildLvl && guildItems.containsKey(guildLevel + 1))
		{
			ItemBuilder ib = new ItemBuilder(ItemsCreator.getItemStack(Material.STONE_BRICKS, GuildsManager._ItemsColorNamePrimal + "Ulepsz gildie"))
				.lore(GuildsManager._ItemsColorNamePrimal + "Wymagane zasoby:");
			guildItems.get(guildLevel + 1)
				.forEach(gi -> ib.lore(GuildsManager._ItemsColorNamePrimal + gi.getName() + ": " + GuildsManager._ItemsColorNameSecond + gi.getAmount()));
			this.setItem(22, ib.make(), e -> this.upgradeGuildBuilding());
		}
	}

	
	private void openDiplomacy()
	{
		player.openInventory(new Inventory_GuildMenu_Diplomacy(player).getInventory());
	}
	
	// Open buildings menu
	private void openBuildingsMenu()
	{
		player.openInventory(new Inventory_GuildMenu_Buildings(player).getInventory());
	}

	// Deleting player form guild

	private void createInventory_DeletePlayer()
	{
		clearInventory();
		IntStream.range(0, inventory.getSize()).forEach(i -> this.setItem(i, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE)));
		// this.setItem(8, ItemsCreator.getItemStack(Material.PLAYER_HEAD, "Wyszukaj gracza"), e -> openPlayerSearch());
		draw_DeletePlayerContainer();
		this.setItem(35,
			this.getSkullItem(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdhYWNhZDE5M2UyMjI2OTcxZWQ5NTMwMmRiYTQzMzQzOGJlNDY0NGZiYWI1ZWJmODE4MDU0MDYxNjY3ZmJlMiJ9fX0=",
				"Poprzednia strona"),
			e -> createInventory());
	}

	private void draw_DeletePlayerContainer()
	{
		Collection<GuildPlayer> playersOnline = guildPlayer.getGuild().getAllGuildPlayer();
		List<GuildPlayer> newList = playersOnline.stream().collect(Collectors.toCollection(ArrayList::new)).subList(page * 14,
			playersOnline.size() > ((page + 1) * 14) ? ((page + 1) * 14) : playersOnline.size());
		newList.remove(guildPlayer);
		IntStream.range(0, newList.size()).forEach(
			i -> this.setItem(playerContainer[i], ItemsCreator.getPlayerHead(Bukkit.getPlayer(newList.get(i).getPlayeruuid()), "LPM + Shift zeby usunac"),
				e ->
				{
					if (e.isShiftClick() && e.isLeftClick())
					{
						deletePlayer(newList.get(i));
					}
				}));
		buttons_NextPrevPage(newList.size(),() -> draw_DeletePlayerContainer());
	}

	private void deletePlayer(GuildPlayer p)
	{
		Player playerDel = Bukkit.getPlayer(p.getPlayeruuid());
		if (playerDel != null)
		{
			playerDel.sendMessage(GuildsManager._GlobalPrefix + "Zostales usuniety z gildi");
		}
		GuildsManager.getGuildManager().deleteGuildPlayer(p);
		Guild g = guildPlayer.getGuild();
		WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(g.getGuildLocation().getWorld())).getRegion(g.getGuildRegionId()).getMembers()
			.removePlayer(p.getPlayeruuid());// ;(player.getUniqueId());
		p.getGuild().removePlayerFromGuild(p);
		GuildPluginMain.eserver.delete(GuildPlayer.class, p.getId());

	}

	// Add player to guild

	private void createInventory_AddPlayer()
	{
		clearInventory();
		IntStream.range(0, inventory.getSize()).forEach(i -> this.setItem(i, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE)));
		this.setItem(8, ItemsCreator.getItemStack(Material.PLAYER_HEAD, GuildsManager._ItemsColorNamePrimal + "Wyszukaj gracza"), e -> openAddPlayerSearch());
		draw_AddPlayersContainer();
		this.setItem(35,
			this.getSkullItem(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdhYWNhZDE5M2UyMjI2OTcxZWQ5NTMwMmRiYTQzMzQzOGJlNDY0NGZiYWI1ZWJmODE4MDU0MDYxNjY3ZmJlMiJ9fX0=",
				"Poprzednia strona"),
			e -> createInventory());
	}

	private void draw_AddPlayersContainer()
	{
		Collection<? extends Player> playersOnline = GuildPluginMain.server.getOnlinePlayers();
		List<? extends Player> newList = playersOnline.stream().collect(Collectors.toCollection(ArrayList::new)).subList(page * 14,
			playersOnline.size() > ((page + 1) * 14) ? ((page + 1) * 14) : playersOnline.size());
		IntStream.range(0, newList.size()).forEach(
			i -> this.setItem(playerContainer[i], ItemsCreator.getPlayerHead(newList.get(i), "LPM zeby zaprosic"), e -> sendInvitation(playerContainer[i], newList.get(i))));
		buttons_NextPrevPage(newList.size(),() -> draw_AddPlayersContainer());
	}

	public void openAddPlayerSearch()
	{
		new AnvilGUI.Builder().onComplete((player, text) ->
		{
			Player p = Bukkit.getServer().getPlayer(text);
			if (p == null)
			{
				return AnvilGUI.Response.text("Gracz nieznaleziony");
			} else
			{
				GuildPlayer gp = GuildsManager.getGuildManager().getGuildPlayer(p);
				GuildsManager kwp = GuildsManager.getGuildManager();
				if (gp == null)
				{
					if (kwp.getInvitationSize(p) >= 21)
					{
						player.sendMessage(GuildsManager._GlobalPrefix + "Gracz nie ma miejsca na zaproszenia");
					} else
					{
						player.sendMessage(GuildsManager._GlobalPrefix + "Zaproszenie zostało wysłane");
						kwp.addGuildInvitation(p, guildPlayer.getGuild());
					}
					AnvilGUI.Response.close();
				} else
				{
					return AnvilGUI.Response.text("Gracz posiada gildie");
				}

			}
			return AnvilGUI.Response.close();
		}).text("Podaj nazwe gracza").title("Podaj nazwe gracza").plugin(GuildPluginMain.main).open(this.player);
	}

	public void sendInvitation(int i, Player p)
	{
		GuildPlayer gp = GuildsManager.getGuildManager().getGuildPlayer(p);
		GuildsManager kwp = GuildsManager.getGuildManager();
		if (gp == null)
		{
			if (kwp.getInvitationSize(p) >= 14)
			{
				player.sendMessage(GuildsManager._GlobalPrefix + "Gracz nie ma miejsca na zaproszenia");
			} else
			{
				player.sendMessage(GuildsManager._GlobalPrefix + "Zaproszenie zostało wysłane");
				kwp.addGuildInvitation(p, guildPlayer.getGuild());
				p.sendMessage(GuildsManager._GlobalPrefix + "Otrzymales zaproszenie do gildi: " + guildPlayer.getGuild().getName());

			}
		} else
		{
			player.sendMessage(GuildsManager._GlobalPrefix + "Gracz posiada gildie");
		}
		buttons.remove(i);
	}

	// Delete guild

	private void deleteGuildButton()
	{
		final String code = String.valueOf((int) (Math.random() * 9000) + 1000);
		new AnvilGUI.Builder().onComplete((player, text) ->
		{
			if (text.equals(code))
			{
				if (deleteGuild())
				{
					player.sendMessage(GuildsManager._GlobalPrefix + "Gildia zostala usunieta");
				} else
				{
					player.sendMessage(GuildsManager._GlobalPrefix + "Cos poszlo nie tak");
				}
				return AnvilGUI.Response.close();
			} else
			{
				player.sendMessage(GuildsManager._GlobalPrefix + "Kod niepoprawny");
				deleteGuildButton();
				return AnvilGUI.Response.close();
			}
		}).text("Przepisz kod zeby usunac gildie").title("Kod: " + code).plugin(GuildPluginMain.main).open(this.player);
	}

	private boolean deleteGuildBuilding(com.sk89q.worldedit.world.World world, ProtectedCuboidRegion region)
	{
		CuboidRegion r = new CuboidRegion(world, region.getMaximumPoint(), region.getMinimumPoint());
		LocalConfiguration config = WorldEdit.getInstance().getConfiguration();
		try (EditSession editSession = new EditSessionBuilder(world).build())// WorldEdit.getInstance().getSessionManager().e.newEditSession(world))
		{
			Snapshot snapshot = config.snapshotRepo.getSnapshot("Default.zip");
			ChunkStore chunkStore = snapshot.getChunkStore();
			SnapshotRestore restore = new SnapshotRestore(chunkStore, editSession, r);
			restore.restore();

		} catch (InvalidSnapshotException | IOException | DataException | MaxChangedBlocksException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean deleteGuild() throws NullPointerException
	{

		com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(player.getWorld());
		Guild g = guildPlayer.getGuild();
		EbeanServer server = GuildPluginMain.eserver;
		GuildsManager gm = GuildsManager.getGuildManager();
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		if(world != null)
		{
			RegionManager regions = container.get(world);
			if(regions != null)
			{
				if(g.getGuildRegionId() != null) 
				{
					ProtectedCuboidRegion region = (ProtectedCuboidRegion) regions.getRegion(g.getGuildRegionId());
					if(region != null)
					{
						if (!deleteGuildBuilding(world, region))
						{
							return false;
						}
				
						regions.removeRegion(g.getGuildRegionId());
						try
						{
							regions.save();
						} catch (StorageException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
		g.getAllOnlineGuildPlayers().forEach(p ->
		{
			p.getPlayer().sendMessage(GuildsManager._GlobalPrefix + "Twoja gildia zostala rozwiazana");
			gm.deleteGuildPlayer(p);
		});

		g.getAllGuildPlayer().forEach(p -> server.delete(GuildPlayer.class, p.getId()));
		server.delete(Guild.class, g.getId());

		gm.deleteGuild(g);

		return true;

	}
	// Upgrade guild building

	private void upgradeGuildBuilding()
	{
		int guildLevel = guildPlayer.getGuild().getGuildLevel();
		Inventory inv = player.getInventory();
		if (guildItems.get(guildLevel + 1).stream().anyMatch(gi -> !gi.checkInv(inv)))
		{
			player.sendMessage(GuildsManager._GlobalPrefix + "Brak przedmiotow na ulepszenie gildi");
			return;
		}
		Guild g = guildPlayer.getGuild();
		com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(g.getGuildLocation().getWorld());
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(world);
		ProtectedCuboidRegion region = (ProtectedCuboidRegion) regions.getRegion(g.getGuildRegionId());

		if (deleteGuildBuilding(world, region))
		{
			if (!SchematicOperations.tryPasteGuildBuilding(g.getGuildLocation(), "GuildCore_" + guildLevel + ".schem"))
			{
				player.sendMessage(GuildsManager._GlobalPrefix + "Cos poszlo nie tak");
			} else
			{

				g.setGuildLevel(guildLevel + 1);

			}
		}

	}

	// Create guild land
	private static final int regionRadius = 32;

	private void createGuilldBase()
	{
		int guildLevel = guildPlayer.getGuild().getGuildLevel();
		Inventory inv = player.getInventory();
		if (guildItems.get(guildLevel + 1).stream().anyMatch(gi -> !gi.checkInv(inv)))
		{
			player.sendMessage(GuildsManager._GlobalPrefix + "Brak przedmiotow na budynek gildi");
			if (!player.isOp())
			{
				return;
			}
		}

		ProtectedCuboidRegion pcr = getRegionForGuild();
		if (!verifyLandForGuild(pcr, player.getWorld()))
		{
			player.sendMessage(GuildsManager._GlobalPrefix + "Ta ziemia jest juz zajeta");
		} else if (!SchematicOperations.tryPasteGuildBuilding(player.getLocation(), "GuildCore_1.schem"))
		{
			player.sendMessage(GuildsManager._GlobalPrefix + "Nie da sie umiesci budynku gildi");
		} else
		{
			guildItems.get(guildLevel + 1).stream().forEach(gi -> gi.removeItems(inv));
			guildPlayer.getGuild().setGuildLevel(1);
			guildPlayer.getGuild().setGuildLocation(player.getLocation());
			createGuildLand(pcr, player.getWorld());
		}
	}

	private ProtectedCuboidRegion getRegionForGuild()
	{
		// Location guildLocation = player.getLocation();
		Location loc1 = player.getLocation().add(regionRadius, 0, regionRadius), loc2 = player.getLocation().subtract(regionRadius, 0, regionRadius);
		BlockVector3 bv1 = BlockVector3.at(loc1.getX(), 0, loc1.getZ()), bv2 = BlockVector3.at(loc2.getX(), 255, loc2.getZ());
		return new ProtectedCuboidRegion(guildPlayer.getGuild().getName(), bv1, bv2);
	}

	private boolean verifyLandForGuild(ProtectedCuboidRegion pcr, World world)
	{

		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(world));
		ApplicableRegionSet set = regions.getApplicableRegions(pcr);

		if (set.size() > 0)
		{
			return false;
		}

		return true;
	}

	private void createGuildLand(ProtectedCuboidRegion pcr, World world)
	{
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(world));
		DefaultDomain members = pcr.getMembers();
		for (GuildPlayer gp : guildPlayer.getGuild().getAllGuildPlayer())
		{
			members.addPlayer(gp.getPlayeruuid());
		}

		pcr.setFlag(Flags.PVP, StateFlag.State.DENY);
		guildPlayer.getGuild().setGuildRegionId(pcr.getId());
		regions.addRegion(pcr);

	}

	// Misc
	public List<Block> getSurrounding(Location origin)
	{

		List<Block> blocks = new ArrayList<>();
		for (int x = -1; x < 2; x++)
		{
			for (int y = 0; y < 2; y++)
			{
				for (int z = -1; z < 2; z++)
				{

					Location loc = origin.clone().add(x, y, z);
					Block block = loc.getBlock();
					if (!origin.equals(loc)
						&& (block.getType().name().contains("WOOD") || block.getType().name().contains("LEAVES") || block.getType().name().contains("FENCE")))
					{

						blocks.add(block);

					}

				}
			}
		}

		return blocks;

	}

	public List<Block> getLogs(Location origin)
	{

		List<Block> logs = new ArrayList<>();
		List<Block> next = getSurrounding(origin);

		while (!next.isEmpty())
		{

			List<Block> nextNext = new ArrayList<>();
			for (Block log : next)
			{

				if (!logs.contains(log))
				{
					logs.add(log);
					nextNext.addAll(getSurrounding(log.getLocation()));
				}

			}

			next = nextNext;

		}

		return logs;

	}

	public void removeItemFromInventory(Inventory inv, int amount, Material mat)
	{
		int toremove = amount;
		for (ItemStack item : inv.getContents())
		{
			if (toremove > 0)
			{
				if (item.getType().equals(mat))
				{
					if (toremove >= item.getAmount())
					{
						item.setType(Material.AIR);
						toremove -= item.getAmount();
					}
				}
			} else
			{
				break;
			}
		}
	}

	public void removeItemFromInventory(Inventory inv, int amount, String mat)
	{
		int toremove = amount;
		for (ItemStack item : inv.getContents())
		{
			if (toremove > 0)
			{
				if (item.getType().toString().toLowerCase().contains(mat))
				{
					if (toremove >= item.getAmount())
					{
						item.setType(Material.AIR);
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
