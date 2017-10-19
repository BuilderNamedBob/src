package temp.entity;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

import temp.engine.Main;
import temp.game.Game;
import temp.game.Time;
import temp.game.Timer;
import temp.gameobject.GameObject;
import temp.utils.Utils;

public abstract class Entity extends GameObject {
	
	public float xMoveVector;
	public float yMoveVector;
	public float speedFactor;
	
	public Weapon weapon;
	public boolean attacking;
	public Timer attackTimer;
	public Timer attackSpeedTimer;
	
	public int currentHealth;
	public int maxHealth;
	public float moveSpeed;
	public float attackRange;
	public String[] textureNameArray;
	public Texture[] textureArray;

	public void initEntity(float x, float y, int width, int height, String textureNameArrayPrefix,
						   boolean solid, float moveSpeed, Weapon weapon, int maxHealth) {
		setTextureNameArray(textureNameArrayPrefix);
		loadTextureArray(textureNameArray);
		initGameObject(x, y, 0f, width, height, "entity/" + textureNameArray[4], solid);
		this.moveSpeed = moveSpeed;
		this.weapon = weapon;
		this.maxHealth = maxHealth;
		currentHealth = maxHealth;
		speedFactor = 1;
		attackTimer = new Timer(100f);
		attackSpeedTimer = new Timer(1f / weapon.attackSpeed);
	}
	
	public void attemptAttack(boolean triggerCondition, boolean damageCondition, ArrayList<Entity> damageRecipients) {
		/*
		if (attackTimer.progress() >= 1f / weapon.attackSpeed && !attacking) {
			if (triggerCondition) {
				attacking = true;
				attackDelayTimer = 0;
			}
		} else {
			attackDelayTimer += (double)Time.getDifference() / 1000000000;
		}
		if (attacking && attackDelayTimer >= weapon.windUpTime + weapon.attackTime) { 
			if (damageCondition) {
				for (Entity e : damageRecipients) {
					e.takeDamage(weapon.damage);
				}
			}
			attacking = false;
		}
		*/
		if (!attackTimer.isRunning() && !attackSpeedTimer.isRunning()) {
			if (triggerCondition) {
				attackTimer.start();
				attackSpeedTimer.start();
			}
		}
		if (attackTimer.progress() >= weapon.windUpTime + weapon.attackTime) { //damage registers when they finish their swing
			if (damageCondition) {
				for (Entity e : damageRecipients) {
					e.takeDamage(weapon.damage);
				}
			}
			attackTimer.reset();
		}
			
	}
	
	public void renderTargetSector(float angle) {
		if (attackTimer.isRunning()) {
			if (attackTimer.progress() >= weapon.windUpTime && attackTimer.progress() <= weapon.windUpTime + weapon.attackTime) {
				glColor3f(1, 0, 0);
			}
			Utils.drawSector(x + width / 2, y + height / 2, -0.3f, weapon.attackRange, angle, 90f);
		}
		glColor3f(1, 1, 1);
	}
	
	public void renderHealthBars(int healthBarWidth, int healthBarHeight) {
		glColor3f(1, 0, 0);
		Utils.drawQuad(x + (width - healthBarWidth) / 2, y + height * 1.5f, -0.1f, healthBarWidth, healthBarHeight);
		
		glColor3f(0, 1, 0);
		float percentageHealth = (float)currentHealth / maxHealth;
		if (percentageHealth < 0) {
			percentageHealth = 0;
		}
		Utils.drawQuad(x + (width - healthBarWidth) / 2, y + height * 1.5f, -0.05f,
					   (int)(healthBarWidth * percentageHealth), healthBarHeight);
		
		glColor3f(1, 1, 1);
	}
	
	public void takeDamage(int damage) {
		currentHealth -= damage;
	}
	
	public void checkDeath() {
		if (currentHealth <= 0) {
			if (this instanceof Enemy) {
				Main.game.objectsToDelete.add(this);
			} else if (this instanceof Player) {
				Main.game = new Game();
			}
		}
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