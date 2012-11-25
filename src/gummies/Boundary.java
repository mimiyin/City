package gummies;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.*;

import pbox2d.PBox2D;
import processing.core.PApplet;

public class Boundary {
	PApplet parent;

	// We need to keep track of a Body and a width and height
	Body body;
	float w, h;

	// A reference to our box2d world
	PBox2D box2d;
	
	MouseSpring ms;


	// Constructor
	Boundary(PApplet p, PBox2D box2d_, float x, float y) {
		parent = p;
		box2d = box2d_;
		w = 50;
		h = 500;
		// Add the box to the box2d world
		makeBody(new Vec2(x, y), w, h);
		
	}

	// This function removes the particle from the box2d world
	void killBody() {
		box2d.destroyBody(body);
	}

	// Is the particle ready for deletion?
	boolean done() {
		// Let's find the screen position of the particle
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Is it off the bottom of the screen?
		if (pos.y > Gummies.mHeight + w * h) {
			killBody();
			return true;
		}
		return false;
	}
	
	void update() {
		ms.update();
		
	}

	// Drawing the box
	void display() {
		// We look at each body and get its screen position
		Vec2 pos = box2d.getBodyPixelCoord(body);
		
		//PApplet.println("Boundary Position: " + pos.x);
		
		// Get its angle of rotation
		float a = body.getAngle();

		parent.rectMode(PApplet.CENTER);
		parent.pushMatrix();
		parent.translate(pos.x, pos.y);
		parent.rotate(-a);
		parent.fill(175);
		parent.stroke(0);
		parent.rect(0, 0, w, h);
		parent.popMatrix();
		
		ms.display();
	}

	// This function adds the rectangle to the box2d world
	void makeBody(Vec2 center, float w_, float h_) {

		// Define a polygon (this is what we use for a rectangle)
		PolygonShape sd = new PolygonShape();
		float box2dW = box2d.scalarPixelsToWorld(w_ / 2);
		float box2dH = box2d.scalarPixelsToWorld(h_ / 2);
		sd.setAsBox(box2dW, box2dH);

		// Define a fixture
		FixtureDef fd = new FixtureDef();
		fd.shape = sd;
		// Parameters that affect physics
		fd.density = 100.0f;
		fd.friction = 0.0f;
		fd.restitution = 0.0f;

		// Define the body and make it from the shape
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(box2d.coordPixelsToWorld(center));

		body = box2d.createBody(bd);
		body.createFixture(fd);

		// Give it some initial random velocity
//		body.setLinearVelocity(new Vec2(500, 0));
//		body.setAngularVelocity(parent.random(-50, 50));

		ms = new MouseSpring(parent, box2d, body, center);

	}

}
