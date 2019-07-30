package com.windskull.DTO;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
@Entity
@Table(name = "Guilds")
public class DTO_Guild {

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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the opis
	 */
	public String getOpis() {
		return opis;
	}

	/**
	 * @param opis the opis to set
	 */
	public void setOpis(String opis) {
		this.opis = opis;
	}

	/**
	 * @return the allGuildPlayer
	 */
	public List<DTO_GuildPlayer> getAllGuildPlayer() {
		return allGuildPlayer;
	}

	/**
	 * @param allGuildPlayer the allGuildPlayer to set
	 */
	public void setAllGuildPlayer(List<DTO_GuildPlayer> allGuildPlayer) {
		this.allGuildPlayer = allGuildPlayer;
	}

	public DTO_Guild() 
	{
	
	}
	@Id
	private int id;
	
	@Column
	private String name;
	@Column
	private String tag;
	@Column
	private String opis;
	
	
	@OneToMany(mappedBy = "guild", targetEntity = DTO_GuildPlayer.class)
	private List<DTO_GuildPlayer> allGuildPlayer;
	
	

}
