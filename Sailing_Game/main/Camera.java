package main;


public class Camera {

	private double x, y, maxVelocity;
	private Vector cameraVelocity;

	private Game game;
	
	public Camera(Game game) {
		this.game = game;
		this.x = 0;
		this.y = 0;
		
		cameraVelocity = new Vector();
		maxVelocity = .1;
	}
	
	
	public void tick() {
		x += cameraVelocity.getXComponent();
		y += cameraVelocity.getYComponent();
	}

	public double getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
