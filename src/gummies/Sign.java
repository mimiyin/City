package gummies;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;

import pbox2d.*;

import processing.core.PApplet;
import processing.core.PFont;

public class Sign {
	PApplet parent;
	PBox2D box2d;
	Settings settings;

	Vec2 center;
	float tilt;
	float titleYOffset;
	float namesYOffset;
	float titleTextSize;
	float namesTextSize;
	boolean multiLineTitle;
	PFont font;

	// Make boxes from pshapes
	ArrayList<SignBox> signboxes = new ArrayList<SignBox>();
	float res;
	float margin;
	float signHeight;

	Sign(PApplet p, PBox2D _box2d, Settings s, Vec2 c, float t, float tYO,
			float nYO, float tTS, float nTS, boolean mLT, PFont f, float r, float m, float sH) {
		parent = p;
		box2d = _box2d;
		settings = s;

		center = c;
		tilt = t;
		
		titleYOffset = tYO;
		namesYOffset = nYO;
		
		titleTextSize = tTS;
		namesTextSize = nTS;
		multiLineTitle = mLT;
		font = f;

		res = r;
		margin = m;
		signHeight = sH;
		
		init();
	}

	void init() {
		// Create and set font for credits
		// PFont tempCredits = parent.loadFont("data/adbxtra.ttf");
		parent.textFont(font);

		// Center-align text
		parent.textAlign(PApplet.CENTER);

		// Create background for credits
		parent.textSize(namesTextSize);
		int namesWidth = PApplet.parseInt(parent.textWidth(settings.names));

		parent.textSize(titleTextSize);
		int titleWidth = PApplet.parseInt(parent.textWidth(settings.title[0]));

		// Test for multi-line title and re-adjust sign width and height
		// accordingly
		if (multiLineTitle) {
			parent.textSize(namesTextSize);
			titleWidth = PApplet.parseInt(parent.textWidth(settings.title[1]));
			signHeight *= 1.15f;
		}

		float leftMargin = margin*.25f;
		int signWidth = titleWidth > namesWidth ? titleWidth : namesWidth;
		signWidth += margin;

		// Calculate center of sign
		Vec2 signCenter = new Vec2(signWidth / 2 - leftMargin, signHeight / 2);

		// How much to tilt the sign
		float color = 0;

		for (int col = 0; col <= signWidth; col += res) {
			for (int row = 0; row < signHeight; row += res) {
				Vec2 loc = new Vec2(col, row);
				// Find the angle of rotation for this point relative to the
				// center of the sign
				float direction = PApplet.atan2(signCenter.y - loc.y,
						signCenter.x - loc.x);
				// Calculate the distance between this point and the center of
				// the sign
				float radius = signCenter.sub(loc).length();
				// Calculate the x,y location of this point with added tilt,
				// relative to center of sign
				// Add offsets so the sign appears in the center
				loc.x = center.x + radius
						* PApplet.cos(direction + tilt);
				loc.y = center.y + radius
						* PApplet.sin(direction + tilt);
				signboxes
						.add(new SignBox(parent, box2d, loc, res, tilt, color));
			}
		}
	}
		
	void display() {

		// Display our signboxes
		for (int i = 0; i < signboxes.size(); i++) {
			SignBox bx = signboxes.get(i);
			float toss = parent.random(100);
			if (toss < parent.noise(Stage.t)
					* (settings.decayRate / settings.launchRate)) {
				PApplet.println("Decay");
				bx.setActive(true);
			}
			bx.display();
		}
		parent.stroke(0);
		parent.strokeWeight(100);
		parent.line(center.x, center.y, center.x - 100,
				Gummies.mHeight);
		parent.strokeWeight(1);
		parent.noStroke();

		// Boxes that leave the screen, we delete them
		// (note they have to be deleted from both the box2d world and our list
		for (int i = signboxes.size() - 1; i >= 0; i--) {
			SignBox b = signboxes.get(i);
			if (b.done()) {
				signboxes.remove(i);
			}
		}

		// Display credits
		parent.pushMatrix();
		parent.translate(center.x, center.y);
		parent.rotate(tilt);
		parent.fill(255);
		if (multiLineTitle) {
			parent.textSize(titleTextSize);
			parent.text(settings.title[0], 0, titleYOffset);
			parent.textSize(namesTextSize);
			parent.text(settings.title[1], 0, namesYOffset);
			parent.textSize(namesTextSize);
			parent.text(settings.names, 0, namesYOffset * 2);

		} else {
			parent.textSize(titleTextSize);
			parent.text(settings.title[0], 0, titleYOffset);
			parent.textSize(namesTextSize);
			parent.text(settings.names, 0, namesYOffset);
		}
		parent.popMatrix();

	}
}
