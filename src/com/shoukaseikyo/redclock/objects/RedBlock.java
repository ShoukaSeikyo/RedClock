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
	
	private static enum TYPE {
		ON, OFF, PULSE;
	}
	
	private String CREATOR;
	
	private long START_TICK;
	private long STOP_TICK;
	
	private TYPE SIGN_TYPE;
	//private boolean STATUS;
	private boolean waitSign = true;
	private boolean waitTorch = true;
	private String SLOCATION;
	private Location LOCATION;
	private ORI ORIENTATION;
	
	/* Constants */
	private TYPE ON = TYPE.ON;
	private TYPE OFF = TYPE.OFF;
	private TYPE PULSE = TYPE.PULSE;
	private boolean correct = true;
	
	public RedBlock(String CREATOR, long START_TICK, long STOP_TICK,  String STYPE, Block BLOCK) {
		this.CREATOR = CREATOR;
		if(START_TICK < 0) START_TICK += 24000*(Math.floor(1+Math.abs(START_TICK/24000)));
		if(STOP_TICK < 0) STOP_TICK += 24000*(Math.floor(1+Math.abs(STOP_TICK/24000)));
		if(STOP_TICK > 24000) STOP_TICK -= 24000*(Math.floor(STOP_TICK/24000));
		if(START_TICK > 24000) START_TICK -= 24000*(Math.floor(START_TICK/24000));
		this.START_TICK = START_TICK;
		this.STOP_TICK = STOP_TICK;
		this.SIGN_TYPE = TYPE.valueOf(STYPE);
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
			String STYPE = args[2];
			if(STYPE.equalsIgnoreCase("true")) STYPE = "ON";
			if(STYPE.equalsIgnoreCase("false")) STYPE = "OFF";
			SIGN_TYPE = TYPE.valueOf(STYPE);
			ORIENTATION = ORI.valueOf(args[3]);
			SLOCATION = args[4];
			String[] w_args = args[4].split("/");
				if(w_args.length != 4)
					return;
				World W = Bukkit.getServer().getWorld(w_args[0]);
				double X = Double.valueOf(w_args[1]);
				double Y = Double.valueOf(w_args[2]);
				double Z = Double.valueOf(w_args[3]);
			LOCATION = new Location(W, X, Y, Z);
			CREATOR = (args.length == 5)?"CreeperPlayerPublic":String.valueOf(args[5]);
			if(getWorld() == null) correct = false;
	}
	
	public void updateBlock() {
		if(!correct) {
			String[] w_args = SLOCATION.split("/");
			if(w_args.length != 4)
				return;
			World W = Bukkit.getServer().getWorld(w_args[0]);
			double X = Double.valueOf(w_args[1]);
			double Y = Double.valueOf(w_args[2]);
			double Z = Double.valueOf(w_args[3]);
			LOCATION = new Location(W, X, Y, Z);
			correct = true;
			
		}
		World WORLD = LOCATION.getWorld();
		long TICK = WORLD.getTime();
		if(SIGN_TYPE == PULSE) {
			if(isTime(TICK)) {
				toTorch();
			} else {
				toSign();
			}
		} else {
			if(isInTime(TICK)) {
				if(SIGN_TYPE == ON) {
					toTorch();
				} else {
					toSign();
				}
			} else {
				if(SIGN_TYPE == OFF) {
					toTorch();
				} else {
					toSign();
				}
			}
		}
	}
	
	public boolean isInTime(long TICK) {
		return ((START_TICK < STOP_TICK && TICK >= START_TICK && TICK < STOP_TICK) || (START_TICK > STOP_TICK && !(TICK >= STOP_TICK && TICK < START_TICK)))? true:false;
	}
	public boolean isTime(long TICK) {
		return (START_TICK >= TICK && START_TICK <= TICK + 20)? true:false;
	}
	public boolean canDestroy(Player player) {
		return (player.getName().equals(CREATOR) || CREATOR.equals("CreeperPlayerPublic")) || (player.hasPermission("redclock.admin") || player.isOp()) ? true:false;
	}

	/* SECTION II : CHANGE THE BLOCK
	 * 
	 *  toTorch : change the BLOCK at the location of this object to a REDSTONE_TORCH (OFF = 75, ON = 76)
	 *  toSign : change the BLOCK at the location of this object to a SIGN (WALL = 68, GROUND = 63)
	 */
	
	public void toTorch() {
		Block BLOCK = getBlock();
		BLOCK.getChunk().load();
		
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
	public void toSign() {
		Block BLOCK = getBlock();
		BLOCK.getChunk().load();
		
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
			String Line1 = (SIGN_TYPE == PULSE)?"At ":"From ";
			sign.setLine(1, Line1 + ChatColor.YELLOW + START_TICK);
			String Line2 = (SIGN_TYPE == PULSE)?"":"To " + ChatColor.YELLOW + STOP_TICK;
			sign.setLine(2, Line2);
			String be = (SIGN_TYPE == ON || SIGN_TYPE == OFF )?"be ":"";
			sign.setLine(3, "Will " + be + ChatColor.GREEN + SIGN_TYPE.toString());
			sign.update();
		}
		waitTorch = true;
	}
	
	/* SECTION III : GET VARIABLES
	 * 
	 * getCreator() : return a STRING from the CREATOR variable
	 * getLocation() : return a LOCATION from the LOCATION variable
	 * getBlock() : return a BLOCK from the getBlock() of the LOCATION variable
	 * toString() : convert this object to a custom STRING for saving
	 * */
	
	public String getCreator() {
		return CREATOR;
	}
	public World getWorld() {
		return LOCATION.getWorld();
	}
	public Location getLocation() {
		return LOCATION;
	}
	public Block getBlock() {
		return getLocation().getBlock();
	}
	public String toString() {
		return START_TICK + "|" + STOP_TICK + "|" + SIGN_TYPE.toString() + "|" + ORIENTATION + "|" + this.LOCATION.getWorld().getName()+"/"+this.LOCATION.getX()+"/"+this.LOCATION.getY()+"/"+this.LOCATION.getZ() + "|" + CREATOR;
	}
}
