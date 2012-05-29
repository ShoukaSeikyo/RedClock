package com.shoukaseikyo.redclock;

public class SaveThread implements Runnable {

	Main main;
	
	/*
	 * Constructor
	 * @param minenion : instance of Main
	 */
	public SaveThread(Main main) {
		this.main = main;
	}
	
	/*
	 * Processing thread of to save each 10 seconds
	 */
	public void run() {
		main.save();
	}
}
