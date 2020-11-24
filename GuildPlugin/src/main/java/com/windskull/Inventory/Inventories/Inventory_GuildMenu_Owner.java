package com.windskull.Inventory.Inventories;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.bukkit.plugin.Plugin;

import com.avaje.ebean.EbeanServer;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.factory.MaskFactory;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.convolution.GaussianKernel;
import com.sk89q.worldedit.math.convolution.HeightMap;
import com.sk89q.worldedit.math.convolution.HeightMapFilter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.DataException;
import com.sk89q.worldedit.world.block.BlockTypes;
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
import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildPlayer;
import com.windskull.GuildPlugin.GuildPluginMain;
import com.windskull.Inventory.AnviGui.api.src.main.java.net.wesjd.anvilgui.AnvilGUI;
import com.windskull.Items.ItemBuilder;
import com.windskull.Items.ItemsCreator;
import com.windskull.Managers.GuildsManager;

public class Inventory_GuildMenu_Owner extends Inventory_GuildMenu
{
	
	private interface GuildItem
	{
		boolean checkInv(Inventory inv);
		String getName();
		int getAmount();
		void removeItems(Inventory inv);
	}

	private class GuildItemString implements GuildItem
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
			for(ItemStack item : inv.getContents())
			{
				if(toremove > 0)
				{
					if(item.getType().toString().toLowerCase().contains(mat))
						if(toremove >= item.getAmount())
						{
							item.setType(Material.AIR);
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
	
	private class GuildItemMaterial implements GuildItem
	{
		public GuildItemMaterial(String name,Material mat, int amount) {
			this.mat = mat;
			this.amount = amount;
			this.name= name;
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
		public String getName() {
			return name;
		}

		@Override
		public int getAmount() {
			return amount;
		}
		
		@Override
		public void removeItems(Inventory inv) {
			int toremove = amount;
			for(ItemStack item : inv.getContents())
			{
				if(toremove > 0)
				{
					if(item.getType().equals(mat))
						if(toremove >= item.getAmount())
						{
							item.setType(Material.AIR);
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
	
	private static HashMap<Integer,List<GuildItem>> guildItems;
	public Inventory_GuildMenu_Owner(Player p) {
		super(p);
		//To config load
		if(guildItems == null)
		{
			guildItems = new HashMap<Integer, List<GuildItem>>();
			List<GuildItem> lvl1 = new ArrayList<GuildItem>();
			lvl1.add(new GuildItemMaterial("Kamien",Material.COBBLESTONE, 360));
			lvl1.add(new GuildItemString("Drewno","WOOD", 200));
			guildItems.put(1, lvl1);
			
			
			List<GuildItem> lvl2 = new ArrayList<GuildItem>();
			lvl2.add(new GuildItemMaterial("Kamien",Material.COBBLESTONE, 600));
			lvl2.add(new GuildItemString("Drewno","WOOD", 300));
			guildItems.put(2, lvl2);
		}
		createInventory();
	}
	
	
	protected void createInventory() {
		clearInventory();
		createInventory_Owner();
	}
	
	private void createInventory_Owner()
	{
		int guildLevel = guildPlayer.getGuild().getGuildLevel();
		
		this.setItem(11, ItemsCreator.getItemStack(Material.PLAYER_HEAD,GuildsManager._ItemsColorNamePrimal +  "Dodaj gracza"), e -> this.createInventory_AddPlayer());
		this.setItem(13, ItemsCreator.getItemStack(Material.SKELETON_SKULL,GuildsManager._ItemsColorNamePrimal +  "Usun gracza"), e -> this.createInventory_DeletePlayer());
		this.setItem(15, ItemsCreator.getItemStack(Material.BARRIER,GuildsManager._ItemsColorNamePrimal +  "Rozwiaz gildie"), e -> this.deleteGuildButton());
		if(guildLevel == 0 )
		{
			ItemBuilder ib = new ItemBuilder(ItemsCreator.getItemStack(Material.GRASS_BLOCK , GuildsManager._ItemsColorNamePrimal +  "Zajmij ziemie")).lore(GuildsManager._ItemsColorNamePrimal + "Wymagane zasoby:");
			guildItems.get(1).forEach(gi -> ib.lore(GuildsManager._ItemsColorNamePrimal +gi.getName() + ": " + gi.getAmount()));
			this.setItem(22,ib.make(), e -> this.createGuilldBase());
		}
		else if(guildLevel < GuildsManager._MaxGuildLvl && guildItems.containsKey(guildLevel+1)) 
		{
			ItemBuilder ib = new ItemBuilder(ItemsCreator.getItemStack(Material.STONE_BRICKS , GuildsManager._ItemsColorNamePrimal +  "Ulepsz gildie")).lore(GuildsManager._ItemsColorNamePrimal + "Wymagane zasoby:");
			guildItems.get(guildLevel+1).forEach(gi -> ib.lore(GuildsManager._ItemsColorNamePrimal +gi.getName() + ": " +GuildsManager._ItemsColorNameSecond+ gi.getAmount()));
			this.setItem(22, ib.make(), e -> this.upgradeGuildBuilding());
		}
	}
	
	
	//Deleting player form guild
	
	private void createInventory_DeletePlayer()
	{
		clearInventory();
		IntStream.range(0, inventory.getSize()).forEach(i -> this.setItem(i, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE)));
		//this.setItem(8, ItemsCreator.getItemStack(Material.PLAYER_HEAD, "Wyszukaj gracza"), e -> openPlayerSearch());
		draw_DeletePlayerContainer();
		this.setItem(35, 
				this.getSkullItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdhYWNhZDE5M2UyMjI2OTcxZWQ5NTMwMmRiYTQzMzQzOGJlNDY0NGZiYWI1ZWJmODE4MDU0MDYxNjY3ZmJlMiJ9fX0=",
						"Poprzednia strona")
				, e -> createInventory());
	}
	
	private void draw_DeletePlayerContainer()
	{
		Collection<GuildPlayer> playersOnline =guildPlayer.getGuild().getAllGuildPlayer();
		List<GuildPlayer> newList = playersOnline.stream().collect(Collectors.toCollection(ArrayList::new)).subList( page *14 , playersOnline.size() > ((page +1)* 14) ? ((page +1)* 14) : playersOnline.size());
		newList.remove(guildPlayer);
		IntStream.range(0, newList.size()).forEach( 
				i -> this.setItem(playerContainer[i], ItemsCreator.getPlayerHead(Bukkit.getPlayer(newList.get(i).getPlayeruuid()),"LPM + Shift zeby usunac"),
				e ->{ if(e.isShiftClick() && e.isLeftClick()) deletePlayer(newList.get(i));}));
		buttons_NextPrevPage(() -> draw_DeletePlayerContainer());
	}
	
	private void deletePlayer(GuildPlayer p)
	{
		Player playerDel = Bukkit.getPlayer(p.getPlayeruuid());
		if(playerDel != null) playerDel.sendMessage(GuildsManager._GlobalPrefix + "Zostales usuniety z gildi");
		GuildsManager.getGuildManager().deleteGuildPlayer(p);
		p.getGuild().removePlayerFromGuild(p);
		GuildPluginMain.eserver.delete(GuildPlayer.class,p.getId());
		
	} 
	
	//Add player to guild
	
	private void createInventory_AddPlayer()
	{
		clearInventory();
		IntStream.range(0, inventory.getSize()).forEach(i -> this.setItem(i, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE)));
		this.setItem(8, ItemsCreator.getItemStack(Material.PLAYER_HEAD, GuildsManager._ItemsColorNamePrimal +"Wyszukaj gracza"), e -> openAddPlayerSearch());
		draw_AddPlayersContainer();
		this.setItem(35, 
				this.getSkullItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdhYWNhZDE5M2UyMjI2OTcxZWQ5NTMwMmRiYTQzMzQzOGJlNDY0NGZiYWI1ZWJmODE4MDU0MDYxNjY3ZmJlMiJ9fX0=",
						"Poprzednia strona")
				, e -> createInventory());
	}
	
	private void draw_AddPlayersContainer()
	{
		Collection<? extends Player> playersOnline = GuildPluginMain.server.getOnlinePlayers();
		List<? extends Player> newList = playersOnline.stream().collect(Collectors.toCollection(ArrayList::new)).subList( page *14 , playersOnline.size() > ((page +1)* 14) ? ((page +1)* 14) : playersOnline.size());
		IntStream.range(0, newList.size()).forEach( i -> this.setItem(playerContainer[i], ItemsCreator.getPlayerHead(newList.get(i),"LPM zeby zaprosic"),e ->sendInvitation(playerContainer[i],newList.get(i))));
		buttons_NextPrevPage(() -> draw_AddPlayersContainer());
	}
	
	public void openAddPlayerSearch()
	{
		new AnvilGUI.Builder().onComplete((player, text) -> 
		{
			Player p = Bukkit.getServer().getPlayer(text);
			if(p == null)
				return AnvilGUI.Response.text((String)"Gracz nieznaleziony");
			else 
			{
				GuildPlayer gp = GuildsManager.getGuildManager().getGuildPlayer(p);
				GuildsManager kwp = GuildsManager.getGuildManager();
				if(gp == null) 
				{
					if(kwp.getInvitationSize(p)  >= 21)
						player.sendMessage(GuildsManager._GlobalPrefix +"Gracz nie ma miejsca na zaproszenia");
					else
					{
						player.sendMessage(GuildsManager._GlobalPrefix +"Zaproszenie zostało wysłane");
						kwp.addGuildInvitation(p, guildPlayer.getGuild());
					}
					AnvilGUI.Response.close();
				}
				else
					return AnvilGUI.Response.text((String)"Gracz posiada gildie");
				
			}
			return AnvilGUI.Response.close();
		}).text("Podaj nazwe gracza").title("Podaj nazwe gracza").plugin((Plugin)GuildPluginMain.main).open(this.player);
	}

	public void sendInvitation(int i,Player p)
	{
		GuildPlayer gp = GuildsManager.getGuildManager().getGuildPlayer(p);
		GuildsManager kwp = GuildsManager.getGuildManager();
		if(gp == null) 
		{
			if(kwp.getInvitationSize(p) >= 14)
				player.sendMessage(GuildsManager._GlobalPrefix +"Gracz nie ma miejsca na zaproszenia");
			else
			{
				player.sendMessage(GuildsManager._GlobalPrefix +"Zaproszenie zostało wysłane");
				kwp.addGuildInvitation(p, guildPlayer.getGuild());
				p.sendMessage(GuildsManager._GlobalPrefix +"Otrzymales zaproszenie do gildi: " + guildPlayer.getGuild().getName());
				
			}
		}
		else
			player.sendMessage(GuildsManager._GlobalPrefix +(String)"Gracz posiada gildie");
		buttons.remove(i);
	}
		
	
	//Delete guild
	
	private void deleteGuildButton()
	{
		final String code = String.valueOf((int)(Math.random()*9000)+1000);
		new AnvilGUI.Builder().onComplete((player, text) -> {
			if (text.equals(code))
			{
				if(deleteGuild())
					player.sendMessage(GuildsManager._GlobalPrefix +"Gildia zostala usunieta");
				else
					player.sendMessage(GuildsManager._GlobalPrefix +"Cos poszlo nie tak");
				return AnvilGUI.Response.close();
			}
			else 
			{
				player.sendMessage(GuildsManager._GlobalPrefix + "Kod niepoprawny");
				deleteGuildButton();
				return AnvilGUI.Response.close();
			}
		}).text("Przepisz kod zeby usunac gildie").title("Kod: " + code).plugin((Plugin)GuildPluginMain.main).open(this.player);
	}

	
	
	private boolean deleteGuildBuilding(com.sk89q.worldedit.world.World world,  ProtectedCuboidRegion region)
	{
		CuboidRegion r = new CuboidRegion(world, region.getMaximumPoint(), region.getMinimumPoint());
		LocalConfiguration config = WorldEdit.getInstance().getConfiguration();
		try (EditSession editSession = new EditSessionBuilder(world).build())// WorldEdit.getInstance().getSessionManager().e.newEditSession(world)) 
		{
			Snapshot snapshot = config.snapshotRepo.getSnapshot("Default.zip");
			ChunkStore chunkStore = snapshot.getChunkStore();
			SnapshotRestore restore = new SnapshotRestore(chunkStore, editSession, r);
			restore.restore();
			
		} 
		catch (InvalidSnapshotException | IOException | DataException | MaxChangedBlocksException e) 
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
		RegionManager regions = container.get(world);
		ProtectedCuboidRegion region = (ProtectedCuboidRegion) regions.getRegion(g.getGuildRegionId());
		
		
		if(!deleteGuildBuilding(world,region)) return false;

		regions.removeRegion(g.getGuildRegionId());
		try 
		{
			regions.save();
		} catch (StorageException e) {e.printStackTrace();}
		
		g.getAllOnlineGuildPlayers().forEach(p ->
			{
				p.getPlayer().sendMessage(GuildsManager._GlobalPrefix + "Twoja gildia zostala rozwiazana");
				gm.deleteGuildPlayer(p);
		});
		
		g.getAllGuildPlayer().forEach(p -> server.delete(GuildPlayer.class,p.getId()));
		server.delete(Guild.class, g.getId());
		
		gm.deleteGuild(g);
		
		return true;
		
	}
	//Upgrade guild building
	
	private void upgradeGuildBuilding()
	{
		int guildLevel = guildPlayer.getGuild().getGuildLevel();
		Inventory inv = player.getInventory();
		if(guildItems.get(guildLevel+1).stream().anyMatch(gi-> !gi.checkInv(inv))) {player.sendMessage(GuildsManager._GlobalPrefix + "Brak przedmiotow na ulepszenie gildi"); return;}
		Guild g = guildPlayer.getGuild();
		com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(g.getGuildLocation().getWorld());
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(world);
		ProtectedCuboidRegion region = (ProtectedCuboidRegion) regions.getRegion(g.getGuildRegionId());
		
		if(deleteGuildBuilding(world,region)) 
			if(!tryPasteGuildBuilding(g.getGuildLocation(),"GuildCore_"+guildLevel+".schem")) 
			{
				player.sendMessage(GuildsManager._GlobalPrefix + "Cos poszlo nie tak");
			}
			else
			{
				
				 g.setGuildLevel(guildLevel+1);
			}
		
	}
	
	//Create guild land
	private static final int regionRadius = 32;
	private void createGuilldBase() 
	{
		int guildLevel = guildPlayer.getGuild().getGuildLevel();
		Inventory inv = player.getInventory();
		if(guildItems.get(guildLevel+1).stream().anyMatch(gi-> !gi.checkInv(inv))) player.sendMessage(GuildsManager._GlobalPrefix + "Brak przedmiotow na budynek gildi"); 
		
		ProtectedCuboidRegion pcr = getRegionForGuild();
		if(!verifyLandForGuild(pcr,player.getWorld()))  player.sendMessage(GuildsManager._GlobalPrefix + "Ta ziemia jest juz zajeta"); 
		else if(!tryPasteGuildBuilding(player.getLocation(),"GuildCore_1.schem")) player.sendMessage(GuildsManager._GlobalPrefix + "Nie da sie umiesci budynku gildi"); 
		else 
		{
			guildPlayer.getGuild().setGuildLevel(1);
			guildPlayer.getGuild().setGuildLocation(player.getLocation());
			createGuildLand(pcr, player.getWorld());
		}
	}
	
	private ProtectedCuboidRegion getRegionForGuild()
	{
		//Location guildLocation = player.getLocation();	
		Location loc1 = player.getLocation().add(regionRadius, 0, regionRadius), loc2 = player.getLocation().subtract(regionRadius, 0, regionRadius);
		BlockVector3 bv1 = BlockVector3.at(loc1.getX(), 0, loc1.getZ()),bv2 = BlockVector3.at(loc2.getX(), 255, loc2.getZ());
		return new ProtectedCuboidRegion(guildPlayer.getGuild().getName(),bv1,bv2);
	}

	private boolean verifyLandForGuild(ProtectedCuboidRegion pcr,World world)
	{
		
		
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(world));
		ApplicableRegionSet set = regions.getApplicableRegions(pcr);
		
		if(set.size() > 0) return false;
		
		
		return true;
	}
	
	private void createGuildLand(ProtectedCuboidRegion pcr,World world)
	{
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(world));
		DefaultDomain members = pcr.getMembers();
		for(GuildPlayer gp : guildPlayer.getGuild().getAllGuildPlayer())
			members.addPlayer(gp.getPlayeruuid());
		
		pcr.setFlag(Flags.PVP, StateFlag.State.DENY);
		guildPlayer.getGuild().setGuildRegionId(pcr.getId());
		regions.addRegion(pcr);
		
	}
	
	private static final int smoothRadius = 3,smoothHeight = 3,iterations = 3;

	

	//Schematic 
	private Clipboard loadClipboard(String fileName) throws FileNotFoundException, IOException
	{
		Clipboard clipboard;	
		File file = new File(GuildPluginMain.main.getDataFolder().getAbsolutePath() + "/GuildSchematic/" + fileName);
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		ClipboardReader reader = format.getReader(new FileInputStream(file));
		clipboard = reader.read();
		return clipboard;
	}
	
	private void pasteSchematic(Clipboard clipboard, Location loc,String fileName) throws FileNotFoundException, IOException
	{
		
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(loc.getWorld()), -1);
		Operation operation = new ClipboardHolder(clipboard)
	            .createPaste(editSession)
	            .to(BlockVector3.at(loc.getX(),loc.getY(),loc.getZ()))
	            //.to(BlockVector3.at(p1.getX(),p1.getY(),p1.getZ()))
	            .ignoreAirBlocks(true).build();
	    Operations.complete(operation);
	}
	
	@SuppressWarnings("deprecation")
	private boolean tryPasteGuildBuilding(Location loc,String fileName)
	{
		
		Clipboard clipboard;
		try {
			clipboard = loadClipboard(fileName);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		//Location loc = player.getLocation();
		World world = loc.getWorld();
		com.sk89q.worldedit.world.World w = BukkitAdapter.adapt(world);
		
		
		//Origin must be in region
		
		//Check corners height 
		
		BlockVector3 origin = clipboard.getOrigin();
		int width = clipboard.getWidth(), lenght = clipboard.getLength();
		
		BlockVector3 p1 = BlockVector3.at(loc.getX() - origin.getX(), loc.getY() - origin.getY(), loc.getZ() - origin.getZ());
		BlockVector3 p2 = p1.add(width, 0, 0);
		BlockVector3 p3 = p1.add(0, 0, lenght);
		BlockVector3 p4 = p1.add(width,0,lenght);

		List<BlockVector3> points = new ArrayList<BlockVector3>();
		
		points.add(p1);
		points.add(p2);
		points.add(p3);
		points.add(p4);
		
		//points.forEach(p -> System.out.println("Point: " + p));
		
		for(BlockVector3 a : points)
		{
			for(BlockVector3 b : points)
			{
				if(a != b)
				{
					int h1 = w.getHighestTerrainBlock(a.getBlockX(), a.getBlockZ(), a.getBlockY()-10, a.getBlockY()+10);
					int h2 = w.getHighestTerrainBlock(b.getBlockX(), b.getBlockZ(), b.getBlockY()-10, b.getBlockY()+10);
					
					//System.out.println("ABS: " + Math.abs(h1-h2));
					if(Math.abs((h1 - h2)) >5)
					{
						//System.out.println("Bledne abs: " +Math.abs(h1 - h2) + " h1: " + h1 + " h2: " + h2	 );
						return false;
					}
				}
			}
		}

		try 
		{
			pasteSchematic(clipboard,loc, fileName);
			
			Bukkit.getScheduler().runTaskAsynchronously(GuildPluginMain.main, new Runnable() 
			{
				public void run() 
				{ 
					
					EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(loc.getWorld()), -1);
					Region r = new CuboidRegion(p1.subtract(0,20,0), p4.add(0, clipboard.getRegion().getHeight(), 0));
					editSession.replaceBlocks(r, BlockTypes.OBSIDIAN.toMask(editSession), BlockTypes.AIR);
					
					
					ParserContext parserContext = new ParserContext();
				    parserContext.setWorld(w);
			        parserContext.setRestricted(false);
				    
				    //Create regions on edge of schematic and perform smoothing on terrain
						
					CuboidRegion region1 = new CuboidRegion(p1.subtract(smoothRadius,-smoothHeight,smoothRadius), p2.add(smoothRadius, smoothHeight, smoothRadius));
					CuboidRegion region2 = new CuboidRegion(p2.add(-smoothRadius, smoothHeight, -smoothRadius), p4.add(smoothRadius,-smoothHeight,smoothRadius));
					CuboidRegion region3= new CuboidRegion(p4.add(smoothRadius,smoothHeight,smoothRadius), p3.subtract(smoothRadius,smoothHeight,smoothRadius));
					CuboidRegion region4 = new CuboidRegion(p3.add(smoothRadius,smoothHeight,smoothRadius), p1.add(-smoothRadius,-smoothHeight,smoothRadius));
					List<CuboidRegion> regions = new ArrayList<CuboidRegion>();
					regions.add(region1);
					regions.add(region2);
					regions.add(region3);
					regions.add(region4);
				 
					
				  
					LocalSession session = new LocalSession();
					
					session.setWorldOverride(w);
					
					MaskFactory mf = WorldEdit.getInstance().getMaskFactory();
					Mask mask = mf.parseFromInput("grass,stone,cobblestone,dirt", parserContext);
						
					
					
					regions.forEach(region -> {
						HeightMap heightMap = new HeightMap(editSession, region, mask);
				        HeightMapFilter filterr = new HeightMapFilter(new GaussianKernel(width > lenght ? width : lenght, 1.0));
				        heightMap.applyFilter(filterr, iterations);		        
					});
					
				}
			});
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
			
		} 			
		return true;
	}
	
	
	
	//Misc
    public List<Block> getSurrounding(Location origin) {

        List<Block> blocks = new ArrayList<>();
        for (int x = -1; x < 2; x++)
            for (int y = 0; y < 2; y++)
                for (int z = -1; z < 2; z++) {

                    Location loc = origin.clone().add(x, y, z);
                    Block block = loc.getBlock();
                    if (!origin.equals(loc) && (block.getType().name().contains("WOOD") || block.getType().name().contains("LEAVES")  || block.getType().name().contains("FENCE"))) {

                        blocks.add(block);

                    }

                }

        return blocks;

    }

    public List<Block> getLogs(Location origin) {

        List<Block> logs = new ArrayList<>();
        List<Block> next = getSurrounding(origin);

        while (!next.isEmpty()) {

            List<Block> nextNext = new ArrayList<>();
            for (Block log : next) {

                if (!logs.contains(log)) {
                    logs.add(log);
                    nextNext.addAll(getSurrounding(log.getLocation()));
                }

            }

            next = nextNext;

        }

        return logs;

    }

    
	public int getHight(World w,int x,int z,int miny,int maxy)
	{
		/*int minys = miny;
		if(w.getBlockAt(x, minys, z).getType().isAir()) minys = minys+1;
		if(w.getBlockAt(x, minys, z).getType().isAir()) minys = minys+1;*/
		System.out.print("Looking at: x: " +x +" z: " + z  );
		for(int y = maxy; y > miny;y--)
		{
			
			Material mat = w.getBlockAt(x, y, z).getType();
			if(mat.equals(Material.DIRT) || mat.equals(Material.COBBLESTONE) || mat.equals(Material.GRASS_BLOCK) || mat.equals(Material.STONE))
			{
				System.out.println(" Found on: " + y);
				return y;
			}
			
		}
		return maxy;
	}
	
	public void removeItemFromInventory(Inventory inv, int amount, Material mat)
	{
		int toremove = amount;
		for(ItemStack item : inv.getContents())
		{
			if(toremove > 0)
			{
				if(item.getType().equals(mat))
					if(toremove >= item.getAmount())
					{
						item.setType(Material.AIR);
						toremove -= item.getAmount();
					}
			}
			else
				break;	
		}
	}
	public void removeItemFromInventory(Inventory inv, int amount, String mat)
	{
		int toremove = amount;
		for(ItemStack item : inv.getContents())
		{
			if(toremove > 0)
			{
				if(item.getType().toString().toLowerCase().contains(mat))
					if(toremove >= item.getAmount())
					{
						item.setType(Material.AIR);
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
