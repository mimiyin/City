package gummies;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Bear {
	PApplet parent;

	PVector[] points;
	float[] angles;

	PVector location, velocity, acceleration, pull;
	float widthMax, widthGrowthRate;
	float width, height, r;

	float rot, dir;
	float c_angle, r_angle;
	float freq, amp;
	int speedTuner;
	float g;

	PImage gummy;

	int screenWidth = Gummies.mWidth;
	int screenHeight = Gummies.mHeight;

	Spring spring;
	Shadow shadow;

	Bear(PApplet parent_, PImage gummy_, float amp_) {
		parent = parent_;
		gummy = gummy_;

		// Which way is the bear traveling? To the right or left?
		int toss = PApplet.parseInt(parent.random(-15, 10));
		if (toss > 0)
			dir = 1;
		else
			dir = -1;

		// Which side of the screen is the bear starting from?
		int startX = 0;
		if (dir < 0)
			startX = screenWidth;

		// Starting location, speed and acceleration of bear
		location = new PVector(startX, height / 2);
		velocity = new PVector(0, 0);
		acceleration = new PVector(0, 0);

		// Which way is the bear turning? Clockwise or counter-clockwise?
		rot = parent.random(-1, 1);
		if (rot < 0)
			rot = -1;
		else
			rot = 1;

		// Various tuning devices for controlling rotation and wavy-ness of bear
		// path
		freq = 1;
		amp = amp_;
		c_angle = PApplet.radians(PApplet.PI);
		r_angle = PApplet.radians(0);
		g = parent.random(0, PApplet.TWO_PI);

		// How big can the bear get

		// Randomize the max size of the gummy bears
		toss = PApplet.parseInt(parent.random(1000));
		int max = 3;
		if (toss % 10 == 0)
			max = 1;
		else if (toss % 5 == 0)
			max = 2;

		widthMax = screenHeight / max;
		widthGrowthRate = widthMax / 5;

		// Throttling how fast the bear travels across the screen
		speedTuner = PApplet.parseInt(screenWidth / 640);

		// Creating a new spring for the bear
		float xAnchor = dir * parent.noise(Stage.t + parent.random(10))
				* screenWidth * -1;
		float k = dir * parent.noise(Stage.t + parent.random(100))
				* parent.noise(Stage.t) * parent.random(0.01f);
		spring = new Spring(parent, xAnchor, k);

		// Create shadow		
		points = initPoints();
		angles = initAngles();
		
		shadow = new Shadow(parent, gummy, points);
	}
	
	PVector[] initPoints() {
		width = 10;
		height = width*1.65f;

		PVector[] points = { 
				new PVector(-width / 2, -height / 2),
				new PVector(-width / 2, height / 2),
				new PVector(width / 2, height / 2),
				new PVector(width / 2, -height / 2), };
		return points;	
	}
	
	float[] initAngles() {
		float[] angles = new float[points.length];
		for(int p = 0; p < points.length; p++) {
			angles[p] = points[p].heading2D();
		}
		
		return angles;
	}
	
	void run() {
		wave();
		turn();
		grow();
		pull();

		// Recalculate the spring force based on bear's current location
		// Apply the new spring force to the bear
		PVector springForce = spring.connect(this);
		applyForce(springForce);

		update();
		display();
	}

	// Move the bear up and down according to sine wave
	void wave() {
		location.y = height / 2 + (PApplet.sin(c_angle) * amp); // amplitude of
																// wave is set
																// when bear is
																// created

		c_angle += .01 * freq; // increment the angle to move through the sin
								// wave
		freq = parent.noise(Stage.t) * 10; // frequency changes every time
											// through draw
	}

	// Turn the bear either to the right or left
	void turn() {
		r_angle += parent.noise(Stage.t) * 0.25 * dir;
	}

	// Grow the bear...up to a point
	void grow() {
		g += .01;
		width += widthGrowthRate * PApplet.sin(g);

		width = PApplet.constrain(width, 0, widthMax); // constrain bear size
		height = 1.65f * width;
	}

	// Pull the bear back and forth (equivalent to pulling the bob on the
	// spring)
	void pull() {
		acceleration.x = speedTuner
				* (PApplet.sin(r_angle) * parent.noise(Stage.t) * parent
						.random(-3, 5));
	}

	// Kill the bear if it exist off the right side of the screen
	boolean die() {
		if (dir > 0 && location.x > screenWidth)
			return true;
		else if (dir <= 0 && location.x < 0)
			return true;
		else
			return false;
	}

	// Apply spring force
	void applyForce(PVector spr_force) {
		PVector app_force = spr_force.get();
		acceleration.add(app_force);
	}

	// Update location, velocity and re-set acceleration to zero
	void update() {
		location.add(velocity);
		velocity.add(acceleration);
		acceleration.mult(0);
		
		//Recalculate the radius
		r = PApplet.sqrt(PApplet.sq(width) + PApplet.sq(height)) / 2;
		for (int p = 0; p < points.length; p++) {
			points[p] = new PVector(r * PApplet.cos(angles[p] + r_angle), r
					* PApplet.sin(angles[p] + r_angle));
		}

		shadow.update(points);
	}

	// Draw the bear
	void display() {

		parent.pushMatrix();
		parent.translate(location.x, location.y);
		shadow.display();
		parent.rotate(r_angle);

		parent.imageMode(PApplet.CENTER);
		parent.image(gummy, 0, 0, width, height);
		parent.imageMode(PApplet.CORNER);

		parent.popMatrix();
	}

	// Return bear's location
	PVector get() {
		return location;
	}
}
