package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Ship extends GameObject{
	
	public boolean selected = true;

	public double health, maxHealth;
	public double rudderLocation = 0; //-100 full left, +100 full right
	public double minRudderLocation = -100;
	public double maxRudderLocation = 100;
	public double rudderSpeed = .5; //how quickly rudderLocation increases or decreases
	
	public double windInSails = 0;
	public double maxWindInSails = 100;
	public double windInSailsIncrement = 2;
	
	public double radialVelocity;
	public double maxRadialVelocity = .005;

	public double heading;
	public double movementDirection;
	
	public double radialDrag;
	public double straightLineDrag;
	
	public KeyInput kInput;
	public MouseInput mInput;
	
	public Ship(Point3D location, Game game) {
		super(location);
		
		width = 64;
		height = 32;
		
		kInput = game.getKeyInput();
		mInput = game.getMouseInput();
		
		maxAcceleration = .01;
		maxVelocity = .2;
		
		/*
		Vector test = new Vector(2, 3, 6);
		test.print();
		System.out.println("length is: " + test.magnitude() + ", direction is: " + test.get2DDirection());
		test.setMagnitude(8.1818);
		test.print();
		System.out.println("length is: " + test.magnitude() + ", direction is (+1.2) " + test.get2DDirection());
		*/
	}

	public void tick() {
		if(!selected) return;
		
		keyInputCheck();
		printGameObjectVectors();
		printShipInformation();
		
		//clampMovement();
		
		turn();
		advance();
	}
	
	public void turn() {
		setTurnRateFromRudderPosition();
		heading += radialVelocity;
		velocity.turn(radialVelocity);
	}
	
	public void setTurnRateFromRudderPosition() {
		radialVelocity = rudderLocation / maxRudderLocation * maxRadialVelocity;
	}
	
	public void advance() {
		setVelocityFromWindInSails();
		location = velocity.movePoint(location);
	}
	
	public void setVelocityFromWindInSails() {
		double newVelocityMag = windInSails / maxWindInSails * maxVelocity;
		if(velocity.magnitude() == 0) {
			velocity = new Vector(heading, newVelocityMag);
		} else
			velocity.setMagnitude(windInSails / maxWindInSails * maxVelocity);
	}

	public void keyInputCheck() {
		if(kInput.keyA && !kInput.keyD)
			rudderLeft();
		else if(!kInput.keyA && kInput.keyD)
			rudderRight();
		
		if(kInput.keyW && !kInput.keyS)
			increaseWind();
		//else 
			//decreaseWind();
	}
	
	public void rudderLeft() {
		rudderLocation -= rudderSpeed;
		rudderLocation = Game.clamp(rudderLocation, minRudderLocation, maxRudderLocation);
	}
	
	public void rudderRight() {
		rudderLocation += rudderSpeed;
		rudderLocation = Game.clamp(rudderLocation, minRudderLocation, maxRudderLocation);
	}
	
	public void decreaseWind() {
		windInSails -= windInSailsIncrement;
		windInSails = Game.clamp(windInSails, 0, maxWindInSails);
	}
	
	public void increaseWind() {
		windInSails += windInSailsIncrement;
		windInSails = Game.clamp(windInSails, 0, maxWindInSails);
	}

	public void calculateDrag() {
		movementDirection = velocity.get2DDirection();
	}
	
	public void clampMovement() {
		acceleration.clamp(maxAcceleration);
		velocity.clamp(maxVelocity);
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.rotate(heading, getCenterX(), getCenterY());
		g.setColor(Color.black);
		g.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
		g2d.rotate(-heading, getCenterX(), getCenterY());
	}
	
	public void printShipInformation() {
		System.out.println("Heading: " + heading + ", movementDirection: " + movementDirection);
		System.out.println("rudderLocation: " + rudderLocation + ", windInSails: " + windInSails);
		System.out.println("radialVelocity: " + radialVelocity);
		System.out.println("radialDrag: " + radialDrag + ", straightLineDrag: " + straightLineDrag);
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
}
