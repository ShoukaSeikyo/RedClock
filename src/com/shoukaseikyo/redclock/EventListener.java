package com.shoukaseikyo.redclock;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import com.shoukaseikyo.redclock.objects.RedBlock;

public class EventListener implements Listener {

	Main main;
	
	/*
	 * Constructor
	 * @param minenion : instance of Main
	 */
	public EventListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void SignPlaced(SignChangeEvent e) {
		if(main.permissions == false || e.getPlayer().hasPermission("redclock.use")) {
			if(!(e.getLine(0).equalsIgnoreCase("redblock")) && !(e.getLine(0).equalsIgnoreCase("redclock")) || e.getLines().length < 3)
				return;
			if(!(e.getLine(1).equalsIgnoreCase("on")) && !(e.getLine(1).equalsIgnoreCase("off")) && !(e.getLine(1).equalsIgnoreCase("pulse")))
				return;
			String STYPE = e.getLine(1).toUpperCase();
			long START = timeParser(e.getLine(2));
			long STOP = timeParser(e.getLine(3));
			main.BLOCKS.add(new RedBlock(e.getPlayer().getName(),START, STOP, STYPE, e.getBlock()));
		}
	}
	
	@EventHandler
	public void Interact(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if((p.hasPermission("redclock.admin") || p.isOp()) && e.getAction() == Action.RIGHT_CLICK_BLOCK && (e.getClickedBlock().getTypeId() == 68 || e.getClickedBlock().getTypeId() == 63)) {
			Location LOCATION = e.getClickedBlock().getLocation();
			for(RedBlock bl : main.BLOCKS) {
				if(bl.getLocation().equals(LOCATION)) {
					p.sendMessage("This redblock have been created by " + ChatColor.AQUA + bl.getCreator());
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void Physic(BlockPhysicsEvent e) {
		Location LOCATION = e.getBlock().getLocation();
		for(RedBlock bl : main.BLOCKS) {
			if(bl.getLocation().equals(LOCATION)) {
					int id = getAttachedBlock(e.getBlock()).getTypeId();
					int[] INTS = {0, 18, 20, 29, 30, 33, 44, 46, 53, 60, 63, 67, 68, 79, 85, 86, 89, 91, 92, 102, 103, 108, 109, 113, 114, 118, 120, 123, 124, 125, 126, 127, 128};
					if(cInt(id, INTS)) {
						e.setCancelled(true);
						return;
					}
			}
		}
	}
	
	@EventHandler
	public void TorchDestoyed(BlockBreakEvent e) {
		int id = e.getBlock().getTypeId();
		if(id == 75 || id == 76 || id == 68 || id == 63) {
			for(RedBlock bl : main.BLOCKS) {
				if(e.getBlock().getLocation().equals(bl.getLocation())) {
					if(main.permissions == true && !(bl.canDestroy(e.getPlayer()))) {
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.RED + "You don't have the permission to destroy this block.");
						return;
					}
					e.getPlayer().sendMessage("You have destoyed a RedBlock.");
					main.BLOCKS.remove(bl);
					return;
				}
			}
		} else {
			for(RedBlock bl : main.BLOCKS) {
				if(e.getBlock().getLocation().equals(getAttachedBlock(bl.getBlock()).getLocation())) {
					e.getPlayer().sendMessage("You need to destroy the RedBlock first.");
					e.setCancelled(true);
				}
			}
		}
	}
	
	public boolean cInt(int INT, int[] INTS) {
		for(int i : INTS) {
			if(INT == i) {
				return true;
			}
		}
		return false;
	}
	public long timeParser(String input) {
		if(!isnumeric(input)) {
			input = input.toUpperCase();
			if(input.contains("PM")) {
				int hour = Integer.valueOf(input.split("PM")[0])-6;
				hour = (hour < 0) ? 12000-Math.abs(hour*1000): hour*1000+12000;
				return Long.valueOf(hour);
			} else if(input.contains("AM")) {
				int hour = Integer.valueOf(input.split("AM")[0])-6;
				hour = (hour < 0) ? 24000-Math.abs(hour*1000): hour*1000;
				return Long.valueOf(hour);
			} else if(input.contains(":")) {
				int hour = Integer.valueOf(input.split(":")[0])-6;
				hour = (hour < 0) ? 24000-Math.abs(hour*1000): hour*1000;
				int minute = Integer.valueOf(input.split(":")[1])*10;
				return Long.valueOf(hour + ((minute <= 60)?minute:0));
			}
			return 0;
		} else
			return Long.valueOf(input);
	}
	
	public static Block getAttachedBlock(Block b) {
        MaterialData m = b.getState().getData();
        BlockFace face = BlockFace.DOWN;
        if (m instanceof Attachable)
            face = ((Attachable) m).getAttachedFace();
        return b.getRelative(face);
    }
	public static boolean isnumeric(String input) {  
	   try {  
	      Integer.parseInt(input);  
	      return true;  
	   } catch(Exception e) {  
	      return false;  
	   }  
	}  
}
