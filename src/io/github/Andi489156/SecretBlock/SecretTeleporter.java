package io.github.Andi489156.SecretBlock;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SecretTeleporter implements Serializable{
	private static final long serialVersionUID = 1L;
	private SerialLocation blockLocation;
	private SerialLocation teleportLocation;
	private String ownerName;
	
	public SecretTeleporter(Block block, Location destination, Player owner) {
		this.blockLocation = new SerialLocation(block.getLocation());
		this.teleportLocation = new SerialLocation(destination);
		this.ownerName = owner.getName();
	}

	public Block getBlock() {
		return blockLocation.getBlock();
	}

	public Location getTeleportLocation() {
		return teleportLocation.getLocation();
	}

	public String getOwnerName() {
		return ownerName;
	}
}
