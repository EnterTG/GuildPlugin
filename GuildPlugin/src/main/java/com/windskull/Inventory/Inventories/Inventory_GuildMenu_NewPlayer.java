package com.windskull.Inventory.Inventories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildPlayer;
import com.windskull.GuildPlugin.GuildPluginMain;
import com.windskull.GuildPlugin.GuildRanks;
import com.windskull.GuildPlugin.GuildStorage;
import com.windskull.GuildPlugin.StorageType;
import com.windskull.Inventory.AnviGui.api.src.main.java.net.wesjd.anvilgui.AnvilGUI;
import com.windskull.Items.ItemsCreator;
import com.windskull.Managers.GuildsManager;
import com.windskull.Managers.PlayerInvitations;

public class Inventory_GuildMenu_NewPlayer extends Inventory_GuildMenu {

	public Inventory_GuildMenu_NewPlayer(Player p) {
		super(p);
		createInventory();
	}


	protected void createInventory() {
		clearInventory();
		createInventory_NewGuild();
		
	}

	private void createInventory_NewGuild() {
		
		this.setItem(13, ItemsCreator.getItemStack(Material.ENCHANTED_BOOK, GuildsManager._ItemsColorNamePrimal + "Stworz nowa gildie"), e -> this.openNameInput());
		this.setItem(22, ItemsCreator.getItemStack(Material.CHEST,GuildsManager._ItemsColorNamePrimal +  "Zaproszenia"), e -> this.createInventory_Invitation());
	}
	
	
	private void openNameInput() {
		new AnvilGUI.Builder().onClose(player -> {if(this.name == null)player.sendMessage(GuildsManager._GlobalPrefix +"Tworzenie gildi przerwane");}).onComplete((player, text) -> {
			if (text.length() > 3 && text.length() <= 20)
				
				if (!GuildsManager.getGuildManager().isGuildExist((String)text)) {
					this.name = text;
					this.openTagInput();
					return AnvilGUI.Response.text((String)"Zakceptowano");
				}
				else
					return AnvilGUI.Response.text((String)"Nazwa zajeta");
			else
				return AnvilGUI.Response.text((String)"Nazwa musi zawierac od 4 do 20 znakow.");
		}).text("Podaj nazwe gildi").title("Podaj nazwe gildi").plugin((Plugin)GuildPluginMain.main).open(this.player);
	}

	private void openTagInput() {
		new AnvilGUI.Builder().onClose(player -> {if(this.tag == null)player.sendMessage(GuildsManager._GlobalPrefix +"Tworzenie gildi przerwane");}).onComplete((player, text) -> {
			if (text.length() >= 2 && text.length() <= 4 )
				if (!GuildsManager.getGuildManager().isTAGExist((String)text)) {
					this.tag = text;
					
					createGuild();
					player.sendMessage(GuildsManager._GlobalPrefix +"Brawo stworzyles gildie");
					return AnvilGUI.Response.close();
				}
				else
					return AnvilGUI.Response.text((String)"Tag zajety");
			else
				return AnvilGUI.Response.text((String)"Tag musi zawierac od 2 do 4 znakow.");
		}).text("Podaj TAG").title("Podaj TAG").plugin((Plugin)GuildPluginMain.main).open(this.player);
	}
	
	private void createGuild()
	{
		Guild g = new Guild();
		g.setName(name);
		g.setOpis("Opis nie ustawiony");
		g.setTag(tag);
		g.setMmr(1000);
		GuildPlayer gp = new GuildPlayer(player, GuildRanks.Owner, g);
		gp.init();
		
		GuildStorage gs = new GuildStorage();
		gs.setGuild(g);
		gs.setStorageType(StorageType.NORMAL);
		gs.setSize(36);
		gs.setStorageContent("");
		g.addGuildStorage(gs);
		
		
		gs = new GuildStorage();
		gs.setGuild(g);
		gs.setStorageType(StorageType.NORMAL);
		gs.setSize(36);
		gs.setStorageContent("");
		g.addGuildStorage(gs);
		
		gs = new GuildStorage();
		gs.setGuild(g);
		gs.setStorageType(StorageType.SAFE);
		gs.setSize(27);
		gs.setStorageContent("");
		g.addGuildStorage(gs);

		g.addNewGuildPlayer(gp);
		GuildsManager.getGuildManager().addNewGuild(g);
		//GuildsManager.getGuildManager().addGuildPlayer(gp);
		GuildPluginMain.eserver.save(g);
	}
	
	
	private void createInventory_Invitation()
	{
		clearInventory();
		IntStream.range(0, inventory.getSize()).forEach(i -> this.setItem(i, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE)));
		GuildsManager kwp = GuildsManager.getGuildManager();
		PlayerInvitations pi = kwp.getPlayerInvitation(player);
		if(pi != null)
		{

			//this.setItem(8, ItemsCreator.getItemStack(Material.PLAYER_HEAD, GuildsManager._ItemsColorName +"Wyszukaj gracza"), e -> openAddPlayerSearch());
			
			Collection<Guild> playersOnline =  pi.guildsInvitation;
			List<? extends Guild> newList = playersOnline.stream()
					.collect(Collectors.toCollection(ArrayList::new)).subList( page *14 , playersOnline.size() > ((page +1)* 14) ? ((page +1)* 14) : playersOnline.size());
			IntStream.range(0, newList.size())
			.forEach( i -> this.setItem(playerContainer[i], ItemsCreator.getItemStack(Material.NETHER_STAR,GuildsManager._ItemsColorNamePrimal +"LPM zeby dolaczyc"),e ->setGuild(newList.get(i))));
			buttons_NextPrevPage(() -> createInventory_Invitation());
		
		}
		this.setItem(35, 
				this.getSkullItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdhYWNhZDE5M2UyMjI2OTcxZWQ5NTMwMmRiYTQzMzQzOGJlNDY0NGZiYWI1ZWJmODE4MDU0MDYxNjY3ZmJlMiJ9fX0=",
						"Poprzednia strona")
				, e -> createInventory());
	}
	
	
	private void setGuild(Guild g)
	{
		GuildPlayer gp = new GuildPlayer(player, GuildRanks.Member, g);
		gp.init();
		sendMessageToAllGuildPlayers(g,GuildsManager._GlobalPrefix +"Gracz: " + player.getName() + " dolaczyl do gildi.");
		g.addNewGuildPlayer(gp);
		GuildsManager kwp = GuildsManager.getGuildManager();
		WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(g.getGuildLocation().getWorld())).getRegion(g.getGuildRegionId()).getMembers().addPlayer(player.getUniqueId());
		
		kwp.playersInvitations.remove(player);
		createInventory();
	}
	
	private void sendMessageToAllGuildPlayers(Guild g,String message)
    {
    	g.getAllOnlineGuildPlayers().forEach(p -> p.getPlayer().sendMessage(message));
    }
}
