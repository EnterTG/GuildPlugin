package com.windskull.Inventory.Inventorys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.avaje.ebean.EbeanServer;
import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildPlayer;
import com.windskull.GuildPlugin.GuildPluginMain;
import com.windskull.GuildPlugin.GuildRanks;
import com.windskull.Inventory.InventoryGui;
import com.windskull.Inventory.InventoryTransactionGui_YesNo;
import com.windskull.Inventory.AnviGui.api.src.main.java.net.wesjd.anvilgui.AnvilGUI;
import com.windskull.Items.ItemsCreator;
import com.windskull.Items.SkullCreator;
import com.windskull.Managers.GuildsManager;

public class Inventory_GuildMenu extends InventoryGui
{
	
		private static final int[] playerContainer = new int[] {10,11,12,13,14,15,16,19,20,21,22,23,24,25};
		
		private String name;
		private String tag;
		
		private int page = 0;
		
		public GuildPlayer guildPlayer;
		private Player player;

		public Inventory_GuildMenu(Player p) {
			this.player = p;
			this.guildPlayer = GuildsManager.getGuildManager().getGuildPlayer(p);
			System.out.print(guildPlayer);
			this.createInventory();
		}

		private void createInventory() {
			if(inventory == null)this.inventory = Bukkit.createInventory((InventoryHolder)this, (int)36, (String)"Gildia");
			if (this.guildPlayer == null) 
				this.createInventory_NewGuild();
			else if(guildPlayer.getRang().equals(GuildRanks.Owner))
				createInventory_Owner();
			else
				createInventory_Member();
			
		}

		@Override
		public boolean onInventoryGuiClick(Player whoClicked, int slot, ItemStack clickedItem) {
			return true;
		}

		@Override
		public boolean onInventoryOpen(Player whoOpen) {
			return false;
		}

		@Override
		public boolean onInventoryClose(Player whoClosed) {
			return false;
		}

		
		private void clearInventory()
		{
			IntStream.range(0, this.inventory.getSize()).forEach(i -> this.inventory.setItem(i, new ItemStack(Material.AIR)));
			page = 0;
			buttons.clear();
		}

		
		private void createInventory_NewGuild() {
			this.setItem(13, ItemsCreator.getItemStack(Material.ENCHANTED_BOOK, "Stworz nowa gildie"), e -> this.openNameInput());
		}
		
		
		
		private void createInventory_Member()
		{
			clearInventory();
			this.setItem(15, ItemsCreator.getItemStack(Material.BARRIER, "Opusc gildie"), e -> this.acceptLeaveGuild());
		}
		
		private void acceptLeaveGuild()
		{
			InventoryTransactionGui_YesNo accept = new InventoryTransactionGui_YesNo(this,(e) -> {leaveGuild();return true;}, player, "Potwierdz opuszczenie gildi",false);
			player.openInventory(accept.getInventory());
		}
		
		private void leaveGuild()
		{
			Guild g = guildPlayer.getGuild();
			g.removePlayerFromGuild(guildPlayer);
			g.getAllOnlineGuildPlayers().forEach(gp ->{ gp.getPlayer().sendMessage("Gracz " + player.getName() + " opuscil gildie");});
			player.sendMessage("Opusciles gildie " + g.getName() );
		}
		
		
		private void createInventory_Owner()
		{
			clearInventory();
			this.setItem(11, ItemsCreator.getItemStack(Material.PLAYER_HEAD, "Dodaj gracza"), e -> this.createInventory_AddPlayer());
			this.setItem(13, ItemsCreator.getItemStack(Material.SKELETON_SKULL, "Usun gracza"), e -> this.createInventory_DeletePlayer());
			this.setItem(15, ItemsCreator.getItemStack(Material.BARRIER, "Rozwiaz gildie"), e -> this.deleteGuildButton());
		}

		private void deleteGuildButton()
		{
			final String code = String.valueOf((int)(Math.random()*9000)+1000);
			new AnvilGUI.Builder().onComplete((player, text) -> {
				if (text.equals(code))
				{
					deleteGuild();
					player.sendMessage("Gildia zostala usunieta");
					return AnvilGUI.Response.close();
				}
				else 
				{
					player.sendMessage("Kod niepoprawny");
					deleteGuildButton();
					return AnvilGUI.Response.close();
				}
			}).text("Przepisz kod zeby usunac gildie").title("Kod: " + code).plugin((Plugin)GuildPluginMain.main).open(this.player);
		}
		
		private void deleteGuild()
		{
			Guild g = guildPlayer.getGuild();
			EbeanServer server = GuildPluginMain.eserver;
			GuildsManager gm = GuildsManager.getGuildManager();
			g.getAllOnlineGuildPlayers().forEach(p ->{
				p.getPlayer().sendMessage("Twoja gildia zostala rozwiazana");
				gm.deleteGuildPlayer(p);});
			g.getAllGuildPlayer().forEach(p -> server.delete(GuildPlayer.class,p.getId()));
			
			gm.deleteGuild(g);
			server.delete(Guild.class, g.getId());
			
		}
		
		
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
			buttons_AddPlayer(() -> draw_AddPlayersContainer());
		}
		
		private void deletePlayer(GuildPlayer p)
		{
			Player player = Bukkit.getPlayer(p.getPlayeruuid());
			if(player != null) player.sendMessage("Zostales usuniety z gildi");
			GuildsManager.getGuildManager().deleteGuildPlayer(p);
			p.getGuild().removePlayerFromGuild(p);
			GuildPluginMain.eserver.delete(GuildPlayer.class,p.getId());
			
		}
		
		
		private void createInventory_AddPlayer()
		{
			clearInventory();
			IntStream.range(0, inventory.getSize()).forEach(i -> this.setItem(i, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE)));
			this.setItem(8, ItemsCreator.getItemStack(Material.PLAYER_HEAD, "Wyszukaj gracza"), e -> openAddPlayerSearch());
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
			buttons_AddPlayer(() -> draw_AddPlayersContainer());
		}
		
		public void sendInvitation(int i,Player p)
		{
			GuildPlayer gp = GuildsManager.getGuildManager().getGuildPlayer(p);
			GuildsManager kwp = GuildsManager.getGuildManager();
			if(gp == null) 
			{
				if(kwp.getInvitationSize(p) >= 21)
					player.sendMessage("Gracz nie ma miejsca na zaproszenia");
				else
				{
					player.sendMessage("Zaproszenie zostało wysłane");
					kwp.addGuildInvitation(p, gp.getGuild());
					
				}
			}
			else
				player.sendMessage((String)"Gracz posiada gildie");
			buttons.remove(i);
		}
		
		
		
		@FunctionalInterface
		private interface pageButton
		{
			public void execute();
		}

		private void buttons_AddPlayer(pageButton p)
		{
			ItemStack arrowright;
			int allPlayers = GuildPluginMain.server.getOnlinePlayers().size();
			if (allPlayers > (this.page + 1) * 14) {
				arrowright = this.getSkullItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM0ZWYwNjM4NTM3MjIyYjIwZjQ4MDY5NGRhZGMwZjg1ZmJlMDc1OWQ1ODFhYTdmY2RmMmU0MzEzOTM3NzE1OCJ9fX0=","Nastepna strona");
				this.setItem(1, arrowright, e -> {
					++this.page;
					p.execute();
				});
			}else
				this.setItem(1, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE));
			if (this.page > 0) {
				arrowright = this.getSkullItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdhYWNhZDE5M2UyMjI2OTcxZWQ5NTMwMmRiYTQzMzQzOGJlNDY0NGZiYWI1ZWJmODE4MDU0MDYxNjY3ZmJlMiJ9fX0=","Poprzednia strona");
				this.setItem(0, arrowright, e -> {
					--this.page;
					p.execute();
				});
			}
			else
				this.setItem(0, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE));
		}

		private ItemStack getSkullItem(String base64String,String name) {
			ItemStack item = SkullCreator.itemFromBase64(base64String);
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(name);
			item.setItemMeta(im);
			/*SkullMeta sm = (SkullMeta)item.getItemMeta();
			sm.setOwningPlayer(Bukkit.getOfflinePlayer((UUID)UUID.fromString(uuidString)));
			
			item.setItemMeta((ItemMeta)sm);*/
			return item;
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
							player.sendMessage("Gracz nie ma miejsca na zaproszenia");
						else
						{
							player.sendMessage("Zaproszenie zostało wysłane");
							kwp.addGuildInvitation(p, gp.getGuild());
						}
						AnvilGUI.Response.close();
					}
					else
						return AnvilGUI.Response.text((String)"Gracz posiada gildie");
					
				}
				return AnvilGUI.Response.close();
			}).text("Podaj nazwe gracza").title("Podaj nazwe gracza").plugin((Plugin)GuildPluginMain.main).open(this.player);
		}
		
		private void openNameInput() {
			new AnvilGUI.Builder().onClose(player -> {if(this.name == null)player.sendMessage("Tworznie gildi przerwane");}).onComplete((player, text) -> {
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
			new AnvilGUI.Builder().onClose(player -> {if(this.tag == null)player.sendMessage("Tworznie gildi przerwane");}).onComplete((player, text) -> {
				if (text.length() >= 2 && text.length() <= 4 )
					if (!GuildsManager.getGuildManager().isTAGExist((String)text)) {
						this.tag = text;
						
						createGuild();
						player.sendMessage("Brawo stworzyles gildie");
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
			GuildPlayer gp = new GuildPlayer(player, GuildRanks.Owner, g);
			gp.init();
			g.addNewGuildPlayer(gp);
			GuildsManager.getGuildManager().addNewGuild(g);
			GuildsManager.getGuildManager().addGuildPlayer(player, gp);
			GuildPluginMain.eserver.save(g);
		}
	}