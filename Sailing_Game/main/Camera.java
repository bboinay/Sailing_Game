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

	public void moveScreenIfMouseAtEdge(){
		int mx = game.getScreenMouseX();
		int my = game.getScreenMouseY();
		
		System.out.println("mx: " + mx + ", my: " + my + ", distToBorder: " + distanceToBorder(mx, my));
		System.out.println("g.WIDTH: " + Game.WIDTH + ", g.HEIGHT: " + Game.HEIGHT);
		System.out.println("x: " + x + ", y: " + y);
		
		double screenMovementHitboxThickness = 50;
		if(distanceToBorder(mx, my) < screenMovementHitboxThickness) {
			double directionToMoveScreen = Math.atan2((my - Game.MIDSCREENY), (mx - Game.MIDSCREENX));
			double newVelocityMagnitude = (screenMovementHitboxThickness - distanceToBorder(mx, my)) * maxVelocity;
			cameraVelocity = new Vector(directionToMoveScreen, newVelocityMagnitude);
			cameraVelocity.setMagnitude(newVelocityMagnitude);
			
		} else
			cameraVelocity = new Vector();
	}	
	
	public double distanceToBorder(int mx, int my) {
		
		double distToTop = my;
		double distToBottom = Game.HEIGHT - my;
		double distToLeft = mx; 
		double distToRight = Game.WIDTH - mx;
		
		return Game.min(Game.min(distToTop, distToBottom), Game.min(distToLeft, distToRight));
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
