package gummies;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import pbox2d.PBox2D;
import processing.core.PApplet;
import processing.core.PVector;

public class Box {
	PApplet parent;

	// We need to keep track of a Body and a width and height
	Body body;
	float w;
	float h;

	// A reference to our box2d world
	PBox2D box2d;
	Vec2 pos, vel;

	boolean isBlown = false;

	// Constructor
	Box(PApplet p, PBox2D box2d_, float x, float y) {
		parent = p;
		box2d = box2d_;
		w = parent.random(150, 500);
		h = parent.random(150, 500);
		pos = new Vec2(x, y);
		vel = new Vec2(0, 0);

		// Add the box to the box2d world
		makeBody(pos, w, h);
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

	// Drawing the box
	void display() {
		// We look at each body and get its screen position
		pos = box2d.getBodyPixelCoord(body);
		// Get its angle of rotation
		float a = body.getAngle();

		parent.rectMode(parent.CENTER);
		parent.pushMatrix();
		parent.translate(pos.x, pos.y);
		parent.rotate(-a);
		parent.fill(175);
		parent.stroke(0);
		parent.rect(0, 0, w, h);
		parent.popMatrix();
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
		fd.density = 0.01f;
		fd.friction = 0.0f;
		fd.restitution = 1.0f;

		// Define the body and make it from the shape
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(box2d.coordPixelsToWorld(center));

		body = box2d.createBody(bd);
		body.createFixture(fd);
		
	    body.setLinearVelocity(new Vec2(parent.random(-5, 5), parent.random(2, 5)));
	    body.setAngularVelocity(parent.random(-5, 5));
	}
}
