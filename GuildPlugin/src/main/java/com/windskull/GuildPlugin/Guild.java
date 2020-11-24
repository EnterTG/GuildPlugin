
package com.windskull.GuildPlugin;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.bukkit.Bukkit;
import org.bukkit.Location;


@Entity
@Table(name="Guilds")
public class Guild {
    @Id
    private int id;
    @Column
    private String name;
    @Column
    private String tag;
    @Column
    private String opis;
    @OneToMany(mappedBy="guild", targetEntity=GuildPlayer.class, cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private List<GuildPlayer> allGuildPlayer = new ArrayList<GuildPlayer>();
    
    @Column
    private String guildRegionId;
    
    @Column
    private int guildLoc_X;
    @Column
    private int guildLoc_Y;
    @Column
    private int guildLoc_Z;
    @Column
    private String worldName;
    @Column
    private int guildLevel;
    
    
    private List<GuildPlayer> allOnlineGuildPlayers = new ArrayList<GuildPlayer>();
    
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getOpis() {
        return this.opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

  
    
    public List<GuildPlayer> getAllGuildPlayer() {
        return this.allGuildPlayer;
    }

    public void setAllGuildPlayer(List<GuildPlayer> allGuildPlayer) {
        this.allGuildPlayer = allGuildPlayer;
    }

	public List<GuildPlayer> getAllOnlineGuildPlayers() {
		return allOnlineGuildPlayers;
	}

	public void setAllOnlineGuildPlayers(List<GuildPlayer> allOnlineGuildPlayers) {
		this.allOnlineGuildPlayers = allOnlineGuildPlayers;
	}
	

	public void playerLogIn(GuildPlayer gp)
	{
		allOnlineGuildPlayers.add(gp);
		gp.init();
	}
	public void addNewGuildPlayer(GuildPlayer guildPlayer)
	{
		allOnlineGuildPlayers.add(guildPlayer);
		allGuildPlayer.add(guildPlayer);
	}

	public void playerLogOut(GuildPlayer dgp) {
		
		allOnlineGuildPlayers.remove(dgp);
	}
	public void removePlayerFromGuild(GuildPlayer gp)
	{
		allOnlineGuildPlayers.remove(gp);
		allGuildPlayer.remove(gp);
		GuildPluginMain.eserver.delete(GuildPlayer.class, gp.getId());
	}

	public String getGuildRegionId() {
		return guildRegionId;
	}

	public void setGuildRegionId(String guildRegionId) {
		this.guildRegionId = guildRegionId;
	}

	public int getGuildLevel() {
		return guildLevel;
	}

	public void setGuildLevel(int guildLevel) {
		this.guildLevel = guildLevel;
	}
	
	public void setGuildLocation(Location loc)
	{
		setGuildLoc_X(loc.getBlockX());
		setGuildLoc_Y(loc.getBlockY());
		setGuildLoc_Z(loc.getBlockZ());
		setWorldName(loc.getWorld().getName());
		
	}
	
	public Location getGuildLocation()
	{
		return new Location(Bukkit.getWorld(getWorldName()), getGuildLoc_X(), getGuildLoc_Y(), getGuildLoc_Z());
	}
	
	public int getGuildLoc_X() {
		return guildLoc_X;
	}

	public void setGuildLoc_X(int guildLoc_X) {
		this.guildLoc_X = guildLoc_X;
	}

	public int getGuildLoc_Y() {
		return guildLoc_Y;
	}

	public void setGuildLoc_Y(int guildLoc_Y) {
		this.guildLoc_Y = guildLoc_Y;
	}

	public int getGuildLoc_Z() {
		return guildLoc_Z;
	}

	public void setGuildLoc_Z(int guildLoc_Z) {
		this.guildLoc_Z = guildLoc_Z;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}
}

