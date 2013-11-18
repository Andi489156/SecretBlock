package io.github.Andi489156.SecretBlock;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
 
public class SerialLocation implements Serializable
{
	private static final long serialVersionUID = 1L;
	private double x, y, z;
    private String worldName;
   
    public SerialLocation(Location location)
    {
    	 x = location.getX();
         y = location.getY();
         z = location.getZ();
         worldName = location.getWorld().getName();
    }
   
    public Location getLocation()
    {
    	World world = Bukkit.getWorld(worldName);
        if (world == null)
            return null;
        return new Location(world, x, y, z);
    }
    
    public Block getBlock(){
    	World world = Bukkit.getWorld(worldName);
        if (world == null)
            return null;
        Block b = world.getBlockAt((int)x, (int)y, (int)z);
        return b;
    }
   
}