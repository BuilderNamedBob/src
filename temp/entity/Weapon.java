package temp.entity;

public enum Weapon {
	
	OP_PLAYER_SWORD(75f, 1f, 0.5f, 0.05f, 50),
	SWORD(75f, 1f, 0.5f, 0.05f, 40),
	DAGGER(45f, 3f, 0.1f, 0.05f, 30),
	WARHAMMER(125f, 0.2f, 2f, 0.2f, 100);
	
	public float attackRange;
	public float attackSpeed;
	public float windUpTime;
	public float attackTime;
	public int damage;
	
	private Weapon(float attackRange, float attackSpeed, float windUpTime, float attackTime, int damage) {
		this.attackRange = attackRange;
		this.attackSpeed = attackSpeed;
		this.windUpTime = windUpTime;
		this.attackTime = attackTime;
		this.damage = damage;
	}
}
