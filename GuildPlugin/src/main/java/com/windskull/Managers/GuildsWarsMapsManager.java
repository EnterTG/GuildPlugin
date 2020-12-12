package com.windskull.Managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.windskull.Wars.GuildsWars_Map;

public class GuildsWarsMapsManager
{

	private static GuildsWarsMapsManager guildsWarsMapsManager;

	private List<GuildsWars_Map> guildWarsMaps = new ArrayList<>();

	private GuildsWarsMapsManager()
	{
		GuildsWars_Map m1 = new GuildsWars_Map();
		m1.attackerSpawnPoint = new Location(Bukkit.getWorld("MapaServerMC"), 100, 100, 100);
		m1.defenderSpawnPoint = new Location(Bukkit.getWorld("MapaServerMC"), 120, 100, 100);
		guildWarsMaps.add(m1);

	}

	public static GuildsWarsMapsManager getInstance()
	{
		if (guildsWarsMapsManager == null)
		{
			guildsWarsMapsManager = new GuildsWarsMapsManager();
		}
		return guildsWarsMapsManager;
	}

	public void addGuildsWarsMap(GuildsWars_Map map)
	{
		guildWarsMaps.add(map);
	}

	public Optional<GuildsWars_Map> getFreeMap()
	{
		return guildWarsMaps.stream().filter(gm -> !gm.isUsed).findFirst();
	}

}
