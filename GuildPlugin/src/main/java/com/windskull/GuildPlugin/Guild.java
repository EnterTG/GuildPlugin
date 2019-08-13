
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
}

