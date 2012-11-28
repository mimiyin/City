package gummies;
import processing.core.PApplet;
import processing.core.PShape;

public class Settings {
	PApplet parent;
	PShape blackLetters, whiteLetters;
	float floodStart, floodEnd, floodRate, waveHeight, launchRate;
	
	Settings(PApplet p, String bL, String wL, float fS, float fE, float fR, float wH, float lR) {
		parent = p;
		blackLetters = parent.loadShape(bL);
		whiteLetters = parent.loadShape(wL);
		floodStart = fS;
		floodEnd = fE;
		floodRate = fR;
		waveHeight = wH;
		launchRate = lR;
	}

}
