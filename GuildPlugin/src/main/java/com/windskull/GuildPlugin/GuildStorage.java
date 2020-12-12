package com.windskull.GuildPlugin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "GuildsStorage")
public class GuildStorage
{

	@Id
	private int id;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "guildStorage")
	private Guild guild;

	@Lob
	private String storageContent;

	@Column
	private int size;

	@Column
	private StorageType storageType;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public Guild getGuild()
	{
		return guild;
	}

	public void setGuild(Guild guild)
	{
		this.guild = guild;
	}

	public String getStorageContent()
	{
		return storageContent;
	}

	public void setStorageContent(String storageContent)
	{
		this.storageContent = storageContent;
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

	public StorageType getStorageType()
	{
		return storageType;
	}

	public void setStorageType(StorageType storageType)
	{
		this.storageType = storageType;
	}

}
