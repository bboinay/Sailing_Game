package com.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class HUD {

	private Game game;
	private Handler handler;
	private Player player;
	
	private DecimalFormat df;
	
	public double cash = 0.00;
	private int hudTimer = 0;
	
	private int messageX = Game.WIDTH / 2 - 170;
	private int messageY = Game.HEIGHT - 100;
	
	private WalkieTalkie walkie;
	
	private int borderLineThickness = 1;
	
	private int barLength = 200;
	private int barHeight = 15;
	private int barX = 14;
	private int healthBarY = 14;
	private int staminaBarY = healthBarY + barHeight - borderLineThickness;
	
	public BufferedImage[][] spriteSheet, spriteSheetMedium;
	public BufferedImage fragSprite, ammoSprite;
	public BufferedImage walkieSprite, walkieAlertSprite;
	
	public HUDObject playerHealthBar, playerStaminaBar, playerMagazineAmmoDisplay;
	
	public HUD(Game game) {
		this.game = game;
		handler = game.getHandler();
		player = handler.getPlayer();
		
		loadHUDSprites();
		
		playerHealthBar = new PlayerHealthBar(game);
		
		playerStaminaBar = new PlayerStaminaBar(game);
		playerStaminaBar.setY(playerHealthBar.getBottomEdgeY());
		
		playerMagazineAmmoDisplay = new PlayerMagazineAmmoDisplay(game);
		playerMagazineAmmoDisplay.setY(playerStaminaBar.getBottomEdgeY() + 5);
		
		
		
		
		df = new DecimalFormat("0.00");
		df.setRoundingMode(RoundingMode.DOWN);
	}
	
	private void loadHUDSprites() {
		SpriteLoader sl = new SpriteLoader();
		spriteSheet = sl.loadSprites("/hud_sprites_9x9.png", 1, 1, 1, 2, 9, 9);
		spriteSheetMedium = sl.loadSprites("/hud_sprites_18x18.png", 1, 1, 1, 2, 18, 18);
		
		walkieSprite = spriteSheetMedium[0][0];
		walkieAlertSprite = spriteSheetMedium[0][1];
		
		fragSprite = spriteSheet[0][0];
		ammoSprite = spriteSheet[0][1];
	}
	
	public void tick() {
		//timeSurvived += .05f;
		hudTimer++;
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g.setFont(new Font("Monospaced", 1, 12));
		
		player = handler.getPlayer();
		
		if(player.hasWalkieTalkie()) {
			walkie = player.getWalkieTalkie();
			walkie.render(g);
		}
		
		playerHealthBar.render(g);
		playerStaminaBar.render(g);
		
		playerMagazineAmmoDisplay.render(g);
			
		//kill count / accuracy mask
			int maskX = barX + barLength;
			int maskY = healthBarY;
			int maskWidth = 340;
			int maskHeight = barHeight + barHeight - borderLineThickness;
			int maskLine = 3;
			int maskLineWidth = 1;
			g.setColor(Color.black);
			g.fillRect(maskX, maskY, maskWidth, maskHeight);
			g.setColor(Color.white);
			g.fillRect(maskX + maskLine, maskY + maskLine, maskWidth - 2 * maskLine, maskHeight - 2 * maskLine);
			g.setColor(Color.black);
			g.fillRect(maskX + maskLine + maskLineWidth, maskY + maskLine + maskLineWidth, maskWidth - 2 * maskLine - 2 * maskLineWidth, maskHeight - 2 * maskLine - 2 * maskLineWidth);
			g.setColor(Color.white);
			int lineX = maskX + 115;
			g.fillRect(lineX, maskY + maskLine, maskLineWidth, maskHeight - 2 * maskLine);
			lineX = maskX + 167;
			g.fillRect(lineX, maskY + maskLine, maskLineWidth, maskHeight - 2 * maskLine);
			lineX = maskX + 250;
			g.fillRect(lineX, maskY + maskLine, maskLineWidth, maskHeight - 2 * maskLine);

		int statXAdd = 5;
		int statYAdd = 5;
		int statSpace = 12;
		
		//Zombie count and kill count
		g.drawString("Zombies killed: " + Integer.toString(handler.zombiesKilled), barX + barLength + maskLineWidth + statXAdd, staminaBarY - 1);
		
	//Accuracy	
		g.drawString("Accuracy %:     " + getAccuracy(), barX + barLength + maskLineWidth + statXAdd, staminaBarY + statSpace - 3);
		//System.out.println("# hit: " + (player.getShotsFired() - player.getShotsMissed()) + ", # fired: " + player.getShotsFired() + ", # missed: " + player.getShotsMissed());
		

		
		//HUD Timer	
			if(game.isDevMode())
				g.drawString("HUD Timer:   " + hudTimer / 1, barX + barLength + maskLineWidth + statXAdd, staminaBarY + 2 * statSpace - 3);

		//Cash
			g.setColor(Color.white);
			g.drawString("$" + df.format(cash), maskX + 175, playerMagazineAmmoDisplay.getY() - 15);    //ax + 7 * (bulletWidth + bulletSpacingX) + 130 - (df.format(cash).length() + 1) * 6, ay - 30);
		
		//count of ammo in inv
			int ammoInfoX = maskX + 272; //ax + Math.min(player.getWep().getMagazineSize(), bulletsPerRow) * bulletSpacingX + 15;
			int ammoInfoY = playerMagazineAmmoDisplay.getY() - 15;
			int yspace = 15;
			g.setColor(Color.white);
			g.drawString(Integer.toString(player.getBulletsInInventory()), ammoInfoX, ammoInfoY);
			g2d.drawImage(ammoSprite, ammoInfoX - 11, ammoInfoY - 9, game);
			
		//count of frags in inv
			//int fragInfoX = ax + Math.min(player.getWep().getMagazineSize(), bulletsPerRow) * bulletSpacingX + 55;
			int fragInfoX = ammoInfoX + 42;
			int fragInfoY = ammoInfoY;
			
			g.drawString(Integer.toString(player.fragsInInv), fragInfoX, fragInfoY);
			g2d.drawImage(fragSprite, fragInfoX  - 11, fragInfoY - 9, game);
			
		//walkie talkie sprite	
			if(player.hasWalkieTalkie()) {
				g2d.drawImage(walkieSprite, fragInfoX - 11 + 45, fragInfoY - 18, 25, 25, game);
			} 
			
		//Shop keybind hint
			//g.setFont(new Font("arial", 1, 12));
			//g.drawString("Press 'SPACE' to visit shop", 14, 62);
		//continue
			
		//interact
		renderInteractionsInPlayerRange(g);
	}
	
	private void renderInteractionsInPlayerRange(Graphics g) {
		if(player.interactRange()) {
			g.setFont(new Font("Monospaced", 1, 24));
			g.setColor(Color.black);
			g.drawString("Press 'Q' to interact", messageX, messageY);
		}
	}
	
	private String getAccuracy() {
		if(player.getShotsFired() == 0) return df.format(0);
		double d = 100 * (double)(player.getShotsFired() - player.getShotsMissed()) / player.getShotsFired();
		return df.format(d);
	}

	public double 	getCash() {return cash;}
	public void 	setCash(double d) {cash = d;}
	
	public int 		getHudTimer() {return hudTimer;}
	public void 	setHudTimer(int hudTimer) {this.hudTimer = hudTimer;}
}
