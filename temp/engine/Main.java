package temp.engine;

import static org.lwjgl.opengl.GL11.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import temp.game.Game;
import temp.game.Time;

public final class Main {
	
	//Sets game window width to 960 pixels, and width to 640 pixels.
	private static final int DISPLAY_WIDTH = 1280;
	private static final int DISPLAY_HEIGHT = 720;
	
	public static Game game;
	
	private Main() {
		
	}
	
	public static void main(String[] args) {
		initDisplay();
		initGL();
		initAL();
		initGame();
		initTime();
		gameLoop();
		cleanUp();
	}
	
	//Sets all necessary settings in the LWJGL Display class, and sets up keyboard and mouse input.
	private static void initDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
			Display.create();
			Display.setTitle("RPG Legacy Code");
			Display.setVSyncEnabled(true);
			Keyboard.create();
			Mouse.create();
		} catch (LWJGLException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	//Initialises OpenGL and all necessary settings. Clear colour is set to black and draw colour is set to white.
	private static void initGL() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), 0, Display.getHeight(), -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0, 0, 0, 1);
		glColor3f(1, 1, 1);
	}
	
	//Initialises OpenAL.
	private static void initAL() {
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		//Audio class must be set up first.
		//Audio.setListenerData();
		//Audio.loadBuffers();
	}
	
	//Creates an instance of the game. Should be triggered from the starting menu.
	private static void initGame() {
		game = new Game();
	}
	
	//Initialises the Time class to keep track of time and allow smooth frame transitions.
	private static void initTime() {
		Time.init();
	}
	
	//Runs the game, as long as the display is not closed.
	private static void gameLoop() {
		while (!Display.isCloseRequested()) {
			game.getInput();
			Time.update();
			game.update();
			renderGame();
		}
	}
	
	//Clears the colour and depth buffers, renders the frame to the screen, and keeps the game synced at 60fps.
	private static void renderGame() {
		glClear(GL_COLOR_BUFFER_BIT);
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		game.render();
		Display.update();
		Display.sync(60);
	}
	
	//Destroys the display, the keyboard and mouse input and the buffers and sources used for OpenAL.
	private static void cleanUp() {
		Display.destroy();
		Keyboard.destroy();
		Mouse.destroy();
		//Audio class must be set up first.
		//Audio.destroy();
		//The following line is used temporarily until the Audio class is created:
		AL.destroy();
	}
}
