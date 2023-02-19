package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Ship extends GameObject{
	
	public boolean selected = true;

	public double rudderPosition = 0; //-100 full left, +100 full right
	public double minRudderPosition = -100;
	public double maxRudderPosition = 100;
	public double rudderSpeed = .5; //how quickly rudderPosition increases or decreases
	
	public double windInSails = 0;
	public double maxWindInSails = 100;
	public double windInSailsIncrement = 2;
	
	public double turnRate;
	public double maxTurnRate = .005;

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
		id = ID.Ship;
		
		height = 32;
		width = 64;
		
		this.game = game;
		handler = game.getHandler();
		kInput = game.getKeyInput();
		mInput = game.getMouseInput();
		
		maxAcceleration = .01;
		maxVelocity = .2;
		
	}

	public void tick() {
		keyInputCheck();
		mouseInputCheck();
		//printGameObjectVectors();
		//printShipInformation();
		
		//clampMovement();
		
		turn();
		advance();
	}
	
	//finds turnRate from rudderPosition, updates heading and velocity accordingly
	public void turn() {
		setTurnRateFromRudderPosition();
		heading += turnRate;
		velocity.turn(turnRate);
	}
	
	//links rudderPosition to turnRate 
	public void setTurnRateFromRudderPosition() {
		turnRate = rudderPosition / maxRudderPosition * maxTurnRate;
	}
	
	//sets velocity and moves location accordingly
	public void advance() {
		setVelocityFromWindInSails();
		location.addVector(velocity);
	}
	
	//this method links windInSails to boat's velocity
	public void setVelocityFromWindInSails() {
		double newVelocityMag = windInSails / maxWindInSails * maxVelocity;
		if(velocity.magnitude() == 0) {
			velocity = new Vector(heading, newVelocityMag);
		} else
			velocity.setMagnitude(windInSails / maxWindInSails * maxVelocity);
	}

	public void keyInputCheck() {
		//handling left-right inputs
		if(kInput.keyA && !kInput.keyD)
			rudderLeft();
		else if(!kInput.keyA && kInput.keyD)
			rudderRight();
		
		//handling up-down inputs
		if(kInput.keyW && !kInput.keyS)
			increaseWind();
		else 
			decreaseWind();
		
		//handling cannon inputs
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
		mInput.tick();
		int mx = game.getMouseX();
		int my = game.getMouseY();
		/*
		Rectangle tempMouse = new Rectangle(mx - 2, my - 2, 4, 4);
		if(mInput.getSingleClicked() && tempMouse.intersects(getBounds())) {
			selected = true;
		} else if(mInput.getSingleClicked() && !tempMouse.intersects(getBounds())) {
			selected = false;
		}
		*/
			
	}
	
	public void rudderLeft() {
		rudderPosition -= rudderSpeed;
		clampRudder();
	}
	
	public void rudderRight() {
		rudderPosition += rudderSpeed;
		clampRudder();
	}
	
	//makes sure rudder stays within its limits (min/maxRudderPosition)
	public void clampRudder() {
		rudderPosition = Game.clamp(rudderPosition, minRudderPosition, maxRudderPosition);
	}

	public void increaseWind() {
		windInSails += windInSailsIncrement;
		clampWindInSails();
	}
	
	public void decreaseWind() {
		windInSails -= windInSailsIncrement / 20.0;
		clampWindInSails();
	}
	
	//makes sure wind amount stays within its limits (0/maxWindInSails)
	public void clampWindInSails() {
		windInSails = Game.clamp(windInSails, 0, maxWindInSails);
	}

	public void fireCannons(int numberOfCannons, double cannonballDirection) {
		for(int i = 0; i < numberOfCannons; i++) {
			int whichPartOfShip = (int)(Math.random() * height - height / 2);
			Point3D cannonballLocation = new Point3D(getCenterX() + whichPartOfShip * Math.cos(heading), getCenterY() + whichPartOfShip * Math.sin(heading));
			handler.addObject(new Cannonball(cannonballLocation, cannonballDirection));
		}
	}
	
	public void fireCannonsRight() {
		double angle = heading + Math.PI / 2;
		fireCannons(5, angle);
	}
	
	public void fireCannonsLeft() {
		double angle = heading - Math.PI / 2;
		fireCannons(5, angle);
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
		System.out.println("rudderPosition: " + rudderPosition + ", windInSails: " + windInSails);
		System.out.println("turnRate: " + turnRate);
		System.out.println("radialDrag: " + radialDrag + ", straightLineDrag: " + straightLineDrag);
	}
}
