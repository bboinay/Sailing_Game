package main;

import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.AWTEventListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JFrame;

public class Game extends Canvas implements Runnable{

	//This is all general stuff the "Game" should be aware of (this is background 
	//stuff you don't need to worry about)
	private static final long serialVersionUID = -3885519666922197636L;
	public static int WIDTH = (int)(800*1), HEIGHT = (int)(608*1);
	public static int MIDSCREENX = (WIDTH - 46) / 2, MIDSCREENY = (HEIGHT - 68) / 2; 
	public Window window;
	public String title = "Sailing Game";
	private Thread thread;
	private boolean isRunning = false;
	private int mouseX, mouseY;
	public int screenMouseX, screenMouseY;
	public int centerX = WIDTH / 2;
	public int centerY = HEIGHT / 2;
	
	//Instances (this is the 'crew' that helps the game run)
	//think of these as like writing the names of the heist crew on the whiteboard.
	//'private Handler handler;' is saying 'yeah we're gonna use a Handler called handler
	//later on in this program. The 'public Handler getHandler()' bit is a method
	//in this Game class. Methods are things that a class can do, so this means any 
	//'Game' object can call '.getHandler()' and it will return the handler that the game
	//is using. This will come in handy later.
	private Handler handler; //handler is the class that keeps track of what GameObjects are in the game
	public Handler getHandler() {return handler;}
	private Camera cam; //camera keeps track of what portion of the window is visible
	public Camera getCamera() {return cam;}
	private HUD hud; //HUD can display information about the game on a 'layer' above the actual game field
	public HUD getHud() {return hud;}
	
	private KeyInput kInput; //this guy's basically just a list that constantly checks if keypresses are happening
	public KeyInput getKeyInput() {return kInput;}
	private MouseInput mInput; //this guy's basically an ear-to-the-ground always checking if the mouse is pressed
							   //and if it is pressed, for how long
	public MouseInput getMouseInput() {return mInput;}
	
	
	
	public STATE gameState = STATE.Game;
	
	Point3D playerOrigin = new Point3D(MIDSCREENX, MIDSCREENY);
	
	public static void main(String args[]) {
		new Game();
	}
	
	public Game() {
		window = new Window(WIDTH, HEIGHT, title, this);
		//Toolkit.getDefaultToolkit().addAWTEventListener(new Listener(), AWTEvent.MOUSE_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
			
		initialize();
		start();
	}
	
	public void initialize() {
		
		//instances
			handler = new Handler(this);
			hud = new HUD(this);
			cam = new Camera(this);
	
		//inputs + listeners
			kInput = new KeyInput(this);
			mInput = new MouseInput();
			this.addKeyListener(kInput);
			this.addMouseListener(mInput);

			
		handler.clear();
		
		handler.addObject(new Ship(playerOrigin, this));
		
		System.out.println("initialization complete");
	}
	
	private synchronized void start() {
		if(isRunning) return;
		
		thread = new Thread(this);
		thread.start();
		isRunning = true;
	}
	
	private synchronized void stop() {
		if(!isRunning) return;
		try{
			thread.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		isRunning = false;
	}
	
	public void run() {
		
		this.requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		//int frames = 0;
		while(isRunning){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				tick();
				delta--;
			}
			render();
			//frames++;
					
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				//frames = 0;
			}
		}
		stop();
	}
	
	private void tick() {
		updateMouseLocation();
		//testPrintMouseLocation();
		//experimental, to keep mouseX and mouseY up to date? idk. think this is important for full auto but still testing
		mInput.tick();
		handler.tick();
	}
	
	public void render() {
		//Buffer strat stuff I don't understand
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		//end of confusing stuff
		
		
		
		g.setColor(Color.pink);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		renderGame(g);
		
		
		
		//Buffer strat stuff I don't understand
		bs.show();
		g.dispose();
		//end of confusing stuff
	}
	
	public void renderGame(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.translate(-cam.getX(),  -cam.getY());
		
		g.setColor(Color.blue);
		g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		handler.render(g);
		
		//g2d.translate(cam.getX(),  cam.getY());
	}

	public void updateMouseLocation() {
		Point mouseLoc = this.getMousePosition();
		if(mouseLoc != null) {
			screenMouseX = (int) mouseLoc.getX();
			screenMouseY = (int) mouseLoc.getY();
			mouseX = screenMouseX + (int) cam.getX();
			mouseY = screenMouseY + (int) cam.getY();
		}
	}
	
	public void testPrintMouseLocation() {
		System.out.println("mouseX: " + mouseX + ", mouseY: " + mouseY);
		System.out.println("screenMouseX: " + screenMouseX + ", screenMouseY: " + screenMouseY);
	}
	
	
	
	public STATE getState() {return gameState;}
	public void setState(STATE s) {gameState = s;}
	
	public int getMouseX() {return mouseX;}
	public int getMouseY() {return mouseY;}
	
	public int getScreenMouseX() {return screenMouseX;}
	public int getScreenMouseY() {return screenMouseY;}
	
	public JFrame getWindowFrame() {
		return window.getFrame();
	}
	
	public Point3D getPlayerOrigin() {
		return playerOrigin;
	}
	
	//give it a number, and a min/max that it should be held within
	//gives back number 'clamped' to exist within that range
	public static double clamp(double num, double min, double max) {
		if(num < min) return min;
		else if(num > max) return max;
		else return num;
	}
	
	//give it an x-component and y-component of a vector
	//returns the vector's magnitude
	public static double magnitude(double a, double b) {
		return Math.sqrt(a * a + b * b);
	}
	
	//give it two GameObjects
	//returns # of pixels between the GameObjects (straight line distance)
	public static double distanceBetweenGameObjects(GameObject a, GameObject b) {
		return magnitude(a.getCenterX() - b.getCenterX(), a.getCenterY() - b.getCenterY());
	}
	
	//give a range i.e. (0, 2)
	//gives you a random double (decimal #) within that range (i.e. 1.2331)
	public double randomDoubleInRange(double min, double max) {
		if(max < min) {
			double temp = max;
			max = min;
			min = temp;
		}
		return Math.random()*(max - min) + min;
	}
	
	//give a range i.e. (0f, 2f)
	//gives you a random float (decimal #) within that range (i.e. 1.2331f)
	public float randomFloatInRange(float min, float max) {
		if(max < min) {
			float temp = max;
			max = min;
			min = temp;
		}
		return (float)Math.random()*(max - min) + min;
	}
	
	//renders a basic box at coordinates (x, y) with width and height, with a painted 
	//border borderColor of thickness lineThickness and a painted main body mainColor
	public static void renderBasicBox(double x, double y, double width, double height, Graphics g, Color borderColor, Color mainColor, int lineThickness) {
		g.setColor(borderColor);
		g.fillRect((int)x, (int)y, (int)width, (int)height);
		g.setColor(mainColor);
		g.fillRect((int)x + lineThickness, (int)y + lineThickness, (int)width - 2 * lineThickness, (int)height - 2 * lineThickness);
	}
	
	//prints out int array for display
	public static void printArray(int[] arr) {
		System.out.print("[");
		for(int i = 0; i < arr.length - 1; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println(arr[arr.length - 1] + "]");
	}
	
	//prints out boolean array for display
	public static void printBooleanArray(boolean[] arr) {
		System.out.print("[");
		for(int i = 0; i < arr.length - 1; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println(arr[arr.length - 1] + "]");
	}
	
	//give it a mouse position (mx, my)
	//returns true if mouse is currently over a box at (x, y) of width 'width' and height 'height'
	public boolean mouseOver(int mx, int my, int x, int y, int width, int height) {
		if (mx > x && mx < x + width) {
			if (my > y && my < y + height) {
				return true;
			}
		}
		return false;
	}
	
	public static double min(double a, double b) {
		if(a < b)
			return a;
		return b;
	}
	
	
}
