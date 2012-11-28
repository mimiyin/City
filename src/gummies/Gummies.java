/**
 * Simple Template for the Big Screens Class, Fall 2012
 * <https://github.com/shiffman/Most-Pixels-Ever>
 * 
 * Note this project uses Processing 2.0b3
 */

package gummies;

import java.util.ArrayList;


import mpe.client.*;

import processing.core.*;
import processing.opengl.*;


public class Gummies extends PApplet {
	
	// Set it to 1 for actual size, 0.5 for half size, etc.
	// This is useful for testing MPE locally and scaling it down to fit to your screen
	public static float scale = .125f;

	// if this is true, it will use the MPE library, otherwise just run stand-alone
	public static boolean MPE = false;
	public static boolean local = true;

	// Client ID
	// Should be adjusted only for "local" testing
	//--------------------------------------
	int ID = 0;
	
	TCPClient client;

	// These we'll use for master width and height instead of Processing's built-in variables
	public static int mWidth;
	public static int mHeight;

	Stage stage;
	
	


	//--------------------------------------
	static public void main(String args[]) {
		// Windowed
		if (local) {
			PApplet.main(new String[] {"gummies.Gummies" });
		// FullScreen Exclusive Mode
		} else {
			PApplet.main(new String[] {"--present", "gummies.Gummies" });
		}
	}

	//--------------------------------------
	public void setup() {

		// If we are using the library set everything up
		if (MPE) {
			// make a new Client using an INI file
			// sketchPath() is used so that the INI file is local to the sketch
			String path = "mpefiles/";
			if (local) {
				path += "local/mpe" + ID + ".ini";
			} else {
				ID = IDGetter.getID();
				path += "6screens/mpe" + ID + ".ini";
			}
			client = new TCPClient(path, this);
			// Not rendering with OPENGL for local testing
			if (local) {
				size((int)(client.getLWidth()*scale), (int)(client.getLHeight()*scale), P2D);
				client.setLocalDimensions((int)(ID*client.getLWidth()*scale), 0, (int)(client.getLWidth()*scale), (int)(client.getLHeight()*scale));
			} else {
				size(client.getLWidth(), client.getLHeight(), P2D);
			}
			
			
			// the size is determined by the client's local width and height
			mWidth = client.getMWidth();
			mHeight = client.getMHeight();
			
		} else {
			// Otherwise with no library, force size
			size(parseInt(11520*scale),parseInt(1080*scale), P2D);
			mWidth = 11520;
			mHeight = 1080;
		}

		// the random seed must be identical for all clients
		randomSeed(0);
		noiseSeed(0);
		
		smooth();
		frameRate(30);
		background(255);
		
		stage = new Stage(this);

		if (MPE) {
			// IMPORTANT, YOU MUST START THE CLIENT!
			client.start();
		}
	}

	//--------------------------------------
	// Keep the motor running... draw() needs to be added in auto mode, even if
	// it is empty to keep things rolling.
	public void draw() {

		// If we are on the 6 screens we want to preset the frame's location
		if (MPE && local) {
			frame.setLocation(ID*width,0);
		}

		// If we're not using the library frameEvent() will not be called automatically
		if (!MPE) {
			frameEvent(null);
		}
	}

	//--------------------------------------
	// Triggered by the client whenever a new frame should be rendered.
	// All synchronized drawing should be done here when in auto mode.
	public void frameEvent(TCPClient c) {

		// Receiving a message for background color
		if (MPE && c.messageAvailable()) {
			String[] msg = c.getDataMessage();
		}

		// clear the screen
		if (!MPE || local) {
			scale(scale);
		}
				
		stage.run();
	}


}


