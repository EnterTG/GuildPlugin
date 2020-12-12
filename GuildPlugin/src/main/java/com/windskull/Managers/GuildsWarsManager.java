package com.windskull.Managers;

import java.util.ArrayList;
import java.util.List;

import com.windskull.Wars.GuildsWar;

public class GuildsWarsManager
{

	private static GuildsWarsManager guildsWarsManager;

	private GuildsWarsManager()
	{

	}

	public static GuildsWarsManager getInstance()
	{
		if (guildsWarsManager == null)
		{
			guildsWarsManager = new GuildsWarsManager();
		}
		return guildsWarsManager;
	}

	private List<GuildsWar> guildsWars = new ArrayList<>();

	public void addGuildWar(GuildsWar gw)
	{
		guildsWars.add(gw);
	}

	public void removeGuildWar(GuildsWar gw)
	{
		guildsWars.remove(gw);
	}

}
