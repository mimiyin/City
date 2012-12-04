package gummies;

import java.util.*;

import org.jbox2d.common.*;
import pbox2d.*;
import processing.core.*;

class Water {

	int xspacing = 10; // how far apart horizontally.
	int w; // width of water surface
	float r = xspacing / 2;

	float yoff = 0.0f; // perlin noise 2nd dimension
	float[] yvalues; // store height values in array
	float waterLine;

	// The water surface is made up of an ArrayList of Box2d circles
	ArrayList<Circle> surface;

	PApplet parent;
	PBox2D box2d;
	float counter = 0.0f;
	Settings settings;

	

	Water(PApplet p, PBox2D box2d_,  Settings _settings) {
		parent = p;
		box2d = box2d_;

		// make a chain of water surface
		surface = new ArrayList<Circle>();

		// This is what box2d uses to put the surface in its world
		w = Gummies.mWidth + 16;
		yvalues = new float[w / xspacing];
		
		settings = _settings;
		waterLine = settings.floodStart;
		for (int i = 0; i < yvalues.length; i++) {
			surface.add(new Circle(parent, box2d, i * xspacing,
					waterLine, r));
		}
	}

	void update() {
		// Make the water line go up, to a certain point
		if(waterLine < settings.floodEnd)
			waterLine += settings.floodRate;
		
		float dx = 0.005f;
		float dy = 0.01f;
		float amplitude = settings.waveHeight;

		// Increment y ('time')
		yoff += dy;

		// float xoff = 0.0; // Option #1
		float xoff = yoff; // Option #2

		for (int i = 0; i < yvalues.length; i++) {
			// Using 2D noise function
			yvalues[i] = (2*parent.noise(xoff,yoff)-1)*amplitude; // Option #1
			// Using 1D noise function
			//yvalues[i] = (2 * parent.noise(xoff) - 1) * amplitude; // Option #2
			xoff += dx;
		}

	}

	void display() {
		// A simple way to draw the wave with an ellipse at each location
		for (int x = 0; x < yvalues.length; x++) {
			parent.noStroke();
			parent.fill(255, 50);
			Circle c = surface.get(x);
			float xPos = (float)x * xspacing;
			float yPos = waterLine + yvalues[x];
			Vec2 pos = box2d.coordPixelsToWorld(xPos,yPos);
			c.body.setTransform(pos, 0);
			c.display();
		}
		// counter-=0.1f;
	}
}
