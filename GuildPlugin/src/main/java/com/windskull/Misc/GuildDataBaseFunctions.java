package com.windskull.Misc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.windskull.GuildPlugin.Guild;

public class GuildDataBaseFunctions {

	
	public static void updateGuildBuildings(Guild g,List<Guild.TMP_GuildBuilding> guildBuildingsMaped)
    {
		try 
		{
			
			String build = guildBuildingsMaped.stream().map(s -> (s.buildingName+":"+s.buildingLevel)).collect(Collectors.joining(";"));
			//System.out.println("Buildings: " + build);
			if(!build.equals(""))
				g.setGuildBuildings(build);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }
	
	public static void mapGuildBuildings(Guild g,String guildBuildings,List<Guild.TMP_GuildBuilding> guildBuildingsMaped)
    {
		if(guildBuildings != null)
	    	Arrays.stream(guildBuildings.split(";")).forEach(s -> 
	    	{
	    		if(s.contains(":"))
	    		{
		    		String[] values = s.split(":");
		    		Preconditions.checkArgument(values.length == 2, "Error while parsing guild building in guild: " +g.getName(), values.length);
		    		try
		    		{
		    			guildBuildingsMaped.add(g.new TMP_GuildBuilding(values[0],Integer.parseInt(values[1])));
		    		}
		    		catch (NumberFormatException e) {
		    			System.out.print("Error while prasing int in: " + g.getName() +" Value: " + values[1]);
						//e.printStackTrace();
					}
	    		}
	    	
	    	});
    }
	
}
