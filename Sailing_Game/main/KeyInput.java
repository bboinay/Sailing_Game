package main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class KeyInput extends KeyAdapter{

	private Game game;
	private Handler handler;
	private HUD hud;
	
	private Player player;
	
	public boolean keys[] = new boolean[13];
	//keys 0 = true right
	//keys 1 = true left
	//keys 2 = true up
	//keys 3 = true down
	
	public boolean keyW, keyA, keyS, keyD, keySPACE, keySHIFT, keyR,
				   key1, key2, key3, key4, key5, keyG, keyE, keyQ, keyF;
	
	public KeyInput(Game game) {
		this.game = game;
		handler = game.getHandler();
		hud = game.getHud();
		
		player = handler.getPlayer();
		
		for(int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
	}
	
	public void keyPressed(KeyEvent e) {
		//ignores further keypresses if saving/loading
		//if(game.getState() == STATE.SaveLoad)return; 
		
		int key = e.getKeyCode();
		player = handler.getPlayer();
		
		if(key == KeyEvent.VK_D) 		{ keyD = true; }
		if(key == KeyEvent.VK_A) 		{ keyA = true; }
		if(key == KeyEvent.VK_W) 		{ keyW = true; }
		if(key == KeyEvent.VK_S) 		{ keyS = true; }
		
		if(key == KeyEvent.VK_SHIFT) 	{ keySHIFT = true; } 
		if(key == KeyEvent.VK_R) 		{ keyR = true; }
		if(key == KeyEvent.VK_1) 		{ key1 = true; }
		if(key == KeyEvent.VK_2) 		{ key2 = true; }
		if(key == KeyEvent.VK_3) 		{ key3 = true; }
		if(key == KeyEvent.VK_4) 		{ key4 = true; }
		if(key == KeyEvent.VK_G) 		{ keyG = true; }
		if(key == KeyEvent.VK_E) 		{ keyE = true; }
		if(key == KeyEvent.VK_Q) 		{ keyQ = true; }
		if(key == KeyEvent.VK_F) 		{ keyF = true; }
		
		if(key == KeyEvent.VK_ESCAPE) {
			//escape key action
		}
		
		if(key == KeyEvent.VK_Y) {
			//y key action
		}

		if(key == KeyEvent.VK_N) {
			//n key action
		}
		
			
		if(key == KeyEvent.VK_SPACE) { 
			//space bar action
		}
	}
	
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_D) 		{ keyD = false; }
		if(key == KeyEvent.VK_A) 		{ keyA = false; }
		if(key == KeyEvent.VK_W) 		{ keyW = false; }
		if(key == KeyEvent.VK_S) 		{ keyS = false; }
		if(key == KeyEvent.VK_SHIFT) 	{ keySHIFT = false; } 
		if(key == KeyEvent.VK_R) 		{ keyR = false; }
		if(key == KeyEvent.VK_1) 		{ key1 = false; }
		if(key == KeyEvent.VK_2) 		{ key2 = false; }
		if(key == KeyEvent.VK_3) 		{ key3 = false; }
		if(key == KeyEvent.VK_4) 		{ key4 = false; }
		if(key == KeyEvent.VK_G) 		{ keyG = false; }
		if(key == KeyEvent.VK_E) 		{ keyE = false; }
		if(key == KeyEvent.VK_Q) 		{ keyQ = false; }
		if(key == KeyEvent.VK_F) 		{ keyF = false; }
	}
}
