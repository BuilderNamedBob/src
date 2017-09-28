package temp.entity;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

import temp.gameobject.GameObject;
import temp.utils.Utils;

public abstract class Entity extends GameObject {
	
	public float xMoveVector;
	public float yMoveVector;
	public float moveSpeed;
	public float attackRange;
	public float speedFactor;
	public String[] textureNameArray;
	public Texture[] textureArray;

	public void initEntity(float x, float y, int width, int height, String textureNameArrayPrefix, boolean solid, float moveSpeed, float attackRange) {
		setTextureNameArray(textureNameArrayPrefix);
		loadTextureArray(textureNameArray);
		initGameObject(x, y, 0.5f, width, height, "entity/" + textureNameArray[4], solid);
		this.moveSpeed = moveSpeed;
		this.attackRange = attackRange;
		speedFactor = 1;
	}
	
	public void setTextureNameArray(String prefix) {
		String[] textureNameArray = new String[8];
		
		textureNameArray[0] = prefix + "up";
		textureNameArray[1] = prefix + "topright";
		textureNameArray[2] = prefix + "right";
		textureNameArray[3] = prefix + "bottomright";
		textureNameArray[4] = prefix + "down";
		textureNameArray[5] = prefix + "bottomleft";
		textureNameArray[6] = prefix + "left";
		textureNameArray[7] = prefix + "topleft";
		
		this.textureNameArray = textureNameArray;
	}
	
	public void loadTextureArray(String[] textureNameArray) {
		Texture[] textureArray = new Texture[8];
		
		for (int i = 0; i < 8; i++) {
			textureArray[i] = Utils.quickLoad("entity/" + textureNameArray[i]);
		}
		
		this.textureArray = textureArray;
	}
	
	public void checkTexture() {
		double cutoffAngle = Math.toRadians(15d); //degrees either side of horizontal/vertical
		double smallCutoffMagnitude = Math.sin(cutoffAngle);
		double largeCutoffMagnitude = Math.cos(cutoffAngle);
		if (yMoveVector >= largeCutoffMagnitude && xMoveVector <= smallCutoffMagnitude && xMoveVector >= -smallCutoffMagnitude) {
			texture = textureArray[0];
		} else if (yMoveVector >= smallCutoffMagnitude && xMoveVector >= smallCutoffMagnitude) {
			texture = textureArray[1];
		} else if (yMoveVector <= smallCutoffMagnitude && yMoveVector >= -smallCutoffMagnitude && xMoveVector >= largeCutoffMagnitude) {
			texture = textureArray[2];
		} else if (yMoveVector <= -smallCutoffMagnitude && xMoveVector >= smallCutoffMagnitude) {
			texture = textureArray[3];
		} else if (yMoveVector <= -largeCutoffMagnitude && xMoveVector <= smallCutoffMagnitude && xMoveVector >= -smallCutoffMagnitude) {
			texture = textureArray[4];
		} else if (yMoveVector <= -smallCutoffMagnitude && xMoveVector <= -smallCutoffMagnitude) {
			texture = textureArray[5];
		} else if (yMoveVector <= smallCutoffMagnitude && yMoveVector >= -smallCutoffMagnitude && xMoveVector <= -largeCutoffMagnitude) {
			texture = textureArray[6];
		} else if (yMoveVector >= smallCutoffMagnitude && xMoveVector <= -smallCutoffMagnitude) {
			texture = textureArray[7];
		}
		
		/*
		if (yMoveVector == 1 && xMoveVector == 0) {
			texture = textureArray[0];
		} else if (yMoveVector == 1 && xMoveVector == 1) {
			texture = textureArray[1];
		} else if (yMoveVector == 0 && xMoveVector == 1) {
			texture = textureArray[2];
		} else if (yMoveVector == -1 && xMoveVector == 1) {
			texture = textureArray[3];
		} else if (yMoveVector == -1 && xMoveVector == 0) {
			texture = textureArray[4];
		} else if (yMoveVector == -1 && xMoveVector == -1) {
			texture = textureArray[5];
		} else if (yMoveVector == 0 && xMoveVector == -1) {
			texture = textureArray[6];
		} else if (yMoveVector == 1 && xMoveVector == -1) {
			texture = textureArray[7];
		}
		 */
	}
	
	public void correctCollision(GameObject go, ArrayList<GameObject> collidingWith) {
		float simX = x;
		float simY = y;
			if (Utils.topLeftCornerIntersects(this, go)) {
				if (Utils.topRightCornerIntersects(this, go) && !Utils.bottomLeftCornerIntersects(this, go) && yMoveVector > 0) {
					simY = go.y - this.height;
				}
				if (!Utils.topRightCornerIntersects(this, go) && Utils.bottomLeftCornerIntersects(this, go) && xMoveVector < 0) {
					simX = go.x + go.width;
				}
				if (!Utils.topRightCornerIntersects(this, go) && !Utils.bottomLeftCornerIntersects(this, go)) {
					if (xMoveVector < 0 && yMoveVector > 0) {
						if (Math.abs(go.x + go.width - simX) > Math.abs(go.y - this.height - simY)) {
							simY = go.y - this.height;
						} else {
							simX = go.x + go.width;
						}
					} else if (xMoveVector < 0) {
						simX = go.x + go.width;
					} else if (yMoveVector > 0) {
						simY = go.y - this.height;
					}
				}
			} else if (Utils.bottomRightCornerIntersects(this, go)) {
				if (Utils.bottomLeftCornerIntersects(this, go) && !Utils.topRightCornerIntersects(this, go) && yMoveVector < 0) {
					simY = go.y + go.height;
				}
				if (!Utils.bottomLeftCornerIntersects(this, go) && Utils.topRightCornerIntersects(this, go) && xMoveVector > 0) {
					simX = go.x - this.width;
				}
				if (!Utils.bottomLeftCornerIntersects(this, go) && !Utils.topRightCornerIntersects(this, go)) {
					if (xMoveVector > 0 && yMoveVector < 0) {
						if (Math.abs(go.x - this.width - simX) > Math.abs(go.y + go.height - simY)) {
							simY = go.y + go.height;
						} else {
							simX = go.x - this.width;
						}
					} else if (xMoveVector > 0 && yMoveVector >= 0) {
						simX = go.x - this.width;
					} else if (xMoveVector <= 0 && yMoveVector < 0) {
						simY = go.y + go.height;
					}
				}
			} else if (Utils.bottomLeftCornerIntersects(this, go)) {
				if (xMoveVector < 0 && yMoveVector < 0) {
					if (Math.abs(go.x + go.width - simX) > Math.abs(go.y + go.height - simY)) {
						simY = go.y + go.height;
					} else {
						simX = go.x + go.width;
					}
				} else if (xMoveVector < 0) {
					simX = go.x + go.width;
				} else if (yMoveVector < 0) {
					simY = go.y + go.height;
				}
			} else if (Utils.topRightCornerIntersects(this, go)) {
				if (xMoveVector > 0 && yMoveVector > 0) {
					if (Math.abs(go.x - this.width - simX) > Math.abs(go.y - this.height - simY)) {
						simY = go.y - this.height;
					} else {
						simX = go.x - this.width;
					}
				} else if (xMoveVector > 0) {
					simX = go.x - this.width;
				} else if (yMoveVector > 0) {
					simY = go.y - this.height;
				}
			}
			x = simX;
			y = simY;
	}
}
