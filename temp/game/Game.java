package temp.game;

import java.util.ArrayList;

import temp.entity.Player;
import temp.gameobject.GameObject;
import temp.gameobject.Wall;
import temp.utils.Utils;

public class Game {

	public boolean paused = false;
	
	public Player player;
	
	public ArrayList<GameObject> currentObjects;
	public ArrayList<GameObject> objectsToDelete;
	public ArrayList<GameObject> objectsToAdd;

	public Game() {
		player = new Player();
		currentObjects = new ArrayList<GameObject>();
		currentObjects.add(player);
		objectsToDelete = new ArrayList<GameObject>();
		objectsToAdd = new ArrayList<GameObject>();
	}
	
	public void getInput() {
		if (!paused) {
			player.xMoveVector = 0;
			player.yMoveVector = 0;
		}
		player.getInput();
	}
	
	public void update() {
		if (!paused) {
			player.update();
			for (GameObject go : currentObjects) {
				go.update();
			}
			deleteObjects();
			addObjects();
			//temporarily moved player.update() from here to above go.update()
		}
	}
	
	public void render() {
		if (!paused) {
			for (GameObject go : currentObjects) {
				go.render();
			}
			player.render();
		}
	}
	
	private void deleteObjects() {
		if (!objectsToDelete.isEmpty()) {
			ArrayList<GameObject> newObjects = new ArrayList<>(currentObjects);
			ArrayList<GameObject> newObjectsToDelete = new ArrayList<>(objectsToDelete);
			for (GameObject go : objectsToDelete) {
				newObjects.remove(go);
				newObjectsToDelete.remove(go);
			}
			currentObjects = newObjects;
			objectsToDelete = newObjectsToDelete;
		}
	}
	
	private void addObjects() {
		if (!objectsToAdd.isEmpty()) {
			ArrayList<GameObject> newObjects = new ArrayList<>(currentObjects);
			ArrayList<GameObject> newObjectsToAdd = new ArrayList<>(objectsToDelete);
			for (GameObject go : objectsToAdd) {
				newObjects.add(go);
				newObjectsToAdd.remove(go);
			}
			currentObjects = newObjects;
			objectsToAdd = newObjectsToAdd;
		}
	}
}
