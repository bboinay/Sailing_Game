package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseInput extends MouseAdapter{

	private boolean clicked = false;
	private int clickTimer = 0;
	
	public MouseInput(){
	}
	
	public void tick() {
		if(clicked) 
			clickTimer++;
		
	}
	
	public void mousePressed(MouseEvent e) {
		clicked = true;
	}
	
	public void mouseReleased(MouseEvent e) {
		clicked = false;
		clickTimer = 0;
	}
	
	public boolean getClicked() {
		return clicked;
	}
	
	public int getClickTimer() {
		return clickTimer;
	}
	
}

