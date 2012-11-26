package gummies;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import pbox2d.PBox2D;

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

	PBox2D box2d;
	Body body;

	Bear(PApplet parent_, PImage gummy_, float amp_, PBox2D box2d_) {
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
		
		PApplet.println("Dir: " + dir + "\tStart: " + startX);
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
				* parent.noise(Stage.t) * parent.random(0.05f);
		spring = new Spring(parent, xAnchor, k);

		// Create shadow
		points = initPoints();
		angles = initAngles();

		shadow = new Shadow(parent, gummy, points);

		// Add Bear to the box2d world
		box2d = box2d_;
		makeBody(new Vec2(location.x/Gummies.scale, location.y/Gummies.scale), width, height);
	}

	PVector[] initPoints() {
		width = 10;
		height = width * 1.65f;

		PVector[] points = { new PVector(-width / 2, -height / 2),
				new PVector(-width / 2, height / 2),
				new PVector(width / 2, height / 2),
				new PVector(width / 2, -height / 2), };
		return points;
	}

	float[] initAngles() {
		float[] angles = new float[points.length];
		for (int p = 0; p < points.length; p++) {
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
		float ampDivider = (PApplet.sin(c_angle) * amp);
		if (ampDivider == 0)
			ampDivider++;
		location.y = (float) height / ampDivider; 
		c_angle += .01 * freq; 
		freq = parent.noise(Stage.t) * 10;
	}

	// Turn the bear either to the right or left
	void turn() {
		r_angle += parent.noise(Stage.t) * 0.15 * dir;
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
		acceleration.x = (float) speedTuner
				* (PApplet.sin(r_angle) * parent.noise(Stage.t) * parent
						.random(-3, 5));
	}

	// Kill the bear if it exist off the right side of the screen
	boolean die() {
		if (location.x > screenWidth*2 || location.x < -screenWidth ) {
			PApplet.println("DIE DIE DIE DIE DIE DIE DIE DIE: " + location.x);
			return true;
		}
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
		
		// Update box2d
		Vec2 pos = body.getWorldCenter();
		Vec2 target = box2d.coordPixelsToWorld(location);
		body.setLinearVelocity(target);
		body.setTransform(target, r_angle);
		
		// Recalculate the radius
		r = PApplet.sqrt(PApplet.sq(width) + PApplet.sq(height)) / 2;
		for (int p = 0; p < points.length; p++) {
			points[p] = new PVector(r * PApplet.cos(angles[p] + r_angle), r
					* PApplet.sin(angles[p] + r_angle));
		}

		shadow.update(points);
	}
	
	 void display() {
//			parent.pushMatrix();
//			parent.translate(location.x, location.y);
//			shadow.display();
//			parent.rotate(r_angle);
//	
//			parent.imageMode(PApplet.CENTER);
//			parent.image(gummy, 0, 0, width, height);
//			parent.imageMode(PApplet.CORNER);
//	
//			parent.popMatrix();
			
		    // We look at each body and get its screen position
		    Vec2 pos = box2d.getBodyPixelCoord(body);
		    // Get its angle of rotation
		    float a = body.getAngle();

		    parent.rectMode(PConstants.CENTER);
		    parent.pushMatrix();
		    parent.translate(pos.x,pos.y);
		    shadow.display();
		    parent.rotate(a);
		    parent.fill(175);
		    parent.stroke(0);
		    parent.image(gummy, 0,0,width,height);
		    parent.popMatrix();
		  }

	// This function adds the rectangle to the box2d world
	void makeBody(Vec2 center, float w_, float h_) {
		// Define and create the body
		BodyDef bd = new BodyDef();
		bd.type = BodyType.KINEMATIC;
		bd.position.set(box2d.coordPixelsToWorld(center));
		body = box2d.createBody(bd);

		// Define a polygon (this is what we use for a rectangle)
		PolygonShape sd = new PolygonShape();
		float box2dW = box2d.scalarPixelsToWorld(10 / 2);
		float box2dH = box2d.scalarPixelsToWorld(Gummies.mHeight / 2);
		sd.setAsBox(box2dW, box2dH);

		// Define a fixture
		FixtureDef fd = new FixtureDef();
		fd.shape = sd;
		// Parameters that affect physics
		fd.density = 10000;
		fd.friction = 0.0f;

		body.createFixture(fd);
		// body.setMassFromShapes();

		// Give it some initial random velocity
		// body.setLinearVelocity(new Vec2(parent.random(-5, 5),
		// parent.random(2, 5)));
		// body.setAngularVelocity(random(-5, 5));
	}
}
