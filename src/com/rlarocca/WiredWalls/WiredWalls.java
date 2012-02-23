package com.rlarocca.WiredWalls;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
		}
		catch(Exception e1){
			e1.printStackTrace();
		}
	}
	
	@EventHandler
	public void OnBlockCharged(BlockRedstoneEvent event){
		Block theBlock = event.getBlock();
		World world = theBlock.getWorld();
		int current = event.getNewCurrent();
		switchDoorDown(theBlock.getX(),theBlock.getY(),theBlock.getZ(), world, current);
		boolean[] neighbors = GetNeighbors(theBlock.getX(),theBlock.getY(),theBlock.getZ(), world);
		if(neighbors[0]){
			switchDoorUp(theBlock.getX(),theBlock.getY()+1,theBlock.getZ()+1, world, current);
			switchDoorDown(theBlock.getX(),theBlock.getY(),theBlock.getZ()+1, world, current);
		}
		if(neighbors[1]){
			switchDoorUp(theBlock.getX(),theBlock.getY()+1,theBlock.getZ()-1, world, current);
			switchDoorDown(theBlock.getX(),theBlock.getY(),theBlock.getZ()-1, world, current);
		}
		if(neighbors[2]){
			switchDoorUp(theBlock.getX()-1,theBlock.getY()+1,theBlock.getZ(), world, current);
			switchDoorDown(theBlock.getX()-1,theBlock.getY(),theBlock.getZ(), world, current);
		}
		if(neighbors[3]){
			switchDoorUp(theBlock.getX()+1,theBlock.getY()+1,theBlock.getZ(), world, current);
			switchDoorDown(theBlock.getX()+1,theBlock.getY(),theBlock.getZ(), world, current);
		}
	}
	
	public boolean[] GetNeighbors(int x, int y, int z, World world){
		boolean[] blocks = new boolean[4];
		//Get upfoward block
		blocks[0] = (!world.getBlockAt(x,y,z+1).isEmpty() && world.getBlockAt(x,y,z+1).getTypeId() != 55);
		if(blocks[0]){
			//log.info(""+world.getBlockAt(x,y,z+1).getTypeId());
		}
		//Get upbehind block
		blocks[1] = (!world.getBlockAt(x,y,z-1).isEmpty() && world.getBlockAt(x,y,z-1).getTypeId() != 55);
		if(blocks[1]){
			//log.info(""+world.getBlockAt(x,y,z-1).getTypeId());
		}
		//Get upleft block
		blocks[2] = (!world.getBlockAt(x-1,y,z).isEmpty() && world.getBlockAt(x-1,y,z).getTypeId() != 55);
		if(blocks[2]){
			//log.info(""+world.getBlockAt(x-1,y,z).getTypeId());
		}
		//Get upright block
		blocks[3] = (!world.getBlockAt(x+1,y,z).isEmpty() && world.getBlockAt(x+1,y,z).getTypeId() != 55);
		if(blocks[3]){
			//log.info(""+world.getBlockAt(x+1,y,z).getTypeId());
		}
		return blocks;
	}
	
	public void switchDoorUp(int x, int y, int z, World world, int current){
		boolean isBlock = true;
		int xx = x;
		int yy = y;
		int zz = z;
		
		while(isBlock){
			yy++;
			Block block = world.getBlockAt(xx, yy, zz);
			BlockState bs = block.getState();
			int stopper = config.getInt("stopper");
			if(!block.isBlockPowered()){
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
				if(block.getTypeId() == stopper){
					isBlock = false;
				}
			}
			if(world.getBlockAt(xx, yy, zz).isEmpty()){
				isBlock = false;
			}
		}
	}
		
		public void switchDoorDown(int x, int y, int z, World world, int current){
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
				if(world.getBlockAt(xx, yy, zz).isEmpty()){
					/*if(world.getBlockAt(xx, yy, zz).getTypeId() == 64 || world.getBlockAt(xx, yy, zz).getTypeId() == 71){
						
					}*/
					isBlock = false;
				}
			}
		}
	}
	
