package io.github.Andi489156.SecretBlock;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SecretController implements Serializable{
	private static final long serialVersionUID = 1L;
	private String ownerName;
	private ArrayList<String> user = new ArrayList<String>();
	private SerialLocation ironBlockLocation;
	private ArrayList<SerialLocation> blockLocation = new ArrayList<SerialLocation>();
	private Map<SerialLocation, Integer> blockType = new HashMap<SerialLocation, Integer>();
	
	public SecretController(Player owner, Block ironBlock, ArrayList<Block> block) {
		this.setOwner(owner);
		this.ironBlockLocation = new SerialLocation(ironBlock.getLocation());
		blockLocation.clear();
		blockType.clear();
		for(int i=0; i<block.size(); i++){
			blockLocation.add(new SerialLocation(block.get(i).getLocation()));
			blockType.put(blockLocation.get(i), Integer.valueOf(block.get(i).getTypeId()));
		}
	}

	public void vanishBlocks(){
		for(int i=0; i<blockLocation.size(); i++){
			blockLocation.get(i).getLocation().getBlock().setType(Material.AIR);
		}
	}
	
	public void showBlocks(){
		for(int i=0; i<blockLocation.size(); i++){
			blockLocation.get(i).getLocation().getBlock().setTypeId(blockType.get(blockLocation.get(i)));
		}
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	
	public void setOwner(Player owner){
		this.ownerName = owner.getName();
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public Block getIronBlock(){
		return ironBlockLocation.getBlock();
	}
	
	public ArrayList<Location> getBlockLocation() {
		ArrayList<Location> location = new ArrayList<Location>();
		for(int i=0; i<blockLocation.size(); i++){
			location.set(i, blockLocation.get(i).getLocation());
		}
		return location;
	}

	public Map<Location, Integer> getBlockType() {
		Map<Location, Integer> type = new HashMap<Location, Integer>(); 
		for (Entry<SerialLocation, Integer> entry : blockType.entrySet()) {
			type.put(entry.getKey().getLocation(), entry.getValue());
		}
		return type;
	}

	public ArrayList<Block> getControlledBlocks() {
		ArrayList<Block> controlledBlocks = new ArrayList<Block>();
		for(int i=0; i<blockLocation.size(); i++){
			controlledBlocks.add(blockLocation.get(i).getBlock());
		}
		return controlledBlocks;
	}

	public ArrayList<String> getUser() {
		return user;
	}

	public void addUser(String user) {
		this.user.add(user);
	}
	
}
