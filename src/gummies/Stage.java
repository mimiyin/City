package gummies;

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
	PFont credits;
	float creditsCenterX;
	float titleCenterY;
	float namesCenterY;

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
		skyline = parent.loadImage("data/skyline.png");
		skyWidth = PApplet.parseInt(1.15f * Gummies.mWidth);
		skyHeight = PApplet.parseInt(1.8f * Gummies.mHeight);

		// Create and set font for credits
		// PFont tempCredits = parent.loadFont("data/adbxtra.ttf");
		credits = parent.createFont("data/AuXDotBitC.ttf", 540);
		parent.textFont(credits);
		// Center-align text
		parent.textAlign(PApplet.CENTER);
		creditsCenterX = Gummies.mWidth / 2;
		titleCenterY = Gummies.mHeight / 2 - 50;
		namesCenterY = titleCenterY + 150;

		// Create background for credits
		parent.textSize(128);
		float textWidth = parent.textWidth(settings.names) + 100;
		int resolution = 50;
		for (int col = 0; col <= (int) textWidth; col += resolution) {
			for (int row = 0; row < 500; row += resolution) {
				float thisX = col + (creditsCenterX - textWidth / 2);
				float thisY = row + 250;
				signboxes.add(new SignBox(parent, box2d, new Vec2(thisX,
						thisY), resolution, 0));
			}
		}

	}

	void run() {

		// Draw the skyline
		parent.image(skyline, -500, -600, skyWidth, skyHeight);

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
		parent.line(Gummies.mWidth/2, Gummies.mHeight/2, Gummies.mWidth/2-100, Gummies.mHeight);
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
		parent.fill(255);
		parent.textSize(256);
		parent.text(settings.title, creditsCenterX, titleCenterY);
		parent.textSize(128);
		parent.text(settings.names, creditsCenterX, namesCenterY);

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
}
