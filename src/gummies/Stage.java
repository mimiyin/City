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

	// Credits
	float tilt;
	PFont credits;
	float creditsCenterX;
	float creditsCenterY;
	float titleYOffset;
	float namesYOffset;

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

	// Create boxes derived from svg file
	PShape bigfileb, bigfilew;

	// Make boxes from pshapes
	ArrayList<SignBox> signboxes;

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

		// Create boxes from square
		signboxes = new ArrayList<SignBox>();

		// Create skyline
		initSkyline();

		// Initialize signage
		initSignage();

	}

	void run() {

		// Draw the skyline
		parent.image(skyline, 0, -Gummies.mHeight/2, skyWidth, skyHeight);

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

		// Display our signboxes
		for (int i = 0; i < signboxes.size(); i++) {
			SignBox bx = signboxes.get(i);
			float toss = parent.random(100);
			if (toss < parent.noise(t) * .025f) {
				PApplet.println("Decay");
				bx.setActive(true);
			}
			bx.display();
		}
		parent.stroke(0);
		parent.strokeWeight(100);
		parent.line(Gummies.mWidth / 2, Gummies.mHeight / 2,
				Gummies.mWidth / 2 - 100, Gummies.mHeight);
		parent.strokeWeight(1);
		parent.noStroke();

		// Boxes that leave the screen, we delete them
		// (note they have to be deleted from both the box2d world and our list
		for (int i = signboxes.size() - 1; i >= 0; i--) {
			SignBox b = signboxes.get(i);
			if (b.done()) {
				signboxes.remove(i);
			}
		}

		// Display credits
		parent.pushMatrix();
		parent.translate(creditsCenterX, creditsCenterY);
		parent.rotate(tilt);
		parent.fill(255);
		parent.textSize(256);
		parent.text(settings.title, 0, titleYOffset);
		parent.textSize(128);
		parent.text(settings.names, 0, namesYOffset);
		parent.popMatrix();

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
		String title = data[0].split(delim)[1];
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

	void initSignage() {
		// Create and set font for credits
		// PFont tempCredits = parent.loadFont("data/adbxtra.ttf");
		credits = parent.createFont("data/AuXDotBitC.ttf", 540);
		parent.textFont(credits);
		// Center-align text
		parent.textAlign(PApplet.CENTER);
		creditsCenterX = Gummies.mWidth / 2;
		creditsCenterY = Gummies.mHeight / 2;
		titleYOffset = -15;
		namesYOffset = 160;

		// Create background for credits
		parent.textSize(128);
		int signWidth = PApplet
				.parseInt(parent.textWidth(settings.names) + 100);
		int signHeight = 500;
		Vec2 signCenter = new Vec2(signWidth / 2 - 25, signHeight / 2);
		float signXOffset = creditsCenterX - (signWidth / 2);
		float signYOffset = signHeight/2;
		int res = 50;
		// How much to tilt the sign
		tilt = PApplet.PI / 36;

		for (int col = 0; col <= signWidth; col += res) {
			for (int row = 0; row < 500; row += res) {
				Vec2 loc = new Vec2(col, row);
				// Find the angle of rotation for this point relative to the
				// center of the sign
				float direction = PApplet.atan2(signCenter.y - loc.y,
						signCenter.x - loc.x);
				// Calculate the distance between this point and the center of
				// the sign
				float radius = signCenter.sub(loc).length();
				// Calculate the x,y location of this point with added tilt,
				// relative to center of sign
				// Add offsets so the sign appears in the center
				loc.x = signCenter.x + radius * PApplet.cos(direction + tilt)
						+ signXOffset;
				loc.y = signCenter.y + radius * PApplet.sin(direction + tilt)
						+ signYOffset;
				float color = 0;
				signboxes
						.add(new SignBox(parent, box2d, loc, res, tilt, color));
			}
		}
	}

	void initSkyline() {
		skyline = parent.loadImage("data/skyline-saved.jpg");
		skyWidth = PApplet.parseInt(Gummies.mWidth);
		skyHeight = PApplet.parseInt(1.5f * Gummies.mHeight);		
//		skyline.resize(skyWidth, skyHeight);
//		skyline.loadPixels();
//
//		
//		for (int i = 0; i < skyWidth; i++) {
//			for (int j = 0; j < skyHeight; j++) {
//				int c = skyline.get(i, j);
//				float bright = parent.brightness(c);
//				if (bright > 100) {
//					float dynamicFill = PApplet.map(j, 2 * (skyHeight / 3), skyHeight, 200, 0);
//					int cellColor = parent.color(dynamicFill);
//					skyline.set(i, j, cellColor);
//					}					
//				}
//			}
//		skyline.updatePixels();
	}
}
