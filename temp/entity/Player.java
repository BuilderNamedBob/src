package temp.entity;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import temp.engine.Main;
import temp.game.Time;
import temp.utils.Utils;
import temp.gameobject.GameObject;

public class Player extends Entity {
	
	public static final int SIZE = 32;
	public boolean attacking = false;
	public float attackRange = 100f;
	public float mouseAngle;

	public Player() {
		initEntity(Display.getWidth() / 2 - SIZE / 2, Display.getHeight() / 2 - SIZE / 2, SIZE, SIZE, "player/player", true, 1.5f);
	}
	
	public void getInput() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W) && !Keyboard.isKeyDown(Keyboard.KEY_S)) {
			yMoveVector = 1;
		} else if (!Keyboard.isKeyDown(Keyboard.KEY_W) && Keyboard.isKeyDown(Keyboard.KEY_S)) {
			yMoveVector = -1;
		} else {
			yMoveVector = 0;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A) && !Keyboard.isKeyDown(Keyboard.KEY_D)) {
			xMoveVector = -1;
		} else if (!Keyboard.isKeyDown(Keyboard.KEY_A) && Keyboard.isKeyDown(Keyboard.KEY_D)) {
			xMoveVector = 1;
		} else {
			xMoveVector = 0;
		}
		if (Mouse.isButtonDown(0)) {
			attacking = true;
		} else {
			attacking = false;
		}
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					Main.game.paused = !Main.game.paused;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
					int xSpawn = Utils.genRandomNumber(Display.getWidth() - 32);
					int ySpawn = Utils.genRandomNumber(Display.getHeight() - 32);
					Main.game.currentObjects.add(new Enemy(xSpawn, ySpawn, 32, true, 1.5f, true, 128, true));
				}
			}
		}
		while (Mouse.next()) {
			if (Mouse.getEventButtonState()) {
				if (Mouse.getEventButton() == 0) {
					for (GameObject go : Main.game.currentObjects) {
						if (go != this && Utils.isCollidingWithSector(go, this)) {
							Main.game.objectsToDelete.add(go);
						}
					}
				}
			}
		}
	}
	
	public void update() {
		updateMouseAngle();
		act();
		checkFlags();
		checkTexture();
	}
	
	//This method can be removed when it matches the method it is overriding in the GameObject class
	public void render() {
		if (attacking) {
			glColor3f(1, 0, 0);
		}
		
		
		Utils.drawSector(x + SIZE / 2, y + SIZE / 2, 0f, attackRange, mouseAngle, 90f);
		
		glColor3f(1, 1, 1);
		Utils.drawQuadTex(texture, x, y, z, width, height);
	}
	
	private void updateMouseAngle() {
		float yDifference = Mouse.getY() - (y + SIZE / 2);
		float xDifference = Mouse.getX() - (x + SIZE / 2);
		
		float tanTheta = yDifference / xDifference;
		float angle = (float)Math.toDegrees(Math.atan(-yDifference / xDifference));
		
		if (xDifference <= 0) {
			angle -= 90;
		} else {
			angle += 90;
		}
		
		mouseAngle = angle;
	}
	
	private void act() {
		float moveAmount = moveSpeed * speedFactor * Time.getDelta();
		float diagonalMoveAmount = moveAmount * (float)(Math.sqrt(2) / 2);
		ArrayList<GameObject> collidingWith = Utils.collidesWith(this);
		if (collidingWith.size() == 0 || !Utils.isCollidingWithSolids(this)) {
			if (xMoveVector != 0 && yMoveVector != 0) {
				x += diagonalMoveAmount * xMoveVector;
				y += diagonalMoveAmount * yMoveVector;
			} else {
				x += moveAmount * xMoveVector;
				y += moveAmount * yMoveVector;
			}
			collidingWith = Utils.collidesWith(this);
			for (GameObject go : collidingWith) {
				if (!go.solid) {
					continue;
				}
				correctCollision(go, collidingWith);
			}
			/*
			ArrayList<GameObject> touchingWith = Utils.touchingWith(this);
			for (GameObject go1 : touchingWith) {
				if (go1 instanceof FloorItem) {
					if (pickupFlag) {
						pickUp((FloorItem)go1);
					}
					continue;
				} else if (go1 instanceof Container) {
					if (useFlag) {
						((Container)go1).attemptOpen();
					}
				}
			}
			*/
		} else {
			System.out.println("Error: player should not be colliding with anything when the frame updates");
		}
	}
	
	private void checkFlags() {
		/*
		Item firstItemInInventory = inventory.getContents()[0];
		if (dropFlag && firstItemInInventory != null) {
			drop(firstItemInInventory);
		}
		if (useFlag && firstItemInInventory != null) {
			firstItemInInventory.activate();
		}
		*/
	}
	
	/*
	private void pickUp(FloorItem item) {
		if (!inventory.isFull()) {
			inventory.add(item.getItem());
			Main.getGame().getObjectsToDelete().add(item);
			storedItems.add(item);
		}
	}
	
	private void drop(Item item) {
		inventory.remove(item);
		for (int i = 0; i < storedItems.size(); i++) {
			FloorItem floorItem = storedItems.get(i);
			if (floorItem.getItem() == item) {
				floorItem.setX(x);
				floorItem.setY(y);
				float newZ = -1;
				for (GameObject go : Util.collidesWith(this)) {
					float highestObjectZ = go.getZ();
					if (highestObjectZ > newZ) {
						newZ = highestObjectZ;
					}
				}
				if (newZ + 0.01f >= z) {
					newZ = z - 0.01f;
				} else {
					newZ += 0.01f;
				}
				floorItem.setZ(newZ);
				ArrayList<GameObject> newObjects = new ArrayList<>(Main.getGame().getCurrentObjects());
				newObjects.add(floorItem);
				Main.getGame().setCurrentObjects(newObjects);
				break;
			}
		}
	}
	*/
}
