package main;


public class Camera {

	private double x, y;
	private Game game;
	private Player player = null;
	
	public Camera(Game game) {
		this.game = game;
		this.x = 0;
		this.y = 0;
	}
	
	
	public void tick() {
		if(player == null)
			player = game.getHandler().getPlayer();
		else {
			x = player.getX() - Game.MIDSCREENX;
			y = player.getY()- Game.MIDSCREENY;
		}
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
