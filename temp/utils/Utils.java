package temp.utils;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import temp.engine.Main;
import temp.entity.Entity;
import temp.entity.Player;
import temp.gameobject.GameObject;

public final class Utils {
	
	private Utils() {
		
	}
	
	private static Random random = new Random();
	
	//generates an integer between 0 (inclusive) and maxBound (exclusive)
	public static int genRandomNumber(int maxBound) {
		return random.nextInt(maxBound);
	}
	
	public static void drawQuad(float x, float y, float z, int width, int height) {
		glDisable(GL_TEXTURE_2D);
		
		glBegin(GL_QUADS);
		glVertex3f(x, y, z);
		glVertex3f(x + width, y, z);
		glVertex3f(x + width, y + height, z);
		glVertex3f(x, y + height, z);
		glEnd();
		
		glEnable(GL_TEXTURE_2D);
	}
	
	public static void drawSector(float x1, float y1, float z, float radius, float centreAngle, float width) 
	{
		float x2;
		float y2;
		
		glDisable(GL_TEXTURE_2D);
		
		glBegin(GL_TRIANGLE_FAN);
		
		glVertex3f(x1, y1, z);
		for (double angle = centreAngle - width / 2; angle < centreAngle + width / 2; angle += 0.2)
		{
			double anglerad = Math.toRadians(angle);
		    x2 = x1 + (float)Math.sin(anglerad) * radius;
		    y2 = y1 + (float)Math.cos(anglerad) * radius;
		    glVertex3f(x2, y2, z);
		}
		glEnd();

		glEnable(GL_TEXTURE_2D);
	}
	
	public static void drawQuadTex(Texture texture, float x, float y, float z, int width, int height) {
		texture.bind();
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); //Temporary additions, supposed to reduce blurring
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); //Temporary additions, supposed to reduce blurring
		glTranslatef(x, y, z);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 1);
		glVertex2f(0, 0);
		glTexCoord2f(1, 1);
		glVertex2f(width, 0);
		glTexCoord2f(1, 0);
		glVertex2f(width, height);
		glTexCoord2f(0, 0);
		glVertex2f(0, height);
		glEnd();
		glLoadIdentity();
	}
	
	public static Texture loadTexture(String path, String fileType) {
		Texture tex = null;
		InputStream in = ResourceLoader.getResourceAsStream(path);
		try {
			tex = TextureLoader.getTexture(fileType, in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tex;
	}
	
	public static Texture quickLoad(String name) {
		Texture tex = loadTexture("temp/res/sprites/" + name + ".png", "PNG");
		if ((tex.getImageWidth() & (tex.getImageWidth() - 1)) != 0 ||
			(tex.getImageHeight() & (tex.getImageHeight() - 1)) != 0) {
			System.out.println(name + " texture must have both its dimensions as a power of 2");
		}
		return tex;
	}
	
	public static boolean isColliding(GameObject go1, GameObject go2) {
		Rectangle r1 = new Rectangle((int)go1.x, (int)go1.y, go1.width,  go1.height);
		Rectangle r2 = new Rectangle((int)go2.x, (int)go2.y, go2.width,  go2.height);
		if (r1.intersects(r2)) {
			return true;
		}
		return false;
	}
	
	//Used for finding if enemy should be killed when within target sector
	/*
	public static boolean isCollidingWithSector(GameObject go1, Player player) {
		float x1 = player.x + player.width / 2;
		float y1 = player.y + player.height / 2;
		
		ArrayList<Integer> xPoints = new ArrayList<Integer>();
		ArrayList<Integer> yPoints = new ArrayList<Integer>();
		
		xPoints.add((int)x1);
		yPoints.add((int)y1);
		
		for (double angle = player.mouseAngle - player.width / 2; angle < player.mouseAngle + player.width / 2; angle += 0.2)
		{
			double anglerad = Math.toRadians(angle);
			
			x1 += (float)Math.sin(anglerad) * player.attackRange;
			y1 += (float)Math.cos(anglerad) * player.attackRange;
			
		    xPoints.add((int)x1);
		    yPoints.add((int)y1);
		}
		
		xPoints.add((int)x1);
		yPoints.add((int)y1);
		
		int arraySize = xPoints.size();
		int[] xPointsArray = new int[arraySize];
		int[] yPointsArray = new int[arraySize];
		
		for (int i = 0; i < arraySize; i++) {
			xPointsArray[i] = xPoints.get(i);
			yPointsArray[i] = yPoints.get(i);
		}
		
		Polygon p = new Polygon(xPointsArray, yPointsArray, arraySize);
		Rectangle r = new Rectangle((int)go1.x, (int)go1.y, go1.width,  go1.height);
		
		if (p.intersects(r)) {
			return true;
		}
		return false;
	}
	*/
	
	public static boolean isCollidingWithSector(GameObject target, Entity attacker, float angle) {
		float xCentre = attacker.x + attacker.width / 2;
		float yCentre = attacker.y + attacker.height / 2;
		
		//Circle of radius attackRange around the player
		Ellipse2D e = new Ellipse2D.Float(xCentre - attacker.weapon.attackRange, yCentre - attacker.weapon.attackRange,
												  attacker.weapon.attackRange * 2, attacker.weapon.attackRange * 2);
		//Rectangle representing enemy hitbox
		Rectangle r = new Rectangle((int)target.x, (int)target.y, target.width, target.height);
		
		//Method was rearranged and this statement added to avoid spending lots of time performing calculations
		//when the enemy is nowhere near the player. This will speed up the game when lots of enemies exist.
		if (!e.intersects(r)) {
			return false;
		}
		
		ArrayList<Integer> xPoints = new ArrayList<Integer>();
		ArrayList<Integer> yPoints = new ArrayList<Integer>();
		
		xPoints.add((int)xCentre);
		yPoints.add((int)yCentre);
		
		xPoints.add((int)(xCentre + (float)Math.sin(Math.toRadians(angle - 45)) * attacker.weapon.attackRange));
		yPoints.add((int)(yCentre + (float)Math.cos(Math.toRadians(angle - 45)) * attacker.weapon.attackRange));
		
		xPoints.add((int)(xCentre + (float)Math.sin(Math.toRadians(angle)) * attacker.weapon.attackRange * (float)Math.sqrt(2)));
		yPoints.add((int)(yCentre + (float)Math.cos(Math.toRadians(angle)) * attacker.weapon.attackRange * (float)Math.sqrt(2)));
		
		xPoints.add((int)(xCentre + (float)Math.cos(Math.toRadians(45 - angle)) * attacker.weapon.attackRange));
		yPoints.add((int)(yCentre + (float)Math.sin(Math.toRadians(45 - angle)) * attacker.weapon.attackRange));
		
		int arraySize = xPoints.size();
		int[] xPointsArray = new int[arraySize];
		int[] yPointsArray = new int[arraySize];
		for (int i = 0; i < arraySize; i++) {
			xPointsArray[i] = xPoints.get(i);
			yPointsArray[i] = yPoints.get(i);
		}
		
		//Rotated square of side length attackRange
		Polygon p = new Polygon(xPointsArray, yPointsArray, arraySize);
		
		if (!p.intersects(r)) {
			return false;
		}
		return true;
	}
	
	
	public static boolean isTouching(GameObject go1, GameObject go2) {
		Rectangle r1 = new Rectangle((int)go1.x - 1, (int)go1.y - 1, go1.width + 2,  go1.height + 2);
		Rectangle r3 = new Rectangle((int)go2.x, (int)go2.y, go2.width, go2.height);
		if (r1.intersects(r3)) {
			return true;
		}
		return false;
	}
	
	public static ArrayList<GameObject> touchingWith(GameObject object) {
		ArrayList<GameObject> touchingWith = new ArrayList<GameObject>();
		ArrayList<GameObject> objects = Main.game.currentObjects;
		for (GameObject go : objects) {
			if (go != object) {
				if (isTouching(go, object)) {
					touchingWith.add(go);
				}
			}
		}
		return touchingWith;
	}
	
	public static ArrayList<GameObject> collidesWith(GameObject object) {
		ArrayList<GameObject> collidingWith = new ArrayList<GameObject>();
		ArrayList<GameObject> objects = Main.game.currentObjects;
		for (GameObject go : objects) {
			if (go != object) {
				if (isColliding(go, object)) {
					collidingWith.add(go);
				}
			}
		}
		return collidingWith;
	}
	
	public static boolean isCollidingWithSolids(GameObject object) {
		ArrayList<GameObject> collidingWith = collidesWith(object);
		for (GameObject go : collidingWith) {
			if (go.solid) {
				return true;
			}
		}
		return false;
	}
	
	public static float calculateXDistance(GameObject go1, GameObject go2) {
		return go2.x - go1.x;
	}
	
	public static float calculateYDistance(GameObject go1, GameObject go2) {
		return go2.y - go1.y;
	}
	
	public static float calculateDistanceFromCentre(GameObject go1, GameObject go2) {
		float go1X = go1.x + go1.width / 2;
		float go1Y = go1.y + go1.height / 2;
		float go2X = go2.x + go2.width / 2;
		float go2Y = go2.y + go2.height / 2;
		return (float)Math.sqrt(Math.pow(go2X - go1X, 2) + Math.pow(go2Y - go1Y, 2));
	}
	
	//Calculates the angle in degrees between an origin and a point relative to north, moving clockwise
	public static float calculateAngle(float originX, float originY, float pointX, float pointY) {
		float yDifference = pointY - originY;
		float xDifference = pointX - originX;
		
		float angle = (float)Math.toDegrees(Math.atan(-yDifference / xDifference));
		
		if (xDifference <= 0) {
			angle -= 90;
		} else {
			angle += 90;
		}
		
		return angle;
	}
	
	public static boolean bottomLeftCornerIntersects(GameObject go1, GameObject go2) {
		if (go1.x >= go2.x && go1.x <= go2.x + go2.width && go1.y >= go2.y && go1.y <= go2.y + go2.height) {
			return true;
		}
		return false;
	}
	
	public static boolean bottomRightCornerIntersects(GameObject go1, GameObject go2) {
		if (go1.x + go1.width >= go2.x && go1.x + go1.width <= go2.x + go2.width && go1.y >= go2.y && go1.y <= go2.y + go2.height) {
			return true;
		}
		return false;
	}
	
	public static boolean topLeftCornerIntersects(GameObject go1, GameObject go2) {
		if (go1.x >= go2.x && go1.x <= go2.x + go2.width && go1.y + go1.height >= go2.y && go1.y + go1.height <= go2.y + go2.height) {
			return true;
		}
		return false;
	}
	
	public static boolean topRightCornerIntersects(GameObject go1, GameObject go2) {
		if (go1.x + go1.width >= go2.x && go1.x + go1.width <= go2.x + go2.width && go1.y + go1.height >= go2.y && go1.y + go1.height <= go2.y + go2.height) {
			return true;
		}
		return false;
	}
	
	public static float correctOverlap(float position, float min, float max) {
		if (position <= min) {
			return min;
		} else if (position >= max){
			return max;
		} else {
			return position;
		}
	}
}
