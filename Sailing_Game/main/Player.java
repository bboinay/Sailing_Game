package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Player extends Ship{

	private KeyInput kInput;
	private Game game;
	
	public double priority = 200;
	
	public Player(Point3D origin, Game game) {
		super(origin, game);
		setId(ID.Player);
		
		width = 64;
		height = 32;
		
		this.game = game;
		kInput = game.getKeyInput();
	}
	
	
	
	
}
