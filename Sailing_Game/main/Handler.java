package main;

import java.awt.Graphics;
import java.util.LinkedList;

public class Handler {

	public LinkedList<GameObject> object = new LinkedList<GameObject>();
	private Game game;
	private Camera cam; //ignore all cam stuff for now
	private double renderDistance = Game.WIDTH * 1.2f;
	
	public Handler(Game game) {
		this.game = game;
		cam = game.getCamera(); //ignore all cam stuff for now
	}
	
	public void tick() {
		for(int i = 0; i < object.size(); i++) {
			object.get(i).tick();
		}
	}
	

	public void render(Graphics g) {
		//ignore all cam stuff for now
		if(cam == null) {
			cam = game.getCamera();
			return;
		}
		
		for(int i = 0; i < object.size(); i++) {
			GameObject temp = object.get(i);
			if(Game.magnitude(temp.getX() - cam.getX(), temp.getY() - cam.getY()) < renderDistance)
				temp.render(g);
		}
	}
	
	public void addObject(GameObject obj) {
		if(object.size() == 0) object.add(obj);
		
		for(int i = 0; i < object.size(); i++) {
			if(object.get(i).priority < obj.priority) {
				object.add(i, obj);
				return;
			}
		}
		object.add(object.size() - 1, obj);
	}
	
	public void removeObject(GameObject obj) {
		if(obj == null) return;
		object.remove(obj);
	}
	
	public void clear() {
		object.clear();
	}
	
	public void print() {
		for(int i = 0; i < object.size(); i++) {
			System.out.println(object.get(i).getId() + " ");
		}
	}
	
	public Game getGame() {
		return game;
	}
}
