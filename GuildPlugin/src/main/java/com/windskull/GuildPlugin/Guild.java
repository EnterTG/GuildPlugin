	
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
import javax.persistence.Transient;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.windskull.Managers.GuildsManager;
import com.windskull.Misc.GuildDataBaseFunctions;
import com.windskull.Wars.GuildWarPreapare;


@Entity
@Table(name="Guilds")
public class Guild 
{
	
	public class TMP_GuildBuilding
	{
		
		
		public String buildingName;
		public TMP_GuildBuilding(String buildingName, int buildingLevel) {
			super();
			this.buildingName = buildingName;
			this.buildingLevel = buildingLevel;
		}
		public int buildingLevel;
	}
	
	
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
    
    @OneToMany(mappedBy="guild", targetEntity=GuildStorage.class, cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
    private List<GuildStorage> allGuildStorage = new ArrayList<GuildStorage>();
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
    
    @Column(columnDefinition = "integer default 1000")
    private int mmr;
    
    @Column
    private String guildBuildings;
   /* @DbJsonB
    Map<String,Integer> guildBuildings;*/
    @Transient
    private List<GuildPlayer> allOnlineGuildPlayers = new ArrayList<GuildPlayer>();
    @Transient
    public List<TMP_GuildBuilding> guildBuildingsMaped = new ArrayList<Guild.TMP_GuildBuilding>();
    @Transient
    public GuildWarPreapare guildWars = new GuildWarPreapare(this);
    
    
    
   // @PreUpdate
    public void updateGuildBuildings()
    {
    	GuildDataBaseFunctions.updateGuildBuildings(this, getGuildBuildingsMaped());
    }
    //@PostLoad
    public void mapGuildBuildings()
    {
    	GuildDataBaseFunctions.mapGuildBuildings(this, getGuildBuildings(), getGuildBuildingsMaped());
    }
    

    
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
	
	public List<GuildStorage> getAllGuildStorage() {
		return this.allGuildStorage;
	}

	public void setAllGuildStorage(List<GuildStorage> allGuildStorage) {
		this.allGuildStorage = allGuildStorage;
	}
	
	public void playerLogIn(GuildPlayer gp)
	{
		allOnlineGuildPlayers.add(gp);
		gp.init();
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

	public String getGuildBuildings() {
		return guildBuildings;
	}
	public void setGuildBuildings(String guildBuildings) {
		this.guildBuildings = guildBuildings;
	}
	public List<TMP_GuildBuilding> getGuildBuildingsMaped() {
		return guildBuildingsMaped;
	}
	public void setGuildBuildingsMaped(List<TMP_GuildBuilding> guildBuildingsMaped) {
		this.guildBuildingsMaped = guildBuildingsMaped;
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

	public int getMmr() {
		return mmr;
	}
	public void setMmr(int mmr) {
		this.mmr = mmr;
	}
	public void addNewGuildPlayer(GuildPlayer guildPlayer)
	{
		allOnlineGuildPlayers.add(guildPlayer);
		allGuildPlayer.add(guildPlayer);
		GuildsManager.getGuildManager().addGuildPlayer(guildPlayer);
	}

	public void addGuildStorage(GuildStorage gs)
	{
		allGuildStorage.add(gs);
	}
	
	public String getFullString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "ID: " + id);
		builder.append(System.lineSeparator() + "Name: " + name);
		builder.append(System.lineSeparator() + "Tag: " + tag);
		builder.append(System.lineSeparator() + "World name: " + worldName);
		builder.append(System.lineSeparator() + "X Y Z: " + guildLoc_X + " " + guildLoc_Y + " " + guildLoc_Z);
		builder.append(System.lineSeparator() + "MMR: " + mmr);
		builder.append(System.lineSeparator() + "Region: " + guildRegionId);
		builder.append(System.lineSeparator() + "Level: " + guildLevel);
		builder.append(System.lineSeparator() + "Buildings: " + guildBuildings);
		return builder.toString();
	}
	
	
	@Override
	public String toString() {

		return System.lineSeparator() + "Guild ID: " + id + System.lineSeparator() +
				"Guild Name: " + name + System.lineSeparator() + 
				"Guild Tag: " + tag + System.lineSeparator() +
				"Guild Queue: " + guildWars.guildQueueStatus + System.lineSeparator() +
				"Guild MMR: " + mmr + System.lineSeparator() ;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}

