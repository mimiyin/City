package gummies;

import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;

import pbox2d.*;
import processing.core.*;

public class SVGbox {
	PApplet parent;

	// We need to keep track of a Body and a width and height and color
	Body body;
	Vec2 initPos;
	float w;
	float h;
	float c;

	// A reference to our box2d world
	PBox2D box2d;

	// Constructor
	SVGbox(PApplet p, PBox2D box2d_, PShape s, float _c) {
		parent = p;
		box2d = box2d_;
		initPos = new Vec2(s.getParam(0), s.getParam(1));
		w = s.getParam(2);
		h = s.getParam(3);
		c = _c;

		// Add the box to the box2d world
		makeBody(initPos, w, h);
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
		Vec2 pos = box2d.getBodyPixelCoord(body);
		// Get its angle of rotation
		float a = body.getAngle();

		parent.rectMode(parent.CENTER);
		parent.pushMatrix();
		parent.translate(pos.x, pos.y);
		parent.rotate(-a);
		parent.fill(c);
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
		fd.density = 1000;
		fd.friction = (float) 0.3;
		fd.restitution = (float) 0.5;

		// Define the body and make it from the shape
		BodyDef bd = new BodyDef();
		bd.type = BodyType.DYNAMIC;
		bd.position.set(box2d.coordPixelsToWorld(center));

		body = box2d.createBody(bd);
		body.createFixture(fd);

		// Give it some initial random velocity
		// body.setLinearVelocity(new Vec2(parent.random(-50, 50),
		// parent.random(
		// -5000, -10000 )));
		// body.setAngularVelocity(parent.random(-50, 50));
		body.setActive(false);
	}

	void restore(float waterLine) {
		if(waterLine > initPos.y) {
			body.setType(BodyType.KINEMATIC);
			Vec2 pos = body.getWorldCenter();
			Vec2 target = box2d.coordPixelsToWorld(initPos);
			Vec2 diff = new Vec2(target.x - pos.x, target.y - pos.y);
			diff.mulLocal(50);
			body.setLinearVelocity(diff);
		}
	}

	void setActive(boolean isActive) {
		body.setActive(isActive);
	}

}
