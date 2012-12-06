package gummies;

import java.awt.Color;
import java.util.ArrayList;

import org.jbox2d.common.Vec2;

import pbox2d.*;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.core.PShape;
import processing.core.PFont;

public class Stage {
	PApplet parent;

	// Settings file
	Settings settings;

	// Background
	PImage skyline;
	int skyWidth;
	int skyHeight;
	boolean multiLineTitle;

	// Credits
	Sign centerSign;

	// /////////////////////////////////////////////////////
	// ////////////////////////WIND/////////////////////////
	// /////////////////////////////////////////////////////

	ArrayList<Spring> springs = new ArrayList<Spring>();
	ArrayList<Bear> bears = new ArrayList<Bear>();

	// Tchochkes
	PImage[] gummyImgs = new PImage[3];
	PImage gummyMask;

	// Define altitude and light source for shadows
	public static float alt = Gummies.mHeight * 2;
	public static PVector source = new PVector(Gummies.mWidth / 2, -alt);

	// Used for generating noise across a number of wind classes
	public static int t;

	// /////////////////////////////////////////////////////
	// ///////////////////////BOX2D/////////////////////////
	// /////////////////////////////////////////////////////

	// A reference to our box2d world
	PBox2D box2d;

	// Create water line
	Water water;

	Stage(PApplet parent_) {
		parent = parent_;
		t = PApplet.parseInt(parent.random(1000));

		// Load settings file
		loadSettings();

		// Create wind
		gummyMask = parent.loadImage("gummy_mask.jpg");

		for (int i = 0; i < gummyImgs.length; i++) {
			gummyImgs[i] = parent.loadImage("gummy_" + i + ".jpg");
			gummyImgs[i].mask(gummyMask);
		}

		// Initialize box2d physics and create the world
		box2d = new PBox2D(parent);
		box2d.createWorld();

		// Create the water
		water = new Water(parent, box2d, settings);

		// Create skyline
		initSkyline();

		Vec2 centerSignPos = new Vec2(Gummies.mWidth / 2, Gummies.mHeight / 2);
		float tilt = PApplet.PI / 36;
		float titleYOffset = 0;
		float namesYOffset = 160;
		float titleTextSize = 256;
		float namesTextSize = 128;
		PFont font = parent.createFont("data/AuXDotBitC.ttf", 540);
		float res = 50;
		float margin = 100;
		float signHeight = 500;
		centerSign = new Sign(parent, box2d, settings, centerSignPos, tilt,
				titleYOffset, namesYOffset, titleTextSize, namesTextSize,
				multiLineTitle, font, res, margin, signHeight);

	}

	void run() {

		// Draw the skyline
		parent.image(skyline, -300, -Gummies.mHeight / 2, skyWidth, skyHeight);

		// Blow the wind
		launchGummies();

		// Constantly change size, rotation, opacity and strength of springs for
		// each bear
		for (int i = 0; i < bears.size(); i++) {
			Bear thisBear = bears.get(i);
			thisBear.run();
			// If bear reach right side of window, kill it.
			if (thisBear.die())
				bears.remove(thisBear);
		}

		// We must always step through time!
		box2d.step();

		// Display credits
		centerSign.display();

		// Display water
		water.display();
		water.update();
	}

	void launchGummies() {

		// Create new springs and bears at a controlled rate
		float toss = PApplet.parseInt(parent.random(100));
		if (toss < settings.launchRate) {
			// if(bears.size() == 0) {
			// When launching new bears...
			// Choose a color gummy at random
			bears.add(new Bear(parent, gummyImgs[PApplet.parseInt(parent
					.random(0, gummyImgs.length))], (parent.noise(t
					+ parent.random(100)) * 100), box2d));

			t += parent.random(-1, 5);
		}
	}

	void loadSettings() {
		String[] data = parent.loadStrings("settings.txt");
		String delim = ": ";
		String[] title = data[0].split(delim)[1].split("_");

		// Check to see if title is multi-line
		if (title.length > 1)
			multiLineTitle = true;

		String names = data[1].split(delim)[1];
		float floodStart = PApplet.parseFloat(data[2].split(delim)[1]);
		float floodEnd = PApplet.parseFloat(data[3].split(delim)[1]);
		float floodRate = PApplet.parseFloat(data[4].split(delim)[1]);
		float waveHeight = PApplet.parseFloat(data[5].split(delim)[1]);
		float launchRate = PApplet.parseFloat(data[6].split(delim)[1]);
		float decayRate = PApplet.parseFloat(data[7].split(delim)[1]);

		settings = new Settings(parent, title, names, floodStart, floodEnd,
				floodRate, waveHeight, launchRate, decayRate);

	}

	void initSkyline() {
		skyline = parent.loadImage("data/skyline.jpg");
		skyWidth = PApplet.parseInt(1.05f * Gummies.mWidth);
		skyHeight = PApplet.parseInt(1.7f * Gummies.mHeight);
		// skyline.resize(skyWidth, skyHeight);
		// skyline.loadPixels();
		// for (int i = 0; i < skyline.width; i++) {
		// for (int j = 0; j < skyline.height; j++) {
		// int loc = i + j*skyline.width;
		// int c = skyline.get(i, j);
		// float bright = parent.brightness(c);
		// if (bright > 100) {
		// float dynamicFill = PApplet.map(j, 2 * (skyWidth / 3), skyHeight,
		// 200, 0);
		// skyline.pixels[loc] = parent.color(dynamicFill);
		// }
		// }
		// }
		// skyline.updatePixels();
	}

	void updateSkyline(int whichSkyline) {
		if (whichSkyline == -1)
			skyline = parent.loadImage("data/skyline.jpg");
		else if (whichSkyline == 0)
			skyline = parent.loadImage("data/skyline-masked.jpg");
		else if (whichSkyline == 1)
			skyline = parent.loadImage("data/skyline-saved.jpg");
	}
}
