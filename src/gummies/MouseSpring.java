package gummies;

import processing.core.PApplet;

import pbox2d.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

public class MouseSpring {

	PApplet parent;

	PBox2D box2d;
	MouseJoint mj;
	Body b;

	Vec2 center;
	float x;
	float xSpeed = 100;

	MouseSpring(PApplet p, PBox2D box2d_, Body b_, Vec2 center_) {
		parent = p;
		box2d = box2d_;
		b = b_;
		center = center_;

		bind();
	}

	void update() {
		// PApplet.println("Mouse Position: " + parent.mouseX);
		
		//Vec2 mouseWorld = box2d.coordPixelsToWorld((float) parent.mouseX / Gummies.scale, (float) parent.mouseY / Gummies.scale);
		
		//mj.setTarget(mouseWorld);
	}

	void display() {

		// // Display the Spring
		// // We can get the two anchor points
		// Vec2 v1 = null;
		// Vec2 v2 = null;
		// mj.getAnchorA(v1);
		// mj.getAnchorB(v2);
		//
		// //PApplet.println("Box2D Coordinates: " + v1 + "\t" + v2);
		//
		// // Convert to screen coordinates
		// v1 = box2d.coordWorldToPixels(v1);
		// v2 = box2d.coordWorldToPixels(v2);
		//
		// //PApplet.println("Screen Coordinates: " + v1 + "\t" + v2);
		//
		// // And just draw a line
		// parent.stroke(0);
		// parent.strokeWeight(1);
		// parent.line(v1.x, v1.y, v2.x, v2.y);

	}

	void bind() {

		// Create mouse joint
		MouseJointDef md = new MouseJointDef();
		md.bodyA = box2d.getGroundBody();

		// Attach the Box body.
		md.bodyB = b;

		Vec2 mp = box2d.coordPixelsToWorld(center);
		md.target.set(mp);

		//PApplet.println("Body Coordinates: " + mp);

		// Set properties.
		md.maxForce = 1000000.0f; //(float)Gummies.mWidth*10000000;
		md.frequencyHz = 50.0f;
		md.dampingRatio = 10.0f;

		// Create the joint.
		mj = (MouseJoint) box2d.world.createJoint(md);
	}

	void destroy() {
		// We can get rid of the joint when the mouse is released
		if (mj != null) {
			box2d.world.destroyJoint(mj);
			mj = null;
		}
	}
}
