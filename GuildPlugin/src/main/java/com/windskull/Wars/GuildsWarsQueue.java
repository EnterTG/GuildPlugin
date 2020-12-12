package com.windskull.Wars;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.windskull.GuildPlugin.GuildPluginMain;
import com.windskull.Managers.GuildsWarsManager;
import com.windskull.Managers.GuildsWarsMapsManager;

public class GuildsWarsQueue
{

	private static GuildsWarsQueue GuildsWarsQueue;

	public class QueuedGuild
	{
		public int mmr;

		public GuildWarPreapare guildWarPreapare;
		public int queueTime = 0;
		public int attempts = 1;

		public QueuedGuild(GuildWarPreapare gwp)
		{
			guildWarPreapare = gwp;
			mmr = gwp.getGuild().getMmr();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof QueuedGuild)
			{
				return ((QueuedGuild) obj).guildWarPreapare.getGuild().getId() == guildWarPreapare.getGuild().getId();
			} else
			{
				return false;
			}
		}

		@Override
		public int hashCode()
		{
			return guildWarPreapare.hashCode();
		}
	}

	public class GuildWarFind
	{
		public QueuedGuild attackerGuild;
		public QueuedGuild defenderGuild;

		public void start(GuildsWars_Map map)
		{
			System.out.println("Start guilds war:  " + attackerGuild.guildWarPreapare.getGuild() + " And " + defenderGuild.guildWarPreapare.getGuild());
			System.out.println();
			GuildsWar guildsWar = new GuildsWar(attackerGuild.guildWarPreapare, defenderGuild.guildWarPreapare, map);
			guildsWar.startWar();
			GuildsWarsManager.getInstance().addGuildWar(guildsWar);

		}

		@Override
		public int hashCode()
		{
			return Objects.hash(attackerGuild, defenderGuild);
		}
	}

	public static GuildsWarsQueue getInstance()
	{
		if (GuildsWarsQueue == null)
		{
			GuildsWarsQueue = new GuildsWarsQueue();
		}
		return GuildsWarsQueue;
	}

	private ArrayList<QueuedGuild> guildsInQueue;
	private final Lock lock = new ReentrantLock();

	@SuppressWarnings("deprecation")
	private GuildsWarsQueue()
	{
		guildsInQueue = new ArrayList<>();

		// For test
		try
		{
			GuildPluginMain.server.getScheduler().scheduleAsyncRepeatingTask(GuildPluginMain.main, () ->
			{
				searchGoodMatchForAll();
			}, 120, 80);
		} catch (Exception e)
		{
			// e.printStackTrace();
		}

	}

	// For test disable scheduler
	public void joinQueue(GuildWarPreapare gwp)
	{

		lock.lock();
		QueuedGuild qGuild = new QueuedGuild(gwp);
		guildsInQueue.add(qGuild);
		gwp.guildQueueStatus = GuildQueueStatus.INQUEUE;
		// System.out.println("Add guild " + gwp.getGuild() +" to queue succes queue size: " + guildsInQueue.size());
		lock.unlock();
	}

	public void leaveQueue(GuildWarPreapare gwp)
	{
		lock.lock();
		QueuedGuild qGuild = new QueuedGuild(gwp);
		guildsInQueue.remove(qGuild);
		// System.out.println("Remove guild " + gwp.getGuild() +" from queue succes queue size: " + guildsInQueue.size());
		gwp.guildQueueStatus = GuildQueueStatus.NOINQUEUE;
		lock.unlock();
	}

	public void searchGoodMatchForAll()
	{
		// System.out.println("Search guilds match guilds in que: " + guildsInQueue.size());
		List<GuildWarFind> guildWarFinds = new ArrayList<>();
		List<QueuedGuild> guildsToRemoveGuilds = new ArrayList<>();
		lock.lock();
		try
		{
			guildsInQueue.forEach(qw ->
			{
				GuildWarFind guildWarFind;
				if (!guildsToRemoveGuilds.contains(qw) && (guildWarFind = searchGoodMatch(qw)) != null)
				{
					guildsToRemoveGuilds.add(guildWarFind.attackerGuild);
					guildsToRemoveGuilds.add(guildWarFind.defenderGuild);
					guildWarFinds.add(guildWarFind);
				}

			});
			guildsInQueue.removeAll(guildsToRemoveGuilds);
			// For tests

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			lock.unlock();
		}
		try
		{
			GuildsWarsMapsManager guildsWarsMapsManager = GuildsWarsMapsManager.getInstance();
			guildWarFinds.stream().filter(f -> f != null).forEach(f ->
			{
				Optional<GuildsWars_Map> map;

				while (!(map = guildsWarsMapsManager.getFreeMap()).isPresent())
				{
					try
					{
						Thread.sleep(10);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				f.start(map.get());
			});
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public synchronized GuildWarFind searchGoodMatch(QueuedGuild gwp)
	{
		int mmr = gwp.mmr;
		int attempts = gwp.attempts;
		Optional<QueuedGuild> opponent = guildsInQueue.stream().filter(g -> (mmr - (100 * attempts) <= g.mmr && g.mmr <= mmr + (100 * attempts) && g != gwp)).findAny();
		if (opponent.isPresent())
		{
			GuildWarFind find = new GuildWarFind();
			find.attackerGuild = gwp;
			find.defenderGuild = opponent.get();
			/*
			 * guildsInQueue.remove(find.attackerGuild);
			 * guildsInQueue.remove(find.defenderGuild);
			 */
			return find;
		} else
		{
			gwp.attempts++;
			return null;
		}

	}

	public ArrayList<QueuedGuild> getGuildsInQueue()
	{
		return guildsInQueue;
	}

	public void setGuildsInQueue(ArrayList<QueuedGuild> guildsInQueue)
	{
		this.guildsInQueue = guildsInQueue;
	}

}
