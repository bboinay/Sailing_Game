package com.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Player extends GameObject{

	public int maxPlayerRenderHeight = Game.HEIGHT - 68;
	public int maxPlayerRenderWidth = Game.WIDTH - 46;
	
	public boolean dead = false;
	public double lastStepX, lastStepY;
	public int stepSwitch = 1;
	public double stepDistance = 45;
	
	public double originalMaxVelocity = 1.0f;
	public double sprintVelocity = 1.6f;
	public double originalSprintVelocity = sprintVelocity;
	
	public double originalMaxAcceleration = .04f;
	public double sprintAcceleration = .07f;
	
	public double maxDeceleration = 0.1f;
	public double brakeFactor = 0.94f;
	
	public double biteDamage = 40;
	
	public double minStamina = -20f;
	public double maxStamina = 100f;
	public double swingStamina = 20f;
	public double stamina = maxStamina;
	public double staminaDrain = .3f;
	public double staminaRefill = .1f;

	private double ogCashPerZombie = 38.25;
	private double cashEarnedPerZombie = ogCashPerZombie;
	
	
	private int shotsFired = 0;
	private int shotsMissed = 0;
	
	private double flakResistance = 0;
	
	//value from 0 to 1, 1 being most stable and 0 being least
	public double shootingStability = 1f;
	public int timeTilFocused = 50;
	public int timeStill = 0;
	public double aimConeMod = 1; 
	private double headshotDamage;
	private double maxHeadShotDamage = 400;
	private boolean headshotsAllowed = false;
	
	private double reloadMaxVelocity = 0.35f;
	private int bulletsInInventory = 18;
	private boolean reloadingSingles = false;
	private boolean reloadingClip = false;
	private boolean reloadingClipLastTick = false;
	private double reloadTimeWeight = 1;
	private int reloadTimer = 0;
	private boolean canReloadFullClips = false;
	private boolean reloadSoundPlayed = false;
	
	private int swingRange = 40;
	private int swingSize = 11;
	private boolean swinging = false;
	private boolean lastTickSwinging = swinging;
	
	private float fragSpawnChance = 0.30f;
	private int ammoPickupAmount = 28;
	public int fragsInInv = 10;
	private double fragZombieDamagePerVel = 4;
	
	public boolean[] gunsBought = {false, false, false, false};
								//	 SMG  shotgun   AR    axe
	private boolean SMGUnlocked() { return gunsBought[0]; }
	private boolean shotgunUnlocked() { return gunsBought[1]; }
	private boolean ARUnlocked() { return gunsBought[2]; }
	private boolean axeUnlocked() { return gunsBought[3]; }
	
	public boolean pinPulled = false;
	public int pinTime = 25;
	public int pinTimer = 0;
	
	private boolean hasWalkieTalkie = false;
	private WalkieTalkie walkie;
	private boolean interactRange = false;
	private CustomHitbox currHitbox;
	private int hitboxesIn = 0;
	
	private boolean triggerPulled = false;
	
	//upgrades?
	private boolean boxButton = true;
	private boolean placeBoxMode = false;
	private boolean hasLaserSight = true;
	
	private double direction;
	
	int minPlayerX, maxPlayerX, minPlayerY, maxPlayerY;
	
	private KeyInput kInput;
	private MouseInput mInput;
	private Handler handler;
	private Camera cam;
	private Game game;
	private SpriteLoader sl;
	private Shop shop;
	private PlayerUpgradeManager upgradeManager;
	
	public Pistol pistol;
	public Frag frag;
	public SMG smg;
	public Shotgun shotgun;
	public AR ar;
	public Axe axe;
	public Box box;
	
	public RangedWeapon currentWeapon = pistol;
	public RangedWeapon lastWeapon = currentWeapon;
	
	public BufferedImage[][] spriteSheet;
	public BufferedImage walkingSprite, pistolSprite, fragSprite, SMGSprite, shotgunSprite, ARSprite, axeSprite, axeDownSprite;
	
	//shop storage could be optimized a lot i think but thats for another day
	
	public double priority = 200;
	
	public Player(Point3D origin, Game game) {
		super(origin);
		setId(ID.Player);
		
		width = 32;
		height = 32;
		health = 1000f;
		maxHealth = health;
		
		this.game = game;
		handler = game.getHandler();
		upgradeManager = new PlayerUpgradeManager(this);

		setUpPlayerSprites();
		setUpPlayerEquipables();
		
		setMaxVelocityAndMaxAccelerationToNormalValues();
		
		currentWeapon = pistol;
	}
	
	public int getShopSize() {
		return game.getShop().getShopSize();
	}
	
	public void setUpPlayerSprites(){
		sl = new SpriteLoader();
		spriteSheet = sl.loadSprites("/player_sprites_32x48.png", 1, 1, 1, 8, 32, 48);
		walkingSprite = spriteSheet[0][0];
		fragSprite = spriteSheet[0][2];
		axeDownSprite = spriteSheet[0][7];
	}
	
	public void setUpPlayerEquipables() {
		pistol = new Pistol(game);
		frag = new Frag(game);
		smg = new SMG(game);
		shotgun = new Shotgun(game);
		ar = new AR(game);
		axe = new Axe(game);
		box = new Box(game);
	}
	
	public void tick() {
		// 0. check if receiving damage
		if(justDied())
			cryOutAndAcceptDeath();
		if(dead) return;
		
		//1. check for keyboard input and adjust acceleration to intended values
		keyInputCheck();

		//2. check for collisions and restrict acceleration accordingly 
		Collision(); 
		
		//3. check for mouse input and operate equipables accordingly
		mouseInputCheck();
		
		//4. tick any tickable items carried by player
		if(hasWalkieTalkie)
			walkie.tick();
		
		if(pinPulled) {
			throwGrenadeTick();
		}
		
		currentWeapon.meleeTick();
		
		if(stamina <= swingStamina)
			swinging = false;
		lastTickSwinging = swinging;
		
		//5. update velocity and position
		move();
		if(movedEnoughToTakeAStep()) {
			playSteppingSound();
			switchFeet();
		}
		clampPlayerMovement();
		
		//6. anything else?
		updateShootingStability();
		
	}
	
	public void 	setSwinging(boolean b) {swinging = b;}
	public boolean 	isSwinging() {return swinging;}
	
	public void setHealth(double newHealth) {
		health = newHealth;
	}
	
	public void cryOutAndAcceptDeath() {
		AudioPlayer.getSound("player_death").play(1f, Game.MAINVOL);
		dead = true;
		game.setState(STATE.GameOver);
	}
	
	public boolean justDied() {
		clampHealth();
		return (health == 0 && !dead);
	}
	
	public boolean movedEnoughToTakeAStep() {
		return Game.magnitude(getX() - lastStepX, getY() - lastStepY) > stepDistance;
	}
	
	public void playSteppingSound() {
		float stepVolume = game.randomFloatInRange(.9f, 1.1f);
		if(stepSwitch < 0)
			AudioPlayer.getSound("walk_sound1").play(1, stepVolume * Game.MAINVOL);
		else
			AudioPlayer.getSound("walk_sound2").play(1, stepVolume * Game.MAINVOL);
	}
	
	public void switchFeet() {
		stepSwitch *= -1;
		updateLastStepXAndY();
	}

	public void updateLastStepXAndY() {
		lastStepX = getX();
		lastStepY = getY();
	}
	
	public void loadPlayerAmmo(int pistolAmmo, int SMGAmmo, int shotgunAmmo, int ARAmmo) {
		if(pistolAmmo > pistol.getMagazineSize()) {
			pistol.setMagazineSize(11);
		}
		pistol.setBulletsInMag(pistolAmmo);
		smg.setBulletsInMag(SMGAmmo);
		shotgun.setBulletsInMag(shotgunAmmo);
		ar.setBulletsInMag(ARAmmo);
	}
	
	public Rectangle swingHitbox() {
		double angle = Math.atan2(game.getMouseY() - getY() - 12, game.getMouseX() - getX() - 12);
		
		int bx = (int) (12 + getX() + swingRange * Math.cos(angle)) - swingSize / 2;
		int by = (int) (12 + getY() + swingRange * Math.sin(angle)) - swingSize / 2;
		
		return new Rectangle(bx, by, swingSize, swingSize);
	}
	
	private void Collision() {
		GameObject collisionObject;
		interactRange = false;
		
		//iterates through all game objects
		for(int i = handler.object.size() - 1; i >= 0; i--) {
			collisionObject = handler.object.get(i);
			
			if(collisionObject.id == ID.CustomHitbox) {
				CustomHitbox customHitbox = (CustomHitbox)collisionObject;
				if(customHitbox.getName().equals("walkieTalkie")) {
					currHitbox = customHitbox;
					if(collisionObject.getBounds().intersects(getBounds()))
						interactRange = true;
				}
			}
			
			if(collisionObject.id == ID.Block) {
				Block block = (Block)collisionObject;
				block.checkIfHitByAxe();
				playerBlockCollision(block);
			}
			
			if(collisionObject.id == ID.Zombie) {
				Zombie zombie = (Zombie)collisionObject;
				zombie.checkIfHitByAxe();
			}
		}
	}
	
	public boolean axeSwingComplete() {
		if(currentWeapon != axe) return false;
		return (currentWeapon.getSwingTimer() == currentWeapon.getSwingTime());
	}
	
	public void playerBlockCollision(Block block) {
		if(horizontalMovingBounds().intersects(block.getBounds()) || verticalMovingBounds().intersects(block.getBounds())){
			int px = (int)Math.round(getX() + width / 2f);
			int py = (int)Math.round(getY() + width / 2f);
			
			int bx = (int)Math.round(block.getX() + block.width / 2f);
			int by = (int)Math.round(block.getY() + block.height / 2f); 
			
			int dx = px - bx;
			int dy = by - py; //flipped so that it matches regular orientation of coordinate plane
			
			int blockMinX = (int)Math.round(block.getX());
			int blockMaxX = (int)Math.round(block.getX() + block.width);
			int blockMinY = (int)Math.round(block.getY());
			int blockMaxY = (int)Math.round(block.getY() + block.height);
			
			//if player above block
			if(dy > 0) {
				//if player more upwards than to the right or left
				if(Math.abs(dy) > Math.abs(dx)) {
					//move player to top and zero velocity.getYComponent()
					setY(blockMinY - height);
					if(velocity.getYComponent() > 0) velocity.setYComponent(0);
					if(acceleration.getYComponent() > 0) acceleration.setYComponent(0);
				}
				//else if player to right of block
				else if(dx > 0) {
					//move player to right and zero velocity.getXComponent()
					setX(blockMaxX);
					if(velocity.getXComponent() < 0) velocity.setXComponent(0);
					if(acceleration.getXComponent() < 0) acceleration.setXComponent(0);
				} 
				//else if the player is to left of block
				else {
					//move player to left and zero velocity.getXComponent()
					setX(blockMinX - width);
					if(velocity.getXComponent() > 0) velocity.setXComponent(0);
					if(acceleration.getXComponent() > 0) acceleration.setXComponent(0);
				}
			} 
			//if player is below block
			else {
				//if player more below than to right or left
				if(Math.abs(dy) > Math.abs(dx)) {
					//move player to bottom and zero velocity.getYComponent()
					setY(blockMaxY);
					if(velocity.getYComponent() < 0) velocity.setYComponent(0);
					if(acceleration.getYComponent() < 0) acceleration.setYComponent(0);
				}
				//else if player to right of block
				else if(dx > 0) {
					//move player to right and zero velocity.getXComponent()
					setX(blockMaxX);
					if(velocity.getXComponent() < 0) velocity.setXComponent(0);
					if(acceleration.getXComponent() < 0) acceleration.setXComponent(0);
				} 
				//else if the player is to left of block
				else {
					//move player to left and zero velocity.getXComponent()
					setX(blockMinX - width);
					if(velocity.getXComponent() > 0) velocity.setXComponent(0);
					if(acceleration.getXComponent() > 0) acceleration.setXComponent(0);
				}
			}
		}
	} 
	
	public void throwGrenadeTick() {
		pinTimer++;
		
		if(pinTimer >= pinTime) {
			AudioPlayer.getSound("grenade_throw").play(1, .5f * Game.MAINVOL);
			
			GameObject tempFrag = new ThrownFrag(new Point3D(getX() + 16 - 4, getY() + 16 - 4), game);
			
			handler.addObject(tempFrag);
			fragsInInv--;
			pinTimer = 0;
			pinPulled = false;
		}
	}
	
	public double getProjectileStabilityVariation() {
		return ((Math.random() - .5) * (1 - shootingStability) * currentWeapon.getAimCone() * aimConeMod * Math.PI / 180);
	}
	
	private void mouseInputCheck() {
		mInput = game.getMouseInput();
		
		if(!mInput.getClicked()){
			triggerPulled = false;
			return;
		}
		
		if(game.gameState != STATE.Game || dead) return;
		
		if(placeBoxMode) {
			lastWeapon = currentWeapon;
			currentWeapon = box;
		}
		
		//semi auto actions
		if(!triggerPulled) {
			triggerPulled = true;
		
			//pistol & shotgun click action
			if(!currentWeapon.isAutomatic()) {
				currentWeapon.triggerPull();
			} 
			
			//automatic dry fire action
			if(currentWeapon.isAutomatic()) {
				if(currentWeapon.getBulletsInMag() == 0) {
					currentWeapon.dryFireAction();
				}
			} 
			
			//grenade click action
			if(currentWeapon == frag) {
				if(fragsInInv > 0) {
					if(!pinPulled) {
						AudioPlayer.getSound("grenade_pin").play(1f, Game.MAINVOL);
						pinPulled = true;
					}
				}
			}
			
			//axe swing
			if(currentWeapon == axe) {
				if(!swinging) {
					swinging = true;
				} 
			}
			
			//box place action
			if(placeBoxMode) {
				if(Game.magnitude(game.getMouseX() - getX(), game.getMouseY() - getY()) < swingRange * 4) {
					handler.addObject(new Block(new Point3D((double)(game.getMouseX() - 32), (double)(game.getMouseY() - 32)), game));
					placeBoxMode = false;
					currentWeapon = lastWeapon;
				}
			}
		}
		//automatic fire actions
		else if(triggerPulled) {
			if(currentWeapon.isAutomatic()) {
				//separate into own fire() command soon
				if(currentWeapon.shotTimerComplete()) {
					currentWeapon.setShotTimer(0);
					if(currentWeapon.getBulletsInMag() > 0 && !reloading()) {
						currentWeapon.fireAction();
					} 
				} else
					currentWeapon.increaseShotTimer();
			}	
		}
	}
	
	public boolean interactRange() {
		return interactRange;
	}
	
	//establishes accelerations and velocities depending on current key input
	private void keyInputCheck() {

		kInput = game.getKeyInput();
		
		//SHIFT key action
		if(kInput.keySHIFT && hasStamina()) {
			cancelReload();
			increaseMaxVelocityAndMaxAccelerationToSprintValues();
			if(isMoving()) {
				decreaseStamina();
				giveStaminaPenaltyIfOutOfStamina();
			} else {
				increaseStamina();
			}
		} 
		//SHIFT released action
		else { 
			setMaxVelocityAndMaxAccelerationToNormalValues();
			increaseStamina();
		}
		
		
		//sets accX according to A and/or D button presses
		if(kInput.keyD && !kInput.keyA) {//if right and not left
			acceleration.setXComponent(maxAcceleration);
		} else if(kInput.keyA && !kInput.keyD) //if left and not right
			acceleration.setXComponent(-maxAcceleration);
		else 
			acceleration.setXComponent(0);
		
		//sets accY according to W and/or S button presses
		if(kInput.keyW && !kInput.keyS) { //if up and not down
			acceleration.setYComponent(-maxAcceleration);
		} else if(kInput.keyS && !kInput.keyW) //if down and not up
			acceleration.setYComponent(maxAcceleration);
		else 
			acceleration.setYComponent(0);
		
		//if not left or right, decelerate
		if(!kInput.keyD && !kInput.keyA) {
			velocity.multiplyXComponentBy(brakeFactor);
			if(Math.abs((double)velocity.getXComponent()) < 0.05) {
				acceleration.setXComponent(0);
			}
		}
		
		//if not up or down, decelerate
		if(!kInput.keyW && !kInput.keyS) {
			velocity.multiplyYComponentBy(brakeFactor);
			if(Math.abs((double)velocity.getYComponent()) < 0.05) {
				velocity.setYComponent(0);
			}
		}
		
		//R key action
		if(kInput.keyR || reloadingSingles) {
			reloadSinglesAction();
		}
		
		//F key action
		if(kInput.keyF || reloadingClip) {
			if(!canReloadFullClips || !currentWeapon.canReloadWithClip()) {
				reloadSinglesAction();
			} else 
				reloadClipAction();
		}
		
		handleWeaponSelectInput();
	}
	
	private void reloadSinglesAction() {
		if(haveBulletsInInventory() && !currentWeaponMagazineFull()) {
			reloadingSingles = true;
			sprite = walkingSprite;
			maxVelocity = reloadMaxVelocity;
			
			if(reloadTimer >= currentWeapon.getReloadTime() * reloadTimeWeight) {
				reloadTimer = 0;
				currentWeapon.makeSingleReloadSound();
				bulletsInInventory--;
				currentWeapon.increaseBulletsInMagazine();
			} else {
				reloadTimer++;
			}
			
		} else {
			reloadingSingles = false;
		}
	}
	
	private void reloadClipAction() {
		if(bulletsInInventory >= currentWeapon.getMagazineSize() && currentWeapon.getBulletsInMag() != currentWeapon.getMagazineSize()){
			if(!reloadingClip)
				AudioPlayer.getSound(currentWeapon.getReloadClipSound()).play(1f, .15f * (float)currentWeapon.getReloadClipSoundBooster() * Game.MAINVOL);
			reloadingClip = true;
			sprite = walkingSprite;
			maxVelocity = reloadMaxVelocity;
			if(reloadTimer >= currentWeapon.getClipReloadTime() * reloadTimeWeight) {
				reloadTimer = 0;
				bulletsInInventory -= currentWeapon.getMagazineSize();
				currentWeapon.setBulletsInMag(currentWeapon.getMagazineSize());
			} else
				reloadTimer++;
		} else
			reloadingClip = false;
		
		reloadingClipLastTick = reloadingClip;
	}
	
	public boolean hasStaminaForSwing() {
		return (stamina > swingStamina);
	}
	
	private void decreaseStamina() {
		stamina -= staminaDrain;
		stamina = Game.clamp(stamina, minStamina, maxStamina);
	}
	
	public void decreaseStaminaBy(double d) {
		stamina -= d;
		stamina = Game.clamp(stamina, minStamina, maxStamina);
	}
	
	private void increaseStamina() {
		stamina += staminaRefill;
	}
	
	public void giveStaminaPenaltyIfOutOfStamina() {
		if(stamina <= 0) stamina = minStamina;
	}
	
	public void increaseMaxVelocityAndMaxAccelerationToSprintValues() {
		maxVelocity = sprintVelocity;
		maxAcceleration = sprintAcceleration;
	}
	
	public void setMaxVelocityAndMaxAccelerationToNormalValues() {
		maxVelocity = originalMaxVelocity;
		maxAcceleration = originalMaxAcceleration;
	}
	
	public double getSwingStamina() {return swingStamina;}
	
	public boolean hasStamina() {
		return getStamina() > 0;
	}
	
	public void handleWeaponSelectInput() {
		if(kInput.key1) { 
			cancelReload();
			currentWeapon = pistol;
		} 
		if(kInput.key2 && SMGUnlocked()) { 
			cancelReload();
			currentWeapon = smg;
		} 
		if(kInput.key3 && shotgunUnlocked()) { 
			cancelReload();
			currentWeapon = shotgun;
		} 
		if(kInput.key4 && ARUnlocked()) {
			cancelReload();
			currentWeapon = ar;
		} 
		if(kInput.keyG) {
			cancelReload();
			if(fragsInInv > 0)
				currentWeapon = frag;
		} 
		if(kInput.keyE && axeUnlocked()) {
			cancelReload();
			currentWeapon = axe;
		}
	}
	
	public boolean haveBulletsInInventory() {
		return bulletsInInventory > 0;
	}
	
	public boolean currentWeaponMagazineFull() {
		return currentWeapon.getBulletsInMag() == currentWeapon.getMagazineSize();
	}
	
	public void cancelReload() {
		//if(reloadingClip && currentWeapon.canReloadWithClip()) 
		if(AudioPlayer.getSound(currentWeapon.getReloadClipSound()) != null)
			AudioPlayer.getSound(currentWeapon.getReloadClipSound()).stop();
		
		ignoreCurrentReloadInputs();
		reloadTimer = 0;
		
		reloadingSingles = false;
		reloadingClip = false;
	}
	
	private void ignoreCurrentReloadInputs() {
		kInput.keyR = false;
		kInput.keyF = false;
	}
	
	private void clampPlayerMovement() {
		stopPlayerIfMovingOffScreen();
		movePlayerBackOnScreen();
	}
	
	//clamps x and y within game borders	
	private void movePlayerBackOnScreen() {
		minPlayerX = -game.getMapHandler().getBaseMap().getWidth() / 2;
		maxPlayerX = (int) (-minPlayerX + width);
		minPlayerY = -game.getMapHandler().getBaseMap().getHeight() / 2;
		maxPlayerY = (int) (-minPlayerY - height);
		
		setX(Game.clamp(getX(), minPlayerX, maxPlayerX));
		setY(Game.clamp(getY(), minPlayerY, maxPlayerY));
	}
	
	//zeros velocity.getXComponent() and velocity.getYComponent() if at edge 
	private void stopPlayerIfMovingOffScreen() {
		if(getX() <= minPlayerX && velocity.getXComponent() < 0) velocity.setXComponent(0);
		if(getY() <= minPlayerY && velocity.getYComponent() < 0) velocity.setYComponent(0);
		if(getX() >= maxPlayerX && velocity.getXComponent() > 0) velocity.setXComponent(0);
		if(getY() >= maxPlayerY && velocity.getYComponent() > 0) velocity.setYComponent(0);
	}
	
	
	
	

	public void render(Graphics g) {
		if(hasWalkieTalkie)
			walkie.render(g);
		
		if(reloading())
			sprite = walkingSprite;
		else
			sprite = currentWeapon.getEquippedSprite();
		
		if(currentWeapon.getWeaponId() == WEP.Frag) 
			setSpriteForFrag();
		
		if(placeBoxMode) 
			renderPlaceableBox(g);

		renderPlayerCharacter(g);
		//testRenderTheseBounds(g, swingHitbox());
	}
	
	
	
	public void setSpriteForFrag() {
		if(fragsInInv > 0)
			sprite = fragSprite;
		else
			sprite = walkingSprite;
	}
	
	public void renderPlaceableBox(Graphics g) {
		sprite = walkingSprite;
		Color temp = g.getColor();
		if(Game.magnitude(game.getMouseX() - getX(), game.getMouseY() - getY()) < swingRange * 4) {
			g.setColor(Color.red);
			g.drawRect(game.getMouseX() - 32, game.getMouseY() - 32, 64, 64);
		}
		g.setColor(temp);
	}
	
	public void renderPlayerCharacter(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		direction = Math.atan2(game.getMouseY() - getY() - 12, game.getMouseX() - getX() - 12) - 90 * Math.PI / 180; //(Game.HEIGHT - 68) / 2; (Game.WIDTH - 46) / 2);
		
		g2d.rotate(currentWeapon.getSpriteAngleFix() + direction, getX() + width / 2, getY() + height / 2);
		g.drawImage(sprite, (int)getX(), (int)getY(), game);
		g2d.rotate(-(currentWeapon.getSpriteAngleFix() + direction), getX() + width / 2, getY() + height / 2);
	}
	
	public void getBit() {
		decreaseHealthBy(biteDamage);
	}
	
	public double getFlakResistance() {return flakResistance;}
	public void setFlakResistance(double d) {flakResistance = d;}
	
	public void updateShootingStability() {
		if(sprintingOrReloading()) {
			shootingStability = 0;
			timeStill = 0;
		} else if(isMoving()){
			shootingStability = 1 - (double)(velocity.magnitude() / maxVelocity * .7 + .3);
			timeStill = 0;
		} else {
			if(timeStill >= timeTilFocused) {
				shootingStability = 1;
			} else {
				timeStill++;
				shootingStability = .7f;
			}
		}
	}
	
	
	public boolean isMoving() {
		return (velocity.magnitude() > 0);
	}
	
	public boolean sprintingOrReloading() {
		return (isSprinting() || reloading());
	}
	
	public boolean isSprinting() {
		return kInput.keySHIFT;
	}
	
	public boolean reloading() {
		return (reloadingSingles || reloadingClip);
	}
	
	public double getCashPerZombie() {return cashEarnedPerZombie;}
	public void setCashPerZombie(double d) {cashEarnedPerZombie = d;}	
	
	public int getAmmoPickupAmount() {return ammoPickupAmount;}
	public void setAmmoPickupAmount(int ac) {ammoPickupAmount = ac;}
	
	public boolean 	isDead() {return dead;}
	public void 	setDead(boolean b) {dead = b;}
	
	public boolean isReloading() {return reloading();}

	public double 	getOriginalMaxVelocity() {return originalMaxVelocity;}
	public void 	setOriginalMaxVelocity(double d) {originalMaxVelocity = d;}
	public void		multiplyOriginalMaxVelocityBy(double d) {originalMaxVelocity *= d;}
	
	public double 	getMaxVelocity() {return maxVelocity;}
	public void 	setMaxVelocity(double d) {maxVelocity = d;}
	public void		multiplyMaxVelocityBy(double d) {originalMaxVelocity *= d;}

	public double 	getOriginalSprintVelocity() {return originalSprintVelocity;}
	public void 	setOriginalSprintVelocity(double d) {originalSprintVelocity = d;}
	public void		multiplyOriginalSprintVelocityBy(double d) {originalSprintVelocity *= d;}

	public double 	getSprintVelocity() {return sprintVelocity;}
	public void 	setSprintVelocity(double d) {sprintVelocity = d;}
	public void		multiplySprintVelocityBy(double d) {sprintVelocity *= d;}
	
	public int 		getFragsInInventory() {return fragsInInv;}
	public void 	setFragsInInventory(int fragsInInv) {this.fragsInInv = fragsInInv;}

	public int 		getBulletsInInventory() {return bulletsInInventory;}
	public void 	setBulletsInInventory(int bulletsInInventory) {this.bulletsInInventory = bulletsInInventory;}

	public int 		getShotsFired() {return shotsFired;}
	public void 	setShotsFired(int shotsFired) {this.shotsFired = shotsFired;}

	public int 		getShotsMissed() {return shotsMissed;}
	public void 	setShotsMissed(int shotsMissed) {this.shotsMissed = shotsMissed;}

	public RangedWeapon getCurrentWeapon() {return currentWeapon;}
	public void 		setCurrentWeapon(RangedWeapon weapon) {currentWeapon = weapon;}

	public double 	getStamina() {return stamina;}
	public void 	setStamina(double stamina) {this.stamina = stamina;}

	public double 	getMaxStamina() {return maxStamina;}

	public PlayerUpgradeManager getUpgradeManager() {return upgradeManager;}
	
	public Pistol getPistol() {return pistol;}
	public SMG getSMG() {return smg;}
	public Shotgun getShotgun() {return shotgun;}
	public AR getAR() {return ar;}
	public Frag getFrag() {return frag;}
	public Axe getAxe() {return axe;}

	public double 	getDirection() {return direction;}
	public void 	setDirection(double direction) {this.direction = direction;}

	public boolean[] 	getGunsBought() {return gunsBought;}
	public void 		setGunsBought(boolean[] gunsBought) {this.gunsBought = gunsBought;}
	
	public WalkieTalkie getWalkieTalkie() {return walkie;}
	public void 		setHasWalkieTalkie(boolean b) {hasWalkieTalkie = b;}

	public double 	getFragZombieDamagePerVel() {return fragZombieDamagePerVel;}
	public void 	setFragZombieDamagePerVel(double d) {this.fragZombieDamagePerVel = d;}
	
	public double 	getFragSpawnChance() {return fragSpawnChance;}
	public void 	setFragSpawnChance(float f) {fragSpawnChance = f;}
	
	public CustomHitbox getCurrHitbox() {return currHitbox;}
	public void 		setCurrHitbox(CustomHitbox currHitbox) {this.currHitbox = currHitbox;}

	public int 	getHitboxesIn() {return hitboxesIn;}
	public void setHitboxesIn(int x) {this.hitboxesIn = x;}

	public int 	getMinPlayerX() {return minPlayerX;}
	public void setMinPlayerX(int x) {minPlayerX = x;}

	public int 	getMaxPlayerX() {return maxPlayerX;}
	public void setMaxPlayerX(int x) {maxPlayerX = x;}

	public int 	getMinPlayerY() {return minPlayerY;}
	public void setMinPlayerY(int x) {minPlayerY = x;}

	public int 	getMaxPlayerY() {return maxPlayerY;}
	public void setMaxPlayerY(int x) {maxPlayerY = x;}

	public boolean 	boxButton() {return boxButton;}
	public void 	setBoxButton(boolean b) {boxButton = b;}

	public boolean 	isPlaceBoxMode() {return placeBoxMode;}
	public void 	setPlaceBoxMode(boolean b) {placeBoxMode = b;}
	
	public void setReloadTimeWeight(double d) {reloadTimeWeight = d;}
	public void multiplyReloadMaxVelocityBy(double d) {reloadMaxVelocity *= d;}
	public void multiplyMaxStaminaBy(double d) {maxStamina *= d;}
	public void multiplyCashEarnedPerZombieBy(double d) {cashEarnedPerZombie *= d;}
	public void setAimConeMod(double d) {aimConeMod = d;}
	public void setSwingStamina(double d) {swingStamina = d;}
	public void allowHeadshots() {headshotsAllowed = true;}
	public void enableBoxButton() {boxButton = true;}
	public void enableLaserSight() {hasLaserSight = true;}
	
	public void allowClipReloads() {
		canReloadFullClips = true;
	}
	
	
	public boolean hasWalkieTalkie() {
		return hasWalkieTalkie;
	}

	public void giveWalkieTalkie() {
		walkie = new WalkieTalkie(handler);
		hasWalkieTalkie = true;
	}
}
