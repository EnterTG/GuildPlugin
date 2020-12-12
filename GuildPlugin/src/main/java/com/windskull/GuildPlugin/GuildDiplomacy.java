package com.windskull.GuildPlugin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Diplomacy")
public class GuildDiplomacy
{

	@Id
	int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "Declared")
	Guild declared;

	@JoinColumn(name = "Reciver")
	@ManyToOne(fetch = FetchType.EAGER)
	Guild reciver;

	@Column
	DiplomacyType diplomacyType;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public Guild getDeclared()
	{
		return declared;
	}

	public void setDeclared(Guild declared)
	{
		this.declared = declared;
	}

	public Guild getReciver()
	{
		return reciver;
	}

	public void setReciver(Guild reciver)
	{
		this.reciver = reciver;
	}

	public DiplomacyType getDiplomacyType()
	{
		return diplomacyType;
	}

	public void setDiplomacyType(DiplomacyType diplomacyType)
	{
		this.diplomacyType = diplomacyType;
	}

}
