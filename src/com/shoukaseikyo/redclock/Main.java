package com.shoukaseikyo.redclock;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.shoukaseikyo.redclock.objects.RedBlock;

public class Main extends JavaPlugin {
	
	public Logger log = Logger.getLogger("Minecraft");
	public ArrayList<RedBlock> BLOCKS = new ArrayList<RedBlock>();
	public boolean permissions = false;
	public int refresh = 40;
	public int save = 200;
	public String version = "1.0.8";
	File file;
	
	/*
	 * Enabling of the plugin
	 */
	public void onEnable() {
		getConfiguration();
		load();
		setEventsandSchedulers();
		checkVersion();
	}
	/*
	 * Disabling of the plugin
	 */
	public void onDisable() {
		save();
	}
	
	public void setEventsandSchedulers() {
		EventListener EventListener = new EventListener(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(EventListener, this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Thread(this), refresh, refresh);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new SaveThread(this), save, save);
	}
	
	public void checkVersion() {
		if(!(getOnlineVersion().equalsIgnoreCase(version))) {
			log.info("A newer version of RedClock is available. Consider update.");
		}
	}
	
	/*
	 * Load all the redblocks from the blocks.txt file.
	 */
	public void load() {
		file = (new File(this.getDataFolder(), "blocks.txt").exists())? new File(this.getDataFolder(), "blocks.txt"): new File(this.getDataFolder(), "blocks.dat");
		boolean old = (new File(this.getDataFolder(), "blocks.txt").exists()) ? true : false;
		try { file.createNewFile(); } catch (IOException e) {}
		
		try {
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int i = 0;
			while ((strLine = br.readLine()) != null) {
				String line = (old) ? strLine : CR.d(strLine);
				if(line != "" || line.split("\\|").length != 6) {
					RedBlock b = new RedBlock(line);
						BLOCKS.add(b);
						i++;
				}
			}
			in.close();
			log.info("[RedBlock] Loading " + i + " RedBlocks ...");
		} catch (FileNotFoundException e) {
			log.info("[RedBlock] Errors while loading the RedBlocks, the plugin will be disable");
			this.setEnabled(false);
		} catch (IOException e) {
			log.info("[RedBlock] Errors while loading the RedBlocks, the plugin will be disable");
			this.setEnabled(false);
		}
		
		if(old)
			file.delete();
	}
	
	/*
	 * Save all the redblocks to the blocks.txt file.
	 */
	public void save() {
		file.delete();
		file = new File(this.getDataFolder(), "blocks.dat");
        BufferedWriter bwriter = null;
        FileWriter fwriter = null;
        try {
	    	for(RedBlock bl : BLOCKS) {
	            file.createNewFile();
	            fwriter = new FileWriter(file, true);
	            bwriter = new BufferedWriter(fwriter);
	            bwriter.write(CR.c(bl.toString()));
	            bwriter.newLine();
	            bwriter.close();
	    	}
        } catch (IOException e) {}
	}
	
	public void getConfiguration() {
		if(this.getConfig().getString("permissions") == null) {
			this.getConfig().set("permissions", false);
			this.saveConfig();
		} else 
			this.permissions = this.getConfig().getBoolean("permissions");
		
		if(this.getConfig().getString("refresh") == null) {
			this.getConfig().set("refresh", 40);
			this.saveConfig();
		} else {
			this.refresh = this.getConfig().getInt("refresh");
			refresh = (refresh > 0)? refresh : 40;
		}
		
		if(this.getConfig().getString("save") == null) {
			this.getConfig().set("save", 200);
			this.saveConfig();
		} else {
			this.save = this.getConfig().getInt("save");
			save = (save > 0)? save : 200;
		}
	}
	
	public Object getConfig(String s, Object Default) {
		if(this.getConfig().getString(s) == null) {
			this.getConfig().set(s, Default);
			this.saveConfig();
			return Default;
		} else {
			return this.getConfig().get(s);
		}
	}
	
	public String getOnlineVersion() {
		try
	    {
	      URL WhiteListURL = new URL("http://shoukylife.fr.cr/plugins/redclock/version.txt");
	      Scanner in = new Scanner(WhiteListURL.openStream());
	      String line = in.nextLine();
	      in.close();
	      return line;
	    } catch (Exception e) {
	    }
		
		return version;
	}
}
