package io.github.Andi489156.SecretBlock;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;


public final class SecretBlock extends JavaPlugin implements Listener{
	
	private Map<Player, Location> savedLocation = new HashMap<Player, Location>();
	private Map<Player, Block> currentEditingBlock = new HashMap<Player, Block>();
	private Map<Player, ArrayList<Block>> controlledBlockList = new HashMap<Player, ArrayList<Block>>();
	private Map<Player, String> playerToAdd = new HashMap<Player, String>();
	
	private Map<Player, Boolean> onCreateTeleporter = new HashMap<Player, Boolean>();
	private Map<Player, Boolean> onCreateController = new HashMap<Player, Boolean>();
	private Map<Player, Boolean> onEditController = new HashMap<Player, Boolean>();
	private Map<Player, Boolean> onAddUser = new HashMap<Player, Boolean>();
	
	
	private ArrayList<SecretTeleporter> teleporter = new ArrayList<SecretTeleporter>();
	private ArrayList<SecretController> controller = new ArrayList<SecretController>();
	
	private File save_teleporter;
	private File save_controller;
	
	
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		
		//Make sure Data Folder exists
		if(getDataFolder().mkdir()){
			getLogger().warning("Data Folder has been created.");
		}
		
		//Get each save location
		save_teleporter = new File(getDataFolder() + File.separator + "teleporter.bin");
		save_controller = new File(getDataFolder() + File.separator + "controller.bin");
		
		//try to load and to create file if not existing
		try {
			if(save_teleporter.createNewFile()){
				getLogger().warning("Save for teleporters has been created.");
			}
			else{
				try{
					teleporter = SLAPI.load(save_teleporter.getAbsolutePath());
			    }catch(Exception e){
			        e.printStackTrace();
			    }
			}
			
			if(save_controller.createNewFile()){
				getLogger().warning("Save for controllers has been created.");
			}
			else{
				try{
					controller = SLAPI.load(save_controller.getAbsolutePath());
			    }catch(Exception e){
			        e.printStackTrace();
			    }
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		getLogger().info("has been enabled");
	}
	
	@Override
	public void onDisable(){
		try{
			SLAPI.save(teleporter, save_teleporter.getAbsolutePath());
        }catch(Exception e){
             e.printStackTrace();
        }
		
		try{
			SLAPI.save(controller, save_controller.getAbsolutePath());
        }catch(Exception e){
             e.printStackTrace();
        }
		
		getLogger().info("has been disabled");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(args.length < 1){
			return false;
		}
		if(!(sender instanceof Player)){
			sender.sendMessage("Only a player can use this command.");
			return true;
		}
		Player player = (Player) sender;
		
		switch(args[0]){
		
		case "savelocation":
			
				savedLocation.put(player, player.getLocation());
				player.sendMessage("Your location was saved successfully.");
			
			break;
			
		case "create":
			if(args.length < 2){
				return false;
			}
			
			switch(args[1].toLowerCase()){
			
			case "teleporter":
				if (savedLocation.containsKey(player)
						&& savedLocation.get(player) != null) {
					onCreateTeleporter.put(player, true);
					player.sendMessage("Now click on any block to make it a teleporter.");
				} else {
					player.sendMessage("You require a saved location. You can get one by typing /sb savelocation.");
				}
				break;
				
			case "controller":
				onCreateController.put(player, true);
				player.sendMessage("Now click on an iron block to make it a controller.");
				break;
				
			default: 
				return false;
			}
			break;
		
		case "edit":
			if(args.length < 2){
				return false;
			}
			
			switch(args[1].toLowerCase()){
				
			case "controller":
				onEditController.put(player, true);
				player.sendMessage("Now click on a controller to edit.");
				break;
				
			default: 
				return false;
			}
			
			break;
			
		case "adduser":
			if(args.length < 2){
				return false;
			}
			onAddUser.put(player, true);
			playerToAdd.put(player, args[1]);
			player.sendMessage("Now click on a SecretBlock to add this user");
			break;
			
		default:
			return false;
		}
		return true;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {	
			Player player = event.getPlayer();
			Block clickedBlock = event.getClickedBlock();
			
			if(onCreateTeleporter.containsKey(player) && onCreateTeleporter.get(player)){
				teleporter.add(new SecretTeleporter(clickedBlock, savedLocation.get(player), player));
				onCreateTeleporter.put(player, false);	
				try{
					SLAPI.save(teleporter, save_teleporter.getAbsolutePath());
		        }catch(Exception e){
		             e.printStackTrace();
		        }
				player.sendMessage("Teleporter was created successfully.");
				return;
			}
			
			if(onCreateController.containsKey(player) && onCreateController.get(player)){
				if(clickedBlock.getType() == Material.IRON_BLOCK){
					if(currentEditingBlock.containsKey(player)){
						if(currentEditingBlock.get(player).equals(clickedBlock)){
							if(controlledBlockList.containsKey(player)){
								controller.add(new SecretController(player, clickedBlock, controlledBlockList.get(player)));
								try{
									SLAPI.save(controller, save_controller.getAbsolutePath());
						        }catch(Exception e){
						             e.printStackTrace();
						        }
								player.sendMessage("Controller was created successfully. " + controlledBlockList.get(player).size() + " blocks were attached.");
							}
							else{
								player.sendMessage("No blocks were attached to the controller.");
							}
							onCreateController.put(player, false);
							currentEditingBlock.remove(player);
							controlledBlockList.remove(player);
						}
					}
					else{
						currentEditingBlock.put(player, clickedBlock);
						player.sendMessage("Started controller block. Place blocks and click the controller again, when you are finished.");
					}
					return;
				}
			}
			
			if(onEditController.containsKey(player) && onEditController.get(player)){
				for(int i=0; i<controller.size(); i++){
					if(controller.get(i).getIronBlock().equals(clickedBlock)){
						if(currentEditingBlock.containsKey(player)){
							if(currentEditingBlock.get(player).equals(clickedBlock)){
								if(controlledBlockList.containsKey(player)){
									controller.remove(i);
									controller.add(new SecretController(player, clickedBlock, controlledBlockList.get(player)));
									try{
										SLAPI.save(controller, save_controller.getAbsolutePath());
							        }catch(Exception e){
							             e.printStackTrace();
							        }
									player.sendMessage("Controller was edited successfully. " + controlledBlockList.get(player).size() + " blocks were attached.");
								}
								else{
									player.sendMessage("No blocks were attached to the controller.");
								}
								onEditController.put(player, false);
								currentEditingBlock.remove(player);
								controlledBlockList.remove(player);
							}
						}
						else{
							if(controller.get(i).getOwnerName().equals(player.getName())){
								currentEditingBlock.put(player, clickedBlock);
								controlledBlockList.put(player, controller.get(i).getControlledBlocks());
								player.sendMessage("Started editing controller block. Place blocks and click the controller again, when you are finished.");
								return;
							}
							ArrayList<String> user = controller.get(i).getUser();
							for(int k=0; k<user.size(); k++){
								if(user.get(k).equals(player.getName())){
									currentEditingBlock.put(player, clickedBlock);
									controlledBlockList.put(player, controller.get(i).getControlledBlocks());
									player.sendMessage("Started editing controller block. Place blocks and click the controller again, when you are finished.");
									return;
								}
							}
							player.sendMessage("You are not allowed to edit this controller. owner: "+controller.get(i).getOwnerName());
						}
						return;
					}
				}
			}
			
			
			if(onAddUser.containsKey(player) && onAddUser.get(player) && playerToAdd.containsKey(player)){
				for(int i=0; i<controller.size(); i++){
					if(controller.get(i).getIronBlock().equals(clickedBlock)){
						if(controller.get(i).getOwnerName().equals(player.getName())){
							controller.get(i).addUser(playerToAdd.get(player));
							player.sendMessage("You successfully added "+ playerToAdd.get(player) +" to this controller.");
							playerToAdd.remove(player);
							onAddUser.put(player, false);
						}
						else{
							player.sendMessage("This controller does not belong to you.");
						}
						return;
					}
				}
			}
			
			
			for(int i=0; i<teleporter.size(); i++){
				if(teleporter.get(i).getBlock().equals(clickedBlock)){
					Location destination = teleporter.get(i).getTeleportLocation();
					player.teleport(destination);
					player.getWorld().strikeLightningEffect(destination);
					return;
				}
			}
			
			
		}
	}

	@EventHandler
	public void OnPlaceBlock(BlockPlaceEvent event){
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if( (onCreateController.containsKey(player) && onCreateController.get(player))
				|| (onEditController.containsKey(player) && onEditController.get(player))
				&& currentEditingBlock.containsKey(player)){
			if(!controlledBlockList.containsKey(player)){
				controlledBlockList.put(player, new ArrayList<Block>());
			}
			controlledBlockList.get(player).add(block);
			player.sendMessage("Block[" + block.getTypeId() + "] added to controller");
		}
	}
	
	@EventHandler
	public void OnBreakBlock(BlockBreakEvent event){
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if( (onCreateController.containsKey(player) && onCreateController.get(player))
				|| (onEditController.containsKey(player) && onEditController.get(player))
				&& currentEditingBlock.containsKey(player)){
			if(controlledBlockList.containsKey(player)){
				for(int i=0; i<controlledBlockList.get(player).size(); i++){
					if(controlledBlockList.get(player).get(i).equals(block)){
						controlledBlockList.get(player).remove(i);
						player.sendMessage("Block[" + block.getTypeId() + "] removed from controller");
					}
				}
			}
		}
		
		for(int i=0; i<teleporter.size(); i++){
			if(teleporter.get(i).getBlock().equals(block)){
				Player owner = Bukkit.getServer().getPlayer(teleporter.get(i).getOwnerName());
				if(owner != null){
					owner.sendMessage("One of your teleporters has been destroyed.");
				}
				teleporter.remove(i);
				break;
			}
		}
		
		for(int i=0; i<controller.size(); i++){
			if(controller.get(i).getIronBlock().equals(block)){
				Player owner = Bukkit.getServer().getPlayer(controller.get(i).getOwnerName());
				if(owner != null){
					owner.sendMessage("One of your controllers has been destroyed.");
				}
				controller.remove(i);
				break;
			}
		}
		
		
	}
	
	@EventHandler
	public void OnBlockRedstone(BlockRedstoneEvent event){
		Block block_down = event.getBlock().getRelative(BlockFace.DOWN);
		
		if(block_down.getType() == Material.IRON_BLOCK){			
			for(int i=0; i<controller.size(); i++){
				if(controller.get(i).getIronBlock().equals(block_down)){
					if(event.getNewCurrent() >= 1 && event.getOldCurrent() <= 0){
						controller.get(i).vanishBlocks();
					}
					if(event.getNewCurrent() <= 0 && event.getOldCurrent() >= 1){
						controller.get(i).showBlocks();
					}
				}
			}
		}
		
	}

}
