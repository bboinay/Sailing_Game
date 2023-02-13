package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public abstract class GameObject {

	protected double width, height;
	
	protected Point3D location;
	protected Vector velocity = new Vector();
	protected Vector acceleration = new Vector();
	
	protected double maxVelocity;
	protected double maxAcceleration;
	
	protected ID id;
	protected int priority;
	
	public GameObject(Point3D location) {
		this.location = location;
		this.id = ID.GameObject;
		
		
	}

	public abstract void tick();
	public abstract void render(Graphics g);
		
	//VECTOR UPDATE METHODS
	public void move() {
		updateVelocityWithAcceleration();
		updatePositionWithVelocity();
	}
		
	public void updateVelocityWithAcceleration() {
		acceleration.clamp(maxAcceleration);
		velocity.addToXComponent(acceleration.getXComponent());
		velocity.addToYComponent(acceleration.getYComponent());
		velocity.addToZComponent(acceleration.getZComponent());
	}
	
	public void updatePositionWithVelocity() {
		velocity.clamp(maxVelocity);
		location.setX(location.getX() + velocity.getXComponent());
		location.setY(location.getY() + velocity.getYComponent());
		location.setZ(location.getZ() + velocity.getZComponent());
	}
	
	public void accelerateInDirection(double accelerationAmount, double angle) {
		acceleration.addToXComponent(accelerationAmount * Math.cos(angle));
		acceleration.addToYComponent(accelerationAmount * Math.sin(angle));
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
	
	public void setMaxVelocity(double d) {
		maxVelocity = d;
	}
	
	public void setMaxAcceleration(double d) {
		maxAcceleration = d;
	}
	
	public Vector getVelocityVector() {
		return velocity;
	}
	
	public Vector getAccelerationVector() {
		return acceleration;
	}
	
	//ID GETTER & SETTER
	public ID getId() {
		return id;
	}
	public void setId(ID id) {
		this.id = id;
	}
	
	//BASIC GEOMETRY GETTERS & SETTERS
	public Rectangle getBounds() {
		return new Rectangle((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
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
	
	public void printGameObjectVectors() {
		System.out.print("location is: ");
		location.print();
		System.out.print("acceleration is: ");
		acceleration.print();
		System.out.print("velocity is: ");
		velocity.print();
	}
	
	public void printGameObjectVectors(String message) {
		System.out.println("~~~~~" + message + "~~~~~");
		printGameObjectVectors();
	}
}
