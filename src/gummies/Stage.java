package gummies;

import java.util.ArrayList;
import pbox2d.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.joints.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Stage {
	PApplet parent;

	ArrayList<Spring> springs = new ArrayList<Spring>();
	ArrayList<Bear> bears = new ArrayList<Bear>();

	public static int t;
	PImage[] gummyImgs = new PImage[3];
	PImage gummyMask, sky;

	// A reference to our box2d world
	PBox2D box2d;

	// A list we'll use to track fixed objects
	ArrayList<Boundary> boundaries;
	// A list for all of our rectangles
	ArrayList<Box> boxes;
	
	// Define altitude and light source for shadows
	public static float alt = Gummies.mHeight;
	public static PVector source = new PVector(Gummies.mWidth/5, Gummies.mHeight/5);

	Stage(PApplet parent_) {
		parent = parent_;
		t = PApplet.parseInt(parent.random(1000));
		sky = parent.loadImage("sky.jpg");
		gummyMask = parent.loadImage("gummy_mask.jpg");

		for (int i = 0; i < gummyImgs.length; i++) {
			gummyImgs[i] = parent.loadImage("gummy_" + i + ".jpg");
			gummyImgs[i].mask(gummyMask);
		}

		// Initialize box2d physics and create the world
		box2d = new PBox2D(parent);
		box2d.createWorld();

		// Create ArrayLists
		boxes = new ArrayList<Box>();
		boundaries = new ArrayList<Boundary>();

		// Add a bunch of fixed boundaries
		boundaries.add(new Boundary(parent, box2d, Gummies.mWidth / 2,
				Gummies.mHeight / 2));
	}

	void run() {

		if (bears.size() < 100)
			launchGummies();

		parent.image(sky, 0, 0, Gummies.mWidth, Gummies.mHeight);

		// Constantly change size, rotation, opacity and strength of springs for
		// each bear
		for (int i = 0; i < bears.size(); i++) {

			Bear thisBear = (Bear) bears.get(i);
			thisBear.run();

			// If bear reach right side of window, kill it.
			if (thisBear.die())
				bears.remove(thisBear);
		}

		// We must always step through time!
		box2d.step();
		
		// Boxes fall from the top every so often
		if (parent.random(1) <.05) {
			Box p = new Box(parent, box2d, Gummies.mWidth / 2, 30);
			boxes.add(p);
		}

		// Display all the boundaries
		for (Boundary wall : boundaries) {
			wall.update();
			wall.display();
		}

		// Display all the boxes
		for (Box b : boxes) {
			b.display();
		}

		// Boxes that leave the screen, we delete them
		// (note they have to be deleted from both the box2d world and our list
		for (int i = boxes.size() - 1; i >= 0; i--) {
			Box b = boxes.get(i);
			if (b.done()) {
				boxes.remove(i);
			}
		}
	}

	void launchGummies() {

		// Create new springs and bears at a controlled rate
		int toss = PApplet.parseInt(parent.random(1000));
		if (toss % 25 == 0) {
		//if(bears.size() < 1) {
			// When launching new bears...
			// Choose a color gummy at random
			bears.add(new Bear(parent, gummyImgs[PApplet.parseInt(parent
					.random(0, gummyImgs.length))], (parent.noise(t
					+ parent.random(100)) * 100)));

			t += parent.random(-1, 5);
		}
	}
}
