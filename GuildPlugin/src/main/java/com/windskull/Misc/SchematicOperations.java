package com.windskull.Misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.factory.MaskFactory;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.convolution.GaussianKernel;
import com.sk89q.worldedit.math.convolution.HeightMap;
import com.sk89q.worldedit.math.convolution.HeightMapFilter;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.windskull.GuildPlugin.GuildPluginMain;

public class SchematicOperations {

	
	private static final int smoothRadius = 3,smoothHeight = 3,iterations = 3;

	//Schematic 
	public static Clipboard loadClipboard(String fileName) throws FileNotFoundException, IOException
	{
		Clipboard clipboard;	
		File file = new File(GuildPluginMain.main.getDataFolder().getAbsolutePath() + "/GuildSchematic/" + fileName);
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		ClipboardReader reader = format.getReader(new FileInputStream(file));
		clipboard = reader.read();
		return clipboard;
	}
	
	@SuppressWarnings("deprecation")
	public static void pasteSchematic(Clipboard clipboard, Location loc) throws FileNotFoundException, IOException
	{
		
		EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(loc.getWorld()), -1);
		Operation operation = new ClipboardHolder(clipboard)
	            .createPaste(editSession)
	            .to(BlockVector3.at(loc.getX(),loc.getY(),loc.getZ()))
	            //.to(BlockVector3.at(p1.getX(),p1.getY(),p1.getZ()))
	            .ignoreAirBlocks(true).build();
	    Operations.complete(operation);
	    editSession.close();
	}
	
	
	public static Clipboard getClipboard(String fileName)
	{
		Clipboard clipboard;
		try {
			clipboard = loadClipboard(fileName);
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
			
		}
		return clipboard;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean tryPasteGuildBuilding(Location loc,String fileName)
	{
		
		Clipboard clipboard = getClipboard(fileName);
		if(clipboard == null) return false;
		//Location loc = player.getLocation();
		World world = loc.getWorld();
		com.sk89q.worldedit.world.World w = BukkitAdapter.adapt(world);
		
		
		//Origin must be in region
		
		//Check corners height 
		
		BlockVector3 origin = clipboard.getOrigin();
		int width = clipboard.getWidth(), lenght = clipboard.getLength();
		
		BlockVector3 p1 = BlockVector3.at(loc.getX() - origin.getX(), loc.getY() - origin.getY(), loc.getZ() - origin.getZ());
		BlockVector3 p2 = p1.add(width, 0, 0);
		BlockVector3 p3 = p1.add(0, 0, lenght);
		BlockVector3 p4 = p1.add(width,0,lenght);

		List<BlockVector3> points = new ArrayList<BlockVector3>();
		
		points.add(p1);
		points.add(p2);
		points.add(p3);
		points.add(p4);
		
		//points.forEach(p -> System.out.println("Point: " + p));
		
		for(BlockVector3 a : points)
		{
			for(BlockVector3 b : points)
			{
				if(a != b)
				{
				
					int h1 = getHight(world,a.getBlockX(),a.getBlockZ(),a.getY()-10,a.getBlockY()+10);//w.getHighestTerrainBlock(a.getBlockX(), a.getBlockZ(), a.getBlockY()-10, a.getBlockY()+10);
					int h2 = getHight(world,b.getBlockX(),b.getBlockZ(),b.getY()-10,b.getBlockY()+10);//w.getHighestTerrainBlock(b.getBlockX(), b.getBlockZ(), b.getBlockY()-10, b.getBlockY()+10);
					
					//System.out.println("ABS: " + Math.abs(h1-h2));
					if(Math.abs((h1 - h2)) >5)
					{
						//System.out.println("Bledne abs: " +Math.abs(h1 - h2) + " h1: " + h1 + " h2: " + h2	 );
						return false;
					}
				}
			}
		}
		
		try 
		{
			//System.out.println("Stworzono teren gildi: " + guildPlayer.getGuild().getName());
			pasteSchematic(clipboard,loc);
			
			Bukkit.getScheduler().runTaskAsynchronously(GuildPluginMain.main, new Runnable() 
			{
				public void run() 
				{ 
					
					EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(loc.getWorld()), -1);
					Region r = new CuboidRegion(p1.subtract(0,20,0), p4.add(0, clipboard.getRegion().getHeight(), 0));
					editSession.replaceBlocks(r, BlockTypes.OBSIDIAN.toMask(editSession), BlockTypes.AIR);
					
					
					ParserContext parserContext = new ParserContext();
				    parserContext.setWorld(w);
			        parserContext.setRestricted(false);
				    
				    //Create regions on edge of schematic and perform smoothing on terrain
						
					CuboidRegion region1 = new CuboidRegion(p1.subtract(smoothRadius,-smoothHeight,smoothRadius), p2.add(smoothRadius, smoothHeight, smoothRadius));
					CuboidRegion region2 = new CuboidRegion(p2.add(-smoothRadius, smoothHeight, -smoothRadius), p4.add(smoothRadius,-smoothHeight,smoothRadius));
					CuboidRegion region3= new CuboidRegion(p4.add(smoothRadius,smoothHeight,smoothRadius), p3.subtract(smoothRadius,smoothHeight,smoothRadius));
					CuboidRegion region4 = new CuboidRegion(p3.add(smoothRadius,smoothHeight,smoothRadius), p1.add(-smoothRadius,-smoothHeight,smoothRadius));
					List<CuboidRegion> regions = new ArrayList<CuboidRegion>();
					regions.add(region1);
					regions.add(region2);
					regions.add(region3);
					regions.add(region4);
				 
					
				  
					LocalSession session = new LocalSession();
					
					session.setWorldOverride(w);
					
					MaskFactory mf = WorldEdit.getInstance().getMaskFactory();
					Mask mask = mf.parseFromInput("grass,stone,cobblestone,dirt", parserContext);
						
					
					
					regions.forEach(region -> {
						HeightMap heightMap = new HeightMap(editSession, region, mask);
				        HeightMapFilter filterr = new HeightMapFilter(new GaussianKernel(width > lenght ? width : lenght, 1.0));
				        heightMap.applyFilter(filterr, iterations);		        
					});
					
				}
			});
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
			
		} 			
		return true;
	}
	
	public static int getHight(World w,int x,int z,int miny,int maxy)
	{
		/*int minys = miny;
		if(w.getBlockAt(x, minys, z).getType().isAir()) minys = minys+1;
		if(w.getBlockAt(x, minys, z).getType().isAir()) minys = minys+1;*/
		//System.out.print("Looking at: x: " +x +" z: " + z  );
		for(int y = maxy; y > miny;y--)
		{
			
			Material mat = w.getBlockAt(x, y, z).getType();
			if(mat.equals(Material.DIRT) || mat.equals(Material.COBBLESTONE) || mat.equals(Material.GRASS_BLOCK) || mat.equals(Material.STONE))
			{
				//System.out.println(" Found on: " + y);
				return y;
			}
			
		}
		return maxy;
	}
	
}
