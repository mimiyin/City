package gummies;
import processing.core.PApplet;
import processing.core.PShape;

public class Settings {
	PApplet parent;
	PShape blackLetters, whiteLetters;
	float floodStart, floodEnd, floodRate;
	
	Settings(PApplet p, String bL, String wL, float fS, float fE, float fR) {
		parent = p;
		blackLetters = parent.loadShape(bL);
		whiteLetters = parent.loadShape(wL);
		floodStart = fS;
		floodEnd = fE;
		floodRate = fR;
	}

}
