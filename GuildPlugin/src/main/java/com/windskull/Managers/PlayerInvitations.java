package com.windskull.Managers;

import java.util.ArrayList;
import java.util.List;

import com.windskull.GuildPlugin.Guild;

public class PlayerInvitations {
	
	public List<Guild> guildsInvitation = new ArrayList<Guild>();
	
	public int getInvitationAmount()
	{
		return guildsInvitation.size();
	}
	
	public void addInvitation(Guild g)
	{
		guildsInvitation.add(g);
	}
}
