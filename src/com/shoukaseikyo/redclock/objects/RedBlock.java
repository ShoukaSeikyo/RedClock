package com.shoukaseikyo.redclock.objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class RedBlock {

	private static enum ORI {
		GROUND, NORTH, SOUTH, EAST, WEST;
	}
	
	private String CREATOR;
	private long START_TICK;
	private ORI ORIENTATION;
	private long STOP_TICK;
	private boolean STATUS;
	private Location LOCATION;
	private boolean waitSign = true;
	private boolean waitTorch = true;
	
	public RedBlock(String CREATOR, long START_TICK, long STOP_TICK, boolean STATUS, Block BLOCK) {
		this.CREATOR = CREATOR;
		if(START_TICK < 0) START_TICK += 24000*(Math.floor(1+Math.abs(START_TICK/24000)));
		if(STOP_TICK < 0) STOP_TICK += 24000*(Math.floor(1+Math.abs(STOP_TICK/24000)));
		if(STOP_TICK > 24000) STOP_TICK -= 24000*(Math.floor(STOP_TICK/24000));
		if(START_TICK > 24000) START_TICK -= 24000*(Math.floor(START_TICK/24000));
		this.START_TICK = START_TICK;
		this.STOP_TICK = STOP_TICK;
		this.STATUS = STATUS;
		this.LOCATION = BLOCK.getLocation();
		if(BLOCK.getTypeId() == 63) {
			ORIENTATION = ORI.GROUND;
		} else {
			byte damage = BLOCK.getData();
			if(damage == 3)
				ORIENTATION = ORI.SOUTH;
			if(damage == 5)
				ORIENTATION = ORI.EAST;
			if(damage == 4)
				ORIENTATION = ORI.WEST;
			if(damage == 2)
				ORIENTATION = ORI.NORTH;
		}
		
		updateBlock();
	}
	public RedBlock(String LOAD) {
		String[] args = LOAD.split("\\|");
			if(args.length != 5 && args.length != 6)
				return;
			START_TICK = Long.valueOf(args[0]);
			STOP_TICK = Long.valueOf(args[1]);
			STATUS = Boolean.valueOf(args[2]);
			ORIENTATION = ORI.valueOf(args[3]);
			String[] w_args = args[4].split("/");
				if(w_args.length != 4)
					return;
				World W = Bukkit.getServer().getWorld(w_args[0]);
				double X = Double.valueOf(w_args[1]);
				double Y = Double.valueOf(w_args[2]);
				double Z = Double.valueOf(w_args[3]);
			LOCATION = new Location(W, X, Y, Z);
			CREATOR = (args.length == 5)?"CreeperPlayerPublic":String.valueOf(args[5]);
			updateBlock();
	}
	
	public void updateBlock() {
		World WORLD = LOCATION.getWorld();
		long TICK = WORLD.getTime();
			Block BLOCK = WORLD.getBlockAt(LOCATION);
		if(isInTime(TICK)) {
			if(STATUS) {
				torch(BLOCK);
			} else {
				sign(BLOCK);
			}
		} else {
			if(!STATUS) {
				torch(BLOCK);
			} else {
				sign(BLOCK);
			}
		}
	}
	
	public void torch(Block BLOCK) {
		
		if(waitTorch) {
			BLOCK.setTypeId(0);
			waitTorch = false;
		} else {
			if(BLOCK.getTypeId() == 75|| BLOCK.getTypeId() == 76)
				return;
			int id = (BLOCK.getTypeId() == 75) ? 75 : 76;
			byte b = 0;
			switch(ORIENTATION) {
				case GROUND:
					b = ((byte)5);
					break;
				case NORTH:
					b = ((byte)4);
					break;
				case SOUTH:
					b = ((byte)3);
					break;
				case EAST:
					b = ((byte)1);
					break;
				case WEST:
					b = ((byte)2);
					break;
			}
			BLOCK.setTypeIdAndData(id, b, true);
		}
		waitSign = true;
	}
	
	public void sign(Block BLOCK) {
		
		if(waitSign) {
			BLOCK.setTypeId(0);
			waitSign = false;
		} else {
			if(BLOCK.getTypeId() == 63|| BLOCK.getTypeId() == 68)
				return;
			if(ORIENTATION == ORI.GROUND) BLOCK.setTypeId(63);
			else BLOCK.setTypeId(68);
		
			switch(ORIENTATION) {
				case NORTH:
					BLOCK.setData((byte)2);
					break;
				case SOUTH:
					BLOCK.setData((byte)3);
					break;
				case EAST:
					BLOCK.setData((byte)5);
					break;
				case WEST:
					BLOCK.setData((byte)4);
					break;
			}
		
			Sign sign = (Sign)BLOCK.getState();
			sign.setLine(0, "RedBlock");
			sign.setLine(1, "From " + ChatColor.YELLOW + START_TICK);
			sign.setLine(2, "To " + ChatColor.YELLOW + STOP_TICK);
			sign.setLine(3, (STATUS) ? "Will be" + ChatColor.GREEN + " ON" : "Will be"  + ChatColor.RED +  " OFF");
			sign.update();
		}
		waitTorch = true;
	}
	public boolean isInTime(long TICK) {
		if(START_TICK < STOP_TICK && TICK >= START_TICK && TICK < STOP_TICK)
			return true;
		if(START_TICK > STOP_TICK && !(TICK >= STOP_TICK && TICK < START_TICK) )
			return true;
		return false;
	}
	public String getCreator() {
		return CREATOR;
	}
	public boolean canDestroy(Player player) {
		if((player.getName().equals(CREATOR) || CREATOR.equals("CreeperPlayerPublic")) || (player.hasPermission("redclock.admin") || player.isOp()))
			return true;
		return false;
	}
	public Location getLocation() {
		return LOCATION;
	}
	public Block getBlock() {
		return getLocation().getWorld().getBlockAt(getLocation());
	}
	public String toString() {
		String LOCATION = this.LOCATION.getWorld().getName()+"/"+this.LOCATION.getX()+"/"+this.LOCATION.getY()+"/"+this.LOCATION.getZ();
		return START_TICK + "|" + STOP_TICK + "|" + STATUS + "|" + ORIENTATION + "|" + LOCATION + "|" + CREATOR;
	}
}
