package battle;

import defaultdata.stage.StageData;

//ゲームデータ管理
public class GameData{
	public static final boolean UNIT = true;
	public static final boolean ENEMY = false;
	private int unitMorale;
	private int enemyMorale;
	private int cost;
	private Object moraleLock = new Object();
	private Object costLock = new Object();
	
	GameData(StageData StageData) {
		unitMorale = StageData.getMorale().get(0);
		enemyMorale = StageData.getMorale().get(1);
		cost = StageData.getCost();
	}
	
	void moraleBoost(boolean code, int boost) {
		synchronized(moraleLock) {
			if(code) {
				unitMorale += boost;
				return;
			}
			enemyMorale += boost;
		}
	}
	
	void lowMorale(boolean code, int decline) {
		synchronized(moraleLock) {
			if(code) {
				unitMorale -= decline;
				return;
			}
			enemyMorale -= decline;
		}
	}
	
	void consumeCost(int consumeValue) {
		synchronized(costLock) {
			cost -= consumeValue;
		}
	}
	
	void addCost(int addValue) {
		synchronized(costLock) {
			cost += addValue;
		}
	}
	
	int getMoraleDifference() {
		return enemyMorale - unitMorale;
	}
	
	int getCost() {
		return cost;
	}
}