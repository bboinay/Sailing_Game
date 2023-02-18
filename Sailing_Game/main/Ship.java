package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Ship extends GameObject{
	
	public boolean selected = false;

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
	
	public boolean ePressed, qPressed;
	
	public Game game;
	public KeyInput kInput;
	public MouseInput mInput;
	public Handler handler;
	
	public Ship(Point3D location, Game game) {
		super(location);
		
		width = 32;
		height = 64;
		
		this.game = game;
		handler = game.getHandler();
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
		if(selected)
			keyInputCheck();
		
		mInput.tick();
		mouseInputCheck();
		//printGameObjectVectors();
		//printShipInformation();
		
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
		else 
			decreaseWind();
		
		if(kInput.keyQ && !qPressed) {
			qPressed = true;
			fireCannonsLeft();
		} else if(!kInput.keyQ)
			qPressed = false;
		
		if(kInput.keyE && !ePressed) {
			ePressed = true;
			fireCannonsRight();
		} else if(!kInput.keyE)
			ePressed = false;
	}
	
	public void mouseInputCheck() {
		int mx = game.getMouseX();
		int my = game.getMouseY();
		Rectangle tempMouse = new Rectangle(mx - 2, my - 2, 4, 4);
		if(mInput.getSingleClicked() && tempMouse.intersects(getBounds())) {
			selected = true;
			game.getCamera().setCameraMode(CAMERAMODE.CameraLocked);
		} else if(mInput.getSingleClicked() && !tempMouse.intersects(getBounds())) {
			selected = false;
			game.getCamera().setCameraMode(CAMERAMODE.CameraFree);
		}
			
	}
	
	public void rudderLeft() {
		rudderLocation -= rudderSpeed;
		rudderLocation = Game.clamp(rudderLocation, minRudderLocation, maxRudderLocation);
	}
	
	public void rudderRight() {
		rudderLocation += rudderSpeed;
		rudderLocation = Game.clamp(rudderLocation, minRudderLocation, maxRudderLocation);
	}

	public void increaseWind() {
		windInSails += windInSailsIncrement;
		windInSails = Game.clamp(windInSails, 0, maxWindInSails);
	}
	
	public void decreaseWind() {
		windInSails -= windInSailsIncrement / 20.0;
		windInSails = Game.clamp(windInSails, 0, maxWindInSails);
	}
	
	public void fireCannonsRight() {
		double angle = heading + Math.PI / 2;
		fireCannons(5, angle);
	}
	
	public void fireCannonsLeft() {
		double angle = heading - Math.PI / 2;
		fireCannons(5, angle);
	}

	public void fireCannons(int numberOfCannons, double cannonballDirection) {
		for(int i = 0; i < numberOfCannons; i++) {
			int whichPartOfShip = (int)(Math.random() * height - height / 2);
			Point3D cannonballLocation = new Point3D(getCenterX() + whichPartOfShip * Math.cos(heading), getCenterY() + whichPartOfShip * Math.sin(heading));
			handler.addObject(new Cannonball(cannonballLocation, cannonballDirection));
			cannonballLocation.print();
			System.out.println("direction " + i + ": "+ cannonballDirection);
		}
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
		g.setColor(new Color(105, 62, 7));
		g.fillRect((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());
		g2d.rotate(-heading, getCenterX(), getCenterY());
		
		g.setColor(Color.red);
		g.fillRect((int)game.getCamera().getX() - 2, (int)game.getCamera().getY() - 2, 4, 4);
		g2d.fill(new Rectangle(game.getMouseX() - 2, game.getMouseY() - 2, 4, 4));
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
