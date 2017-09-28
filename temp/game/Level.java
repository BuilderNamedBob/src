package temp.game;

import java.util.ArrayList;

import temp.gameobject.GameObject;

public class Level {
	
	public ArrayList<GameObject> objects;
	public int id;
	
	public Level(int id) {
		this.id = id;
		generate();
	}
	
	//dimensions are designed for a 960x640 game screen
	private void generate() {
		objects = new ArrayList<GameObject>();
		switch (id) {
		case 1:
			
			break;
		case 2:
			
			break;
		case 3:
			
			break;
		case 4:
			
			break;
		case 5:
			
			break;
		default:
			System.out.println("Error: level id was not between 0-4");
			break;
		}
	}
}
