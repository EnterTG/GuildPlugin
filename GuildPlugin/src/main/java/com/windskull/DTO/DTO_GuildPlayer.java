package com.windskull.DTO;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.windskull.GuildPlugin.GuildRanks;
@Entity
@Table(name = "GuildPlayers")
public class DTO_GuildPlayer {

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the guild
	 */
	public DTO_Guild getGuild() {
		return guild;
	}

	/**
	 * @param guild the guild to set
	 */
	public void setGuild(DTO_Guild guild) {
		this.guild = guild;
	}

	/**
	 * @return the playeruuid
	 */
	public UUID getPlayeruuid() {
		return playeruuid;
	}

	/**
	 * @param playeruuid the playeruuid to set
	 */
	public void setPlayeruuid(UUID playeruuid) {
		this.playeruuid = playeruuid;
	}

	/**
	 * @return the rang
	 */
	public GuildRanks getRang() {
		return rang;
	}

	/**
	 * @param rang the rang to set
	 */
	public void setRang(GuildRanks rang) {
		this.rang = rang;
	}
	@Id
	private int id;
	
	
	@ManyToOne
	private DTO_Guild guild;
	
	@Column
	private UUID playeruuid;
	
	@Column
	private GuildRanks rang;
	
	public DTO_GuildPlayer() 
	{
	}
	
	
	

}
