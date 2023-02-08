package com.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public abstract class GameObject {

	protected double width, height;
	protected double health, maxHealth;
	protected Point3D location;
	protected Vector velocity = new Vector();
	protected Vector acceleration = new Vector();
	protected ID id;
	protected double maxVelocity;
	protected double maxAcceleration;
	protected int priority;

	protected BufferedImage spriteSheet = null;
	protected BufferedImage sprite;
	protected int spriteRow = 1;
	protected int spriteCol = 1;
	
	public GameObject(Point3D location) {
		this.location = location;
		this.id = ID.GameObject;
		
		maxVelocity = 1;
		maxAcceleration = 10;
	}

	public abstract void tick();
	public abstract void render(Graphics g);
		
	//VECTOR CLAMP METHODS
	public void clampVelocity() {
		velocity.clamp(maxVelocity);
	}
	public void clampAcceleration() {
		acceleration.clamp(maxAcceleration);
	}
	
	//VECTOR UPDATE METHODS
	public void move() {
		updateVelocityWithClampedAcceleration();
		updatePositionWithClampedVelocity();
	}
	
	public void updateVelocityWithClampedAcceleration() {
		acceleration.clamp(maxAcceleration);
		
		velocity.addToXComponent(acceleration.getXComponent());
		velocity.addToYComponent(acceleration.getYComponent());
		velocity.addToZComponent(acceleration.getZComponent());
	}
	
	public void updatePositionWithClampedVelocity() {
		velocity.clamp(maxVelocity);
		
		location.setX(location.getX() + velocity.getXComponent());
		location.setY(location.getY() + velocity.getYComponent());
		location.setZ(location.getZ() + velocity.getZComponent());
	}
	
	
	//PHYSICS GETTERS & SETTERS
	public Point3D getLocation() {
		return location;
	}
	
	public double getX() {
		return location.getX();
	}
	
	public void setX(double x) {
		location.setX(x);
	}
	
	public double getY() {
		return location.getY();
	}
	
	public void setY(double y) {
		location.setY(y);
	}
	
	public double getZ() {
		return location.getZ();
	}
	
	public void setZ(double z) {
		location.setZ(z);
	}
	
	public Vector getVelocityVector() {
		return velocity;
	}
	
	public Vector getAccelerationVector() {
		return acceleration;
	}
	
	//given an angle, sets the velocity vector to maxVelocity in that direction
	public void set2DVelocityVectorFromAngle(double angle) {
		velocity.setXComponent(maxVelocity * Math.cos(angle));
		velocity.setYComponent(maxVelocity * Math.sin(angle));
		
		velocity.clamp(maxVelocity);
	}
	
	//ID GETTER & SETTER
	public ID getId() {
		return id;
	}
	public void setId(ID id) {
		this.id = id;
	}
	
	//BASIC RENDER (BOX)
	public void renderBasicBox(Graphics g, Color borderColor, Color mainColor, int lineThickness) {
		g.setColor(borderColor);
		g.fillRect((int)getX(), (int)getY(), (int)width, (int)height);
		g.setColor(mainColor);
		g.fillRect((int)getX() + lineThickness, (int)getY() + lineThickness, (int)width - 2 * lineThickness, (int)height - 2 * lineThickness);
	}
	
	//BASIC GEOMETRY GETTERS & SETTERS
	public Rectangle getBounds() {
		return new Rectangle((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
	}
	public Rectangle horizontalMovingBounds() {
		return new Rectangle((int)(getX() + velocity.getXComponent()), (int)(getY()), (int)getWidth(), (int)getHeight());
	}
	public Rectangle verticalMovingBounds() {
		return new Rectangle((int)getX(), (int)(getY()+ velocity.getYComponent()), (int)getWidth(), (int)getHeight());
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public int getCenterX() {
		return (int)(getX() + width / 2);
	}
	public int getCenterY() {
		return (int)(getY() + height / 2);
	}
	
	//HEALTH
	public void decreaseHealthBy(double damage) {
		health -= damage;
		clampHealth();
	}
	
	public void clampHealth() {
		health = Game.clamp(health, 0, maxHealth);
	}
	
	public double 	getHealth() {return Game.clamp(health, 0, maxHealth);}
	public void 	setHealth(double d) {health = d;}
	
	public double 	getMaxHealth() {return maxHealth;}
	public void 	setMaxHealth(double d) {maxHealth = d;}

	//TESTING
	public void testRenderBounds(Graphics g) {
		g.setColor(Color.red);
		Graphics2D g2d = (Graphics2D)g;
		g2d.fill(getBounds());
	}
	
	public void testRenderTheseBounds(Graphics g, Rectangle r) {
		g.setColor(Color.red);
		Graphics2D g2d = (Graphics2D)g;
		g2d.fill(r);
	}
	
	public void printMovementVectors() {
		System.out.print("location is: ");
		location.print();
		System.out.print("acceleration is: ");
		acceleration.print();
		System.out.print("velocity is: ");
		velocity.print();
		System.out.println("maxVelocity is: " + maxVelocity);
		System.out.println("maxAcceleration is: " + maxAcceleration);
	}
	
	public void printMovementVectors(String message) {
		System.out.println("~~~~~" + message + "~~~~~");
		printMovementVectors();
	}
}
