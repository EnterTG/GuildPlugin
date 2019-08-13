package com.windskull.GuildPlugin;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Entity
@Table(name="PlayersGuilds")
public class GuildPlayer {
    @Id
    private int id;
    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="playerGuild")
    private Guild guild;
    @Column
    private UUID playeruuid;
    @Column
    private GuildRanks rang;
    
    @Transient
    private Player player;

    
    
    public GuildPlayer()
    {
    	
    }
    public void init()
    {
    	player = Bukkit.getPlayer(playeruuid);
    }

    public GuildPlayer(Player p, GuildRanks rang, Guild g) {
    	this.rang = rang;
    	this.playeruuid = p.getUniqueId();
    	guild = g;
	}

	public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public UUID getPlayeruuid() {
        return this.playeruuid;
    }

    public void setPlayeruuid(UUID playeruuid) {
        this.playeruuid = playeruuid;
    }

    public GuildRanks getRang() {
        return this.rang;
    }

    public void setRang(GuildRanks rang) {
        this.rang = rang;
    }

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((playeruuid == null) ? 0 : playeruuid.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GuildPlayer other = (GuildPlayer) obj;
		if (playeruuid == null) {
			if (other.playeruuid != null)
				return false;
		} else if (!playeruuid.equals(other.playeruuid))
			return false;
		return true;
	}
    

}

