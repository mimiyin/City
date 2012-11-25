package gummies;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;


public class Shadow {
	PVector[] points, corners, shadow;
	float alt;
	PImage img;

	PApplet parent;

	Shadow(PApplet p, PImage _texture, PVector[] _points) {
		parent = p;

		points = _points;
		shadow = new PVector[points.length];

		img = _texture;
		PVector[] _corners = { new PVector(0, 0), new PVector(0, img.height),
				new PVector(img.width, img.height), new PVector(img.width, 0), };

		corners = _corners;
		// Project the shadow
		project(_points);
	}

	// Update the relative light source position
	// Re-project the shadow
	void update(PVector[] _points) {
		project(_points);
	}

	// Draw the shadow with texture mapping
	void display() {
		parent.noStroke();
		parent.tint(0, 17);
		parent.beginShape();
		parent.texture(img);
		for (int p = 0; p < shadow.length; p++) {
			parent.vertex(shadow[p].x, shadow[p].y, corners[p].x, corners[p].y);
		}
		parent.endShape();
		parent.noTint();

	}

	// Project each corner of the object onto the "ground"
	void project(PVector[] _points) {

		for (int p = 0; p < _points.length; p++) {

			// Find the angle of the corner being project relative to the sun
			PVector projection = _points[p].get();
			projection.sub(Stage.source);
			float angle = projection.heading2D();

			// Calculate the distance of projection
			// Based on height of the point from the "ground" as defined by the
			// alt
			float _alt = PApplet.abs(_points[p].y - Stage.alt);
			float yOffset = _alt * PApplet.sin(angle);
			float radius = yOffset / PApplet.sin(angle);

			shadow[p] = _points[p].get();
			shadow[p].add(new PVector(radius * PApplet.cos(angle), radius
					* PApplet.sin(angle)));

			// println("Source: " + source + "\t" + "Point: " + p + "\t" +
			// "Angle: " + degrees(angle) + "\t" + "Offset: " + offset);
		}
	}
}
