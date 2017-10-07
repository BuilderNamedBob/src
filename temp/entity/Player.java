package temp.entity;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import temp.engine.Main;
import temp.game.Time;
import temp.gameobject.GameObject;
import temp.utils.Utils;

public class Player extends Entity {
	
	public static final int SIZE = 32;
	public boolean attackFlag = false;
	public boolean dashFlag = false;
	public float dashTimer;
	public float mouseAngle;

	public Player() {
		initEntity(Display.getWidth() / 2 - SIZE / 2, Display.getHeight() / 2 - SIZE / 2, SIZE, SIZE, "player/player", true, 1.5f, Weapon.OP_PLAYER_SWORD, 100);
	}
	
	public void getInput() {
		if (Keyboard.isKeyDown(Keyboard.KEY_W) && !Keyboard.isKeyDown(Keyboard.KEY_S)) {
			yMoveVector = 1;
		} else if (!Keyboard.isKeyDown(Keyboard.KEY_W) && Keyboard.isKeyDown(Keyboard.KEY_S)) {
			yMoveVector = -1;
		} else {
			yMoveVector = 0;
		}
		// Slow player movement speed.
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			speedFactor = 0.5f;
		} else {
			speedFactor = 1.0f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A) && !Keyboard.isKeyDown(Keyboard.KEY_D)) {
			xMoveVector = -1;
		} else if (!Keyboard.isKeyDown(Keyboard.KEY_A) && Keyboard.isKeyDown(Keyboard.KEY_D)) {
			xMoveVector = 1;
		} else {
			xMoveVector = 0;
		}
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
<<<<<<< HEAD
				if (Keyboard.getEventKey() == Keyboard.KEY_SPACE) {
=======
				// Dash key.
				if (Keyboard.getEventKey() == Keyboard.KEY_F) {
>>>>>>> 3a63d67bd0d381664fd809b47c883eeaf5e6a313
					dashFlag = true;
				}
				// Teleportation
				if (Keyboard.getEventKey() == Keyboard.KEY_T) {
					x += xMoveVector * 100;
					y += yMoveVector * 100;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
					Main.game.paused = !Main.game.paused;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_F) {
					int xSpawn = Utils.genRandomNumber(Display.getWidth() - 32);
					int ySpawn = Utils.genRandomNumber(Display.getHeight() - 32);
					Main.game.currentObjects.add(new Enemy(xSpawn, ySpawn, 32, true, 1.5f, true, 1000, false, Weapon.SWORD, 100));
				}
			}
		}
		while (Mouse.next()) {
			if (Mouse.getEventButtonState()) {
				if (Mouse.getEventButton() == 0) {
					attackFlag = true;
				}
			}
		}
	}
	
	public void update() {
		checkDeath();
		updateMouseAngle();
		act();
		handleAttack();
		checkFlags();
		checkTexture();
	}
	
	//This method can be removed when it matches the method it is overriding in the GameObject class
	public void render() {
		renderHealthBars(64, 4);
		renderTargetSector(mouseAngle);
		
		Utils.drawQuadTex(texture, x, y, z, width, height);
	}
	
	private void updateMouseAngle() {
		mouseAngle = Utils.calculateAngle(x + SIZE / 2, y + SIZE / 2, (float)Mouse.getX(), (float)Mouse.getY());
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
	
	private void handleAttack() {
		ArrayList<Entity> targetList = new ArrayList<Entity>();
		for (GameObject go : Main.game.currentObjects) {
			if (go != this && go instanceof Entity && Utils.isCollidingWithSector(go, this, mouseAngle)) {
				targetList.add((Entity)go);
			}
		}
		attemptAttack(attackFlag, true, targetList);
	}
	
	private void checkFlags() {
		attackFlag = false;
		
		// Dash functionality for the player
		if (dashFlag) {
			if (dashTimer >= 0.25f) {
				speedFactor = 1.0f;
				dashFlag = false;
				dashTimer = 0.0f;
			} else {
				speedFactor = 4.0f;
				dashTimer += (double)Time.getDifference() / 1000000000;
			}
		}
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