package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Cannonball extends GameObject{

	public Cannonball(Point3D location, double angle) {
		super(location);
		this.id = ID.Cannonball;
		
		width = 5;
		height = width;
		
		maxVelocity = 3;
		velocity = new Vector(angle, maxVelocity);
		
	}

	@Override
	public void tick() {
		moveXY();
	}

	@Override
	public void render(Graphics g) {
		
		g.setColor(Color.black);
		g.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
		
	}
	
	

}
