package com.main;

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
		//System.out.println("gamestate: " + game.gameState);
		
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
		if(key == KeyEvent.VK_F) 		{ keyF = true; }
		
		/*
		//weapon selection button presses
		if(game.getState() == STATE.Game) {
			if(key == KeyEvent.VK_1) {
				keyR = true;
			} else if(key == KeyEvent.VK_2) {
				if(player.getBulletsInInv() > 0 || player.getBulletsInMag() > 0)	
					player.wep = WEP.SMG;
			} else if(key == KeyEvent.VK_G) {
				player.setReloading(false);
				if(player.fragsInInv > 0)
					player.wep = WEP.Frag;
			}
		}*/
		
		if(key == KeyEvent.VK_Q) {
			
			if(player.interactRange() && player.getCurrHitbox().getName().equals("walkieTalkie")) {
				player.giveWalkieTalkie();
			}
		}

		if(key == KeyEvent.VK_V) {
			//System.out.println(player.x + " " + player.y);
			//player.fireAction("pistol_shot");
			
			//game.printBooleanArray(player.getGunsBought());
			//System.out.println(game.isDemo());
			
		}
		//System.out.println("mouseX: " + game.getMouseX() + ", mouseY: " + game.getMouseY());
		//System.out.println("screenMouseX: " + game.getScreenMouseX() + ", screenMouseY: " + game.getScreenMouseY());
			
		if(key == KeyEvent.VK_ESCAPE) {
			if(game.getState() == STATE.Settings || game.getState() == STATE.Tutorial)
				game.setState(STATE.Menu);
			else if(game.getState() == STATE.Shop)
				game.setState(STATE.Game);
			else if(game.getState() == STATE.SaveScreen || game.getState() == STATE.Game)
				game.setState(STATE.Shop);
			else if(game.getState() == STATE.Menu)
				game.setState(STATE.QuitConfirm);
		}
		
		if(key == KeyEvent.VK_Y) {
			if(game.getState() == STATE.QuitConfirm)
				System.exit(1);
		}

		if(key == KeyEvent.VK_N) {
			if(game.getState() == STATE.QuitConfirm)
				game.setState(STATE.Menu);
		}
		
		if(!game.isDemo()) {
			if(key == KeyEvent.VK_M) {
				hud.cash = (hud.cash + 1) * 2;
			}
			
			if(key == KeyEvent.VK_Z) {
				double rTheta = Math.random() * 2 * Math.PI;
				int tx = (int)(200 * Math.cos(rTheta));
				int ty = (int)(200 * Math.sin(rTheta));
				//System.out.println("rtheta: " + rTheta + ", tx: " + tx + ", ty: " + ty);
				handler.addObject(new Zombie(new Point3D(tx + player.getX(), ty + player.getY()), game));
			}
			
			if(key == KeyEvent.VK_X) {
				player.setFragsInInventory(player.getFragsInInventory() + 1);
			}
		}
			
		if(key == KeyEvent.VK_SPACE) { 
			if(game.getState() == STATE.Game) {
				game.setState(STATE.Shop); 
				AudioPlayer.getMusic("forest_ambiance").pause();
			} else if(game.getState() == STATE.Shop) {
				game.setState(STATE.Game);
				AudioPlayer.getMusic("forest_ambiance").loop(1f, Game.MAINVOL);
			}
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
		if(key == KeyEvent.VK_F) 		{ keyF = false; }
	}
}
