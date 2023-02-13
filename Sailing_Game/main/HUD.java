package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class HUD {

	private Game game;
	
	private DecimalFormat df;
	
	public double cash = 0.00;
	private int hudTimer = 0;
	
	public HUD(Game game) {
		this.game = game;
		
		df = new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.DOWN);
	}
	
	public void tick() {
		hudTimer++;
	}
	
	public void render(Graphics g) {
		
	}
	
	public double 	getCash() {return cash;}
	public void 	setCash(double d) {cash = d;}
	
	public int 		getHudTimer() {return hudTimer;}
	public void 	setHudTimer(int hudTimer) {this.hudTimer = hudTimer;}
}
