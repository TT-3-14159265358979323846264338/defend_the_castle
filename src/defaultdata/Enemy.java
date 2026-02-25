package defaultdata;

import defaultdata.enemy.EnemyData;
import defaultdata.enemy.*;

public enum Enemy {
	BLUE_SLIME(0, new No0000BlueSlime()),
	RED_SLIME(1, new No0001RedSlime()),
	GREEN_SLIME(2, new No0002GreenSlime()),
	YELLOW_SLIME(3, new No0003YellowSlime()),
	HIGH_SLIME(4, new No0004HighSlime()),
	CASTLE(5, new No0005Castle()),
	FRONT_GATE(6, new No0006FrontGate()),
	SIDE_GATE(7, new No0007SideGate());
	
	private final int id;
	private final EnemyData enemyData;
	
	Enemy(int id, EnemyData enemyData) {
		this.id = id;
		this.enemyData = enemyData;
	}

	public int getId() {
		return id;
	}

	public EnemyData getEnemyData() {
		return enemyData;
	}
	
	public static EnemyData getEnemyData(int id) {
		for(Enemy i: values()) {
			if(i.getId() == id) {
				return i.getEnemyData();
			}
		}
		return null;
	}
}