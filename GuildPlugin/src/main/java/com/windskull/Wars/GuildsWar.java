package com.windskull.Wars;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.earth2me.essentials.Essentials;
import com.windskull.GuildPlugin.Guild;
import com.windskull.GuildPlugin.GuildPluginMain;
import com.windskull.Managers.GuildsManager;
import com.windskull.Misc.PlayerHandler;

public class GuildsWar implements Listener
{
	
	private GuildsWars_Map guildsWarsMap;
	
	private GuildWarPreapare attackerGuild;
	private GuildWarPreapare defenderGuild;
	
	private boolean isStarted = false;
	
	
	private int attackerGuildLifes = 10,defenderGuildLifes = 10;
	
	private HashMap<Player,Boolean> cashe = new HashMap<Player, Boolean>();

	private HashMap<UUID,Boolean> leavers =  new HashMap<UUID,Boolean>();
	
	//private final int maxWarPlayers;
	
	public GuildsWar(GuildWarPreapare attacker,GuildWarPreapare defender,GuildsWars_Map guildsWarsMap)
	{
		//maxWarPlayers = (int) (defender.getPlayersQueue().length * 1.2);
		this.attackerGuild = attacker;
		this.defenderGuild = defender;
		this.guildsWarsMap = guildsWarsMap;
		
		Arrays.stream(attackerGuild.getPlayersQueue()).forEach(p -> cashe.put(p.getPlayer(), true));
		Arrays.stream(defenderGuild.getPlayersQueue()).forEach(p -> cashe.put(p.getPlayer(), false));

		GuildPluginMain.server.getPluginManager().registerEvents(this, GuildPluginMain.main);
		GuildPluginMain.server.getScheduler().runTaskLaterAsynchronously(GuildPluginMain.main, () -> timerOff(), 5*60*20);

	
	}
	
	public void playerDeath(Player player)
	{
		if(isStarted) 
		{
			boolean attacker = cashe.get(player).booleanValue();

			if(attacker)
			{
				if(--attackerGuildLifes <= 0) guildWin(defenderGuild);
				else
					player.teleport(guildsWarsMap.attackerSpawnPoint);
			}
			else
				if(--defenderGuildLifes <= 0) guildWin(attackerGuild);
				else
					player.teleport(guildsWarsMap.defenderSpawnPoint);
		}
	}
	

	
	public void startWar()
	{
		isStarted = true;
		
		attackerGuild.guildQueueStatus = GuildQueueStatus.INMATCH;
		defenderGuild.guildQueueStatus = GuildQueueStatus.INMATCH;
		
		Bukkit.getScheduler().callSyncMethod(GuildPluginMain.main, new Callable<Boolean>() {

			@Override
			public Boolean call() {
				cashe.forEach((p,v) ->{p.sendMessage(GuildsManager._GlobalPrefix+ "Wojna rozopczela sie"); p.teleport((v.booleanValue() ? guildsWarsMap.attackerSpawnPoint : guildsWarsMap.defenderSpawnPoint )); });
				return true;
			}
		});
		
	}

		
	public int maxWarPlayers()
	{
		return 0;// maxWarPlayers;
	}
	
	private void timerOff()
	{
		if(attackerGuildLifes > defenderGuildLifes) guildWin(attackerGuild);
		else guildWin(defenderGuild);
			
	}
	
	public void guildWin(GuildWarPreapare g)
	{
		
		
		isStarted = false;
		cashe.forEach((p,v) ->{p.sendMessage(GuildsManager._GlobalPrefix+ "Wojna zakonczyla sie"); p.teleport((v.booleanValue() ? attackerGuild : defenderGuild ).getGuild().getGuildLocation()); });
		leavers.forEach((p,v) -> offlineTeleport(Bukkit.getOfflinePlayer(p),(v.booleanValue() ? attackerGuild : defenderGuild ).getGuild().getGuildLocation(),v.booleanValue()) );
		EntityDamageEvent.getHandlerList().unregister(this);
		PlayerQuitEvent.getHandlerList().unregister(this);
		PlayerJoinEvent.getHandlerList().unregister(this);
		calculateMMR(g.getGuild(),attackerGuild.getGuild() == g.getGuild() ? defenderGuild.getGuild() : attackerGuild.getGuild());
	}

	public void calculateMMR(Guild winner, Guild losed)
	{
		int winmmr =winner.getMmr();
		int losemmr =losed.getMmr();
		
		winmmr = winmmr + (50 * (losemmr/winmmr));
		losemmr = losemmr - (50 * (losemmr/winmmr));
		System.out.println("Win add mmr: " + winmmr + " Lose sub mmr: " + losemmr);
		
		winner.setMmr(Math.min(winmmr,0));
		losed.setMmr(Math.min(losemmr,0));
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		if(isStarted)
			if(cashe.containsKey(e.getPlayer()))
			{
				leavers.put(e.getPlayer().getUniqueId(),cashe.get(e.getPlayer()));
				cashe.remove(e.getPlayer());
			}
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if(isStarted)
		{
			leavers.remove(e.getPlayer().getUniqueId());
			GuildWarPreapare gwp = GuildsManager.getGuildManager().getGuildPlayer(e.getPlayer()).getGuild().guildWars;
			if(gwp == attackerGuild) cashe.put(e.getPlayer(), true);
			else if(gwp == defenderGuild) cashe.put(e.getPlayer(), false);
		}
	}
	
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e)
	{
		if(isStarted)
			if(e.getEntity() instanceof Player)
			{
				Player player = (Player) e.getEntity();
				
				if(player.getHealth() - e.getFinalDamage() < 1)
				{
					if(cashe.containsKey(player))
					{
						e.setCancelled(true);
						playerDeath(player);
					}
				}
			}
	}	
	
	
	
    /**
     * Set a new location for an offline player.
     * @param player Player to be teleported
     * @param destination New location for the player
     * @return True if the teleport is successful, false otherwise
     */
    private boolean offlineTeleport(OfflinePlayer player, Location destination,boolean attacker)
    {
    	System.out.println("OflineTeleport: " + player.getName());
    	PlayerHandler offlinePlayer = new PlayerHandler(PlayerHandler.findPlayerFile(player.getUniqueId()));
        Location originalLocation = offlinePlayer.getPlayerLocation();
        
        
        if (offlinePlayer.setPlayerLocation(destination)) {
            com.earth2me.essentials.User essentialsUser = ((Essentials)(GuildPluginMain.server.getPluginManager().getPlugin("Essentials")))
                    .getOfflineUser(player.getName());
            essentialsUser.setLastLocation(originalLocation);
            return true;
        } else {
        	Player p = Bukkit.getPlayer(player.getUniqueId());
        	if( p != null)
			{
        		p.teleport((attacker ? attackerGuild : defenderGuild ).getGuild().getGuildLocation());
        		return true;
			}
            return false;
        }
    }
	
}
