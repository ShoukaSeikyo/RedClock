package com.shoukaseikyo.redclock;

import com.shoukaseikyo.redclock.objects.RedBlock;

public class Thread implements Runnable {
	
	Main main;
	
	/*
	 * Constructor
	 * @param minenion : instance of Main
	 */
	public Thread(Main main) {
		this.main = main;
	}
	
	/*
	 * Processing thread to update the redblocks
	 */
	public void run() {
		for(RedBlock bl : main.BLOCKS)
			bl.updateBlock();
	}
}
