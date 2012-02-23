package com.rlarocca.WiredWalls;
/*
 * WiredWalls by Robert La Rocca
 * 
 * Bukkit plugin to make redstone act like it goes up and down tall walls
 */
import java.io.File;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.material.Door;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WiredWalls extends JavaPlugin implements Listener{
	
	Logger log = Logger.getLogger("Minecraft");
	FileConfiguration config;
	
	public void onEnable(){
		log.info("WiredWalls is Running!");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents((Listener) this, this);
		try{
			config = getConfig();
			File filePath = new File(getDataFolder(),"config.yml");
			filePath.mkdir();
			if(!config.contains("stopper")){
				config.set("stopper", 49);
			}
			saveConfig();
			/*
			 * Stopper is the id of the block that will stop the charge. Any block above or below the stopper block
			 * will not be affected by this. 
			 */
		}
		catch(Exception e1){
			e1.printStackTrace();
		}
	}
	
	@EventHandler
	public void OnBlockCharged(BlockRedstoneEvent event){
		Block theBlock = event.getBlock();
		World world = theBlock.getWorld();
		//Get the current to tell if the redstone was turned on or off
		int current = event.getNewCurrent(); 
		
		//This will look for any blocks we want to affect on the events y axis
		chargeDown(theBlock.getX(),theBlock.getY(),theBlock.getZ(), world, current); 
		
		//What y axes are we going to have affect
		boolean[] neighbors = GetNeighbors(theBlock.getX(),theBlock.getY(),theBlock.getZ(), world);
		
		
		if(neighbors[0]){
			// Forward on the Z axis
			chargeUp(theBlock.getX(),theBlock.getY()+1,theBlock.getZ()+1, world, current);
			chargeDown(theBlock.getX(),theBlock.getY()+1,theBlock.getZ()+1, world, current);
		}
		if(neighbors[1]){
			//Backward on the Z axis
			chargeUp(theBlock.getX(),theBlock.getY()+1,theBlock.getZ()-1, world, current);
			chargeDown(theBlock.getX(),theBlock.getY()+1,theBlock.getZ()-1, world, current);
		}
		if(neighbors[2]){
			//left on the X axis
			chargeUp(theBlock.getX()-1,theBlock.getY()+1,theBlock.getZ(), world, current);
			chargeDown(theBlock.getX()-1,theBlock.getY()+1,theBlock.getZ(), world, current);
		}
		if(neighbors[3]){
			//right on the X axis
			chargeUp(theBlock.getX()+1,theBlock.getY()+1,theBlock.getZ(), world, current);
			chargeDown(theBlock.getX()+1,theBlock.getY()+1,theBlock.getZ(), world, current);
		}
	}
	
	public boolean[] GetNeighbors(int x, int y, int z, World world){
		boolean[] blocks = new boolean[4];
		//Get upfoward block
		blocks[0] = (!world.getBlockAt(x,y,z+1).isEmpty() && world.getBlockAt(x,y+1,z+1).getTypeId() != 55);
		if(blocks[0]){
		}
		//Get upbehind block
		blocks[1] = (!world.getBlockAt(x,y,z-1).isEmpty() && world.getBlockAt(x,y+1,z-1).getTypeId() != 55);
		if(blocks[1]){
		}
		//Get upleft block
		blocks[2] = (!world.getBlockAt(x-1,y,z).isEmpty() && world.getBlockAt(x-1,y+1,z).getTypeId() != 55);
		if(blocks[2]){
		}
		//Get upright block
		blocks[3] = (!world.getBlockAt(x+1,y,z).isEmpty() && world.getBlockAt(x+1,y+1,z).getTypeId() != 55);
		if(blocks[3]){
		}
		return blocks;
	}
	
	public void chargeUp(int x, int y, int z, World world, int current){
		boolean isBlock = true; //if true, the block we are currently on is not a stopper (air, bedrock, and the one designated in the config)
		int xx = x;
		int yy = y;
		int zz = z;
		
		while(isBlock){
			yy++;
			Block block = world.getBlockAt(xx, yy, zz);
			BlockState bs = block.getState();
			int stopper = config.getInt("stopper");
				
				//if it is a door, open or close it
				if(block.getTypeId() == 64 || block.getTypeId() == 71){
					Door door = (Door)bs.getData();
					if(door.isOpen()){
						door.setOpen(false);
					}
					else
					{
						door.setOpen(true);
					}
					bs.setData(door);
					bs.update();
				}
				/*if it is a dispenser, shoot it. Current has to be great than zero so it only shoots when 
				 * redstone gets activated and not deactivated
				 */
				if(block.getTypeId() == 23 && current > 0){
					Dispenser dispencer = (Dispenser)bs;
					dispencer.dispense();
				}
				if(block.getTypeId() == stopper){
					isBlock = false;
				}
			if(world.getBlockAt(xx, yy, zz).isEmpty()){
				isBlock = false;
			}
		}
	}
		
		public void chargeDown(int x, int y, int z, World world, int current){
			boolean isBlock = true;
			int xx = x;
			int yy = y;
			int zz = z;
			
			while(isBlock){
				yy--;
				Block block = world.getBlockAt(xx, yy, zz);
				BlockState bs = block.getState();
				int stopper = config.getInt("stopper");
					if(block.getTypeId() == 64 || block.getTypeId() == 71){
						Door door = (Door)bs.getData();
						if(door.isOpen() && current == 0){
							door.setOpen(false);
							log.info("closed");
						}
						else
						{
							door.setOpen(true);
							log.info("open");
						}
						bs.setData(door);
						bs.update();
					}
					if(block.getTypeId() == stopper){
						isBlock = false;
					}
					if(block.getTypeId() == 07){
						isBlock = false;
					}
					if(block.getTypeId() == 23 && current > 0){
						Dispenser dispencer = (Dispenser)bs;
						dispencer.dispense();
					}
							
				if(world.getBlockAt(xx, yy, zz).isEmpty()){
					isBlock = false;
				}
			}
		}
	}
	
