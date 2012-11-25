package gummies;

import processing.core.PApplet;
import processing.core.PVector;

public class Spring {
	PApplet parent;
	PVector anchor;
	float x_anchor, k, len;

	Spring(PApplet parent_, float x_anchor_, float k_) {
		parent = parent_;
		x_anchor = x_anchor_;
		anchor = new PVector(x_anchor, Gummies.mHeight / 2);
		k = k_;
		len = PApplet.abs(x_anchor);
	}

	PVector connect(Bear bear) { // calculate spring force, anchor position is
									// set when created
		PVector spr_force = PVector.sub(bear.location, anchor);

		float d = spr_force.mag();
		float stretch = d - len;

		spr_force.normalize();
		spr_force.mult(-1 * k * stretch);

		return spr_force;
	}
}
