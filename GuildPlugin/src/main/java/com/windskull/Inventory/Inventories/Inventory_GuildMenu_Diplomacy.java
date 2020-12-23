package com.windskull.Inventory.Inventories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.windskull.GuildPlugin.DiplomacyType;
import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildDiplomacy;
import com.windskull.GuildPlugin.GuildPluginMain;
import com.windskull.Items.ItemsCreator;
import com.windskull.Managers.GuildManager;
import com.windskull.Managers.GuildsManager;

public class Inventory_GuildMenu_Diplomacy extends Inventory_GuildMenu
{

	private static final int[] allaysFields = {10, 11, 12, 13, 14};
	private static final int[] warsFields = {19, 20, 21, 22, 23};
	public Inventory_GuildMenu_Diplomacy(Player p)
	{
		super(p);
		createInventory();
	}
	
	
	public void createInventory()
	{
		clearInventory();
		IntStream.range(0, inventory.getSize()).forEach(i -> this.setItem(i, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE)));
		Guild g = guildPlayer.getGuild();
		
		GuildDiplomacy[] allays = g.getAllDiplomacy().stream().filter(gd -> gd.getDiplomacyType() == DiplomacyType.PEACE).toArray(GuildDiplomacy[]::new);
		GuildDiplomacy[] wars = g.getAllDiplomacy().stream().filter(gd -> gd.getDiplomacyType() == DiplomacyType.WAR).toArray(GuildDiplomacy[]::new);
		IntStream.range(0, allaysFields.length).forEach(i ->
		{
			if (i < allays.length)
			{
				GuildDiplomacy gp = allays[i];
				setItem(allaysFields[i], ItemsCreator.getItemStack(Material.BLUE_BANNER,GuildsManager._ItemsColorNamePrimal + gp.getReciver().getName()
					+ " Shift + LPM Zeby usunac"),
					e ->{ if(e.isShiftClick() && e.isLeftClick()) deleteAlly(gp);} );
			} else
			{
				setItem(allaysFields[i], ItemsCreator.getItemStack(Material.BLUE_STAINED_GLASS_PANE, GuildsManager._ItemsColorNamePrimal + "Wolne miejsce"), p -> showGuilds_AddAlly());
			}
		});
		
		IntStream.range(0, warsFields.length).forEach(i ->
		{
			if (i < wars.length)
			{
				GuildDiplomacy gp = wars[i];
				setItem(warsFields[i], ItemsCreator.getItemStack(Material.RED_BANNER,GuildsManager._ItemsColorNamePrimal + gp.getReciver().getName()+ " Shift + LPM Zeby usunac"),
					e ->{ if(e.isShiftClick() && e.isLeftClick()) deleteWar(gp);});
			} else
			{
				setItem(warsFields[i], ItemsCreator.getItemStack(Material.RED_STAINED_GLASS_PANE, GuildsManager._ItemsColorNamePrimal + "Wolne miejsce"), p -> showGuilds_AddWar());
			}
		});
		
		this.setItem(16,
			ItemsCreator.getItemStack(Material.BLUE_WOOL, GuildsManager._ItemsColorNamePrimal + "Tworzenie sojuszy"),
				e -> showGuilds_AddAlly());
		
		this.setItem(25,
			ItemsCreator.getItemStack(Material.RED_WOOL, GuildsManager._ItemsColorNamePrimal + "Tworzenie wojny"),
				e -> showGuilds_AddWar());
	
	}
	
	
	public void deleteAlly( GuildDiplomacy g)
	{
		Guild guild = guildPlayer.getGuild();
		Optional<GuildDiplomacy> toRem = g.getReciver().getAllDiplomacy().stream().filter(gd ->/* gd.getDiplomacyType() == DiplomacyType.PEACE &&*/ gd.getReciver() == g.getDeclared()).findAny();

		//Optional<GuildDiplomacy> toRem2 = g.getAllDiplomacy().stream().filter(gd -> /* gd.getDiplomacyType() == DiplomacyType.PEACE && */gd.getReciver() == guild).findAny();

		if(toRem.isPresent())
		{
			/*
			 * GuildPluginMain.eserver.delete(GuildDiplomacy.class,g.getId());
			 * GuildPluginMain.eserver.delete(GuildDiplomacy.class,guild.getId());
			 */
			guild.getAllDiplomacy().remove(g);
			g.getReciver().getAllDiplomacy().remove(toRem.get());
			GuildPluginMain.eserver.delete(GuildDiplomacy.class,toRem.get().getId());
			GuildPluginMain.eserver.delete(GuildDiplomacy.class,g.getId());
			g.getReciver().getAllOnlineGuildPlayers().forEach(gp -> 
			{
				if(gp.getPlayer() == null) gp.init();
				gp.getPlayer().sendMessage(GuildsManager._GlobalPrefix + "Sojusz z gildia: " + GuildsManager._ItemsColorNameSecond + guild.getName() + GuildsManager._ItemsColorNamePrimal + " zostal rozwiazany");
			});
			
			guild.getAllOnlineGuildPlayers().forEach(gp -> 
			{
				if(gp.getPlayer() == null) gp.init();
				gp.getPlayer().sendMessage(GuildsManager._GlobalPrefix + "Sojusz z gildia: " + GuildsManager._ItemsColorNameSecond + g.getReciver().getName() + GuildsManager._ItemsColorNamePrimal + " zostal rozwiazany");
			});
			createInventory();
		}
	}
	
	public void deleteWar(GuildDiplomacy g)
	{
		Guild guild = guildPlayer.getGuild();
		//Optional<GuildDiplomacy> toRem = guild.getAllDiplomacy().stream().filter(gd ->/* gd.getDiplomacyType() == DiplomacyType.PEACE &&*/ gd.getReciver() == g).findAny();
		//GuildPluginMain.eserver.delete(GuildDiplomacy.class,g.getId());
		guild.getAllDiplomacy().remove(g);
		GuildPluginMain.eserver.delete(GuildDiplomacy.class,g.getId());
		//GuildPluginMain.eserver.save(g.getReciver());
	
		
		guild.getAllOnlineGuildPlayers().forEach(gp -> 
		{
			if(gp.getPlayer() == null) gp.init();
			gp.getPlayer().sendMessage(GuildsManager._GlobalPrefix + "Wojna z gildia: " + GuildsManager._ItemsColorNameSecond + g.getReciver().getName()+GuildsManager._ItemsColorNamePrimal + " zostala zawieszona");
		});
		
		g.getReciver().getAllOnlineGuildPlayers().forEach(gp -> 
		{
			if(gp.getPlayer() == null) gp.init();
			gp.getPlayer().sendMessage(GuildsManager._GlobalPrefix + "Gildia: " + GuildsManager._ItemsColorNameSecond + guild.getName()+GuildsManager._ItemsColorNamePrimal + " usunela wojne z wasza gildia");
		});
		createInventory();
	}
	
	public void showGuilds_AddWar()
	{
		clearInventory();
		Guild gp = guildPlayer.getGuild();
		IntStream.range(0, inventory.getSize()).forEach(i -> this.setItem(i, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE)));
		List<GuildDiplomacy> allDiplomacy = gp.getAllDiplomacy();
		Collection<Guild> allGuilds = GuildsManager.getGuildManager().getAllGuild();
		allGuilds.remove(gp);
		allGuilds.removeAll(allDiplomacy.stream().map(gd -> gd.getReciver()).collect(Collectors.toList()));
		List<? extends Guild> newList = allGuilds.stream()
			.collect(Collectors.toCollection(ArrayList::new)).subList(page * 14, allGuilds.size() > ((page + 1) * 14) ? ((page + 1) * 14) : allGuilds.size());
		IntStream.range(0, newList.size())
			.forEach(i -> 
			{
				Guild g = newList.get(i);
				this.setItem(playerContainer[i],
					ItemsCreator.getItemStack(Material.GRAY_BANNER, GuildsManager._ItemsColorNamePrimal + "LPM Zeby ustawic wojne z gildia: " + GuildsManager._ItemsColorNameSecond + newList.get(i).getName()),
						e -> setWar(g));
			}
		);
		
		this.setItem(35,
			this.getSkullItem(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdhYWNhZDE5M2UyMjI2OTcxZWQ5NTMwMmRiYTQzMzQzOGJlNDY0NGZiYWI1ZWJmODE4MDU0MDYxNjY3ZmJlMiJ9fX0=",
				GuildsManager._ItemsColorNamePrimal + "Poprzednia strona"),
			e -> createInventory());
		buttons_NextPrevPage(newList.size(),() -> showGuilds_AddWar());
	}
	
	
	public void showGuilds_AddAlly() 
	{
		clearInventory();
		Guild gp = guildPlayer.getGuild();
		GuildManager gm = GuildsManager.getGuildManager().getGuildManager(gp);
		IntStream.range(0, inventory.getSize()).forEach(i -> this.setItem(i, ItemsCreator.getClean(Material.GRAY_STAINED_GLASS_PANE)));
		List<GuildDiplomacy> allDiplomacy = gp.getAllDiplomacy();
		Collection<Guild> allGuilds = GuildsManager.getGuildManager().getAllGuild();
		//System.out.println("TOREM I: " + gp);
		allGuilds.remove(gp);

		//System.out.println("TOREM List: ");
		//torem.forEach(tr -> System.out.println(tr));
		allGuilds.removeAll( allDiplomacy.stream().map(gd -> gd.getReciver()).collect(Collectors.toList()));
		List<? extends Guild> newList = allGuilds.stream()
			.collect(Collectors.toCollection(ArrayList::new)).subList(page * 14, allGuilds.size() > ((page + 1) * 14) ? ((page + 1) * 14) : allGuilds.size());
		IntStream.range(0, newList.size())
			.forEach(i -> 
			{
				Guild g = newList.get(i);
				//System.out.println(g);
				boolean hasInv = GuildsManager.getGuildManager().getGuildManager(g).containsAllyInvitation(gp);
				boolean isInv = gm.containsAllyInvitation(g);
				if(!hasInv)
					if(isInv)
						this.setItem(playerContainer[i],ItemsCreator.getItemStack(Material.PURPLE_BANNER, GuildsManager._ItemsColorNamePrimal + "LPM zeby akceptowac zaproszenie od " +
							GuildsManager._ItemsColorNameSecond + newList.get(i).getName() + GuildsManager._ItemsColorNamePrimal +" do sojuszu"),
								e -> acceptAllyInvitation(g));
					else
						this.setItem(playerContainer[i],ItemsCreator.getItemStack(Material.GRAY_BANNER, GuildsManager._ItemsColorNamePrimal + "LPM zeby zaprosic " +
							GuildsManager._ItemsColorNameSecond + newList.get(i).getName() + GuildsManager._ItemsColorNamePrimal +" do sojuszu"),
								e -> sendAllyInvitation(g));
				else
					this.setItem(playerContainer[i],ItemsCreator.getItemStack(hasInv ? Material.BLACK_BANNER : Material.GRAY_BANNER,
						GuildsManager._ItemsColorNameSecond + newList.get(i).getName() + GuildsManager._ItemsColorNamePrimal +" posiada zaproszenie"));
			}
		);
		
		this.setItem(35,
			this.getSkullItem(
				"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdhYWNhZDE5M2UyMjI2OTcxZWQ5NTMwMmRiYTQzMzQzOGJlNDY0NGZiYWI1ZWJmODE4MDU0MDYxNjY3ZmJlMiJ9fX0=",
				GuildsManager._ItemsColorNamePrimal + "Poprzednia strona"),
			e -> createInventory());
		buttons_NextPrevPage(newList.size(),() -> showGuilds_AddAlly());
	}
	
	
	public void setWar(Guild g)
	{
		if(g.getAllDiplomacy().stream().filter(gd -> gd.getDiplomacyType() == DiplomacyType.WAR).count() <= 4)
		{
			GuildDiplomacy gd = new GuildDiplomacy();
			gd.setDeclared(guildPlayer.getGuild());
			gd.setReciver(g);
			gd.setDiplomacyType(DiplomacyType.WAR);
			guildPlayer.getGuild().getAllDiplomacy().add(gd);
			GuildPluginMain.eserver.save(gd.getDeclared());
			//GuildPluginMain.eserver.save(gd);
			g.getAllOnlineGuildPlayers().forEach(gp -> {if(gp.getPlayer() == null) gp.init(); gp.getPlayer().sendMessage(GuildsManager._GlobalPrefix + "Gildia" +gd.getDeclared().getName()+" ustawila status wojna z wasza gildia");});
			guildPlayer.getGuild().getAllOnlineGuildPlayers().forEach(gp -> {if(gp.getPlayer() == null) gp.init(); gp.getPlayer().sendMessage(GuildsManager._GlobalPrefix + "Twoja gildia zmienila status z gildia " +g.getName()+" na wojna");});
			
			//player.sendMessage(GuildsManager._GlobalPrefix + "Wojna z gildia " + GuildsManager._ItemsColorNameSecond + g.getName() + " zostala wypowiedzana");
		}
		else
		{
			player.sendMessage(GuildsManager._GlobalPrefix + "Nie udalo sie wypowiedziec wojny gildi: " + GuildsManager._ItemsColorNameSecond + g.getName());
		}
		showGuilds_AddWar();
	}
	
	public void acceptAllyInvitation(Guild g)
	{
		GuildManager gm = GuildsManager.getGuildManager().getGuildManager(guildPlayer.getGuild());

		if(gm.tryCreateAlly(g))
		{
			final String  gn = g.getName();
			g.getAllOnlineGuildPlayers().forEach(p -> p.getPlayer().sendMessage(GuildsManager._GlobalPrefix + "Sojusz z gildia: " + GuildsManager._ItemsColorNameSecond + gn + " stworzony"));
			final String gn2 = guildPlayer.getGuild().getName();
			guildPlayer.getGuild().getAllOnlineGuildPlayers().forEach(p -> p.getPlayer().sendMessage(GuildsManager._GlobalPrefix + "Sojusz z gildia: " + GuildsManager._ItemsColorNameSecond + gn2 + " stworzony"));
		}
		else player.sendMessage(GuildsManager._GlobalPrefix + "Sojusz z gildia: " + GuildsManager._ItemsColorNameSecond + g.getName() + " nie zostal utworzony");
		showGuilds_AddAlly();
	}
	
	public void sendAllyInvitation(Guild g)
	{
		if(GuildsManager.getGuildManager().getGuildManager(g).addAlyInvitation(guildPlayer.getGuild()))
			player.sendMessage(GuildsManager._GlobalPrefix+ "Wyslano propozycje sojuszu gildi: "+ GuildsManager._ItemsColorNameSecond+ g.getName());
		showGuilds_AddAlly();
		
	}
	
}
