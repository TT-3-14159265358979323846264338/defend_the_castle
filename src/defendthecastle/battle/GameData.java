package defendthecastle.battle;

import defaultdata.stage.StageData;

//ゲームデータ管理
public class GameData{
	public static final boolean UNIT = true;
	public static final boolean ENEMY = false;
	private final Battle battle;
	private int unitMorale;
	private int enemyMorale;
	private int cost;
	private final Object moraleLock = new Object();
	private final Object costLock = new Object();
	
	GameData(Battle battle, StageData stageData) {
		this.battle = battle;
		unitMorale = stageData.getMorale().get(0);
		enemyMorale = stageData.getMorale().get(1);
		cost = stageData.getCost();
	}
	
	void moraleBoost(boolean code, int boost) {
		changeMorale(code, boost);
	}
	
	void lowMorale(boolean code, int decline) {
		changeMorale(code, - decline);
	}
	
	void changeMorale(boolean code, int value) {
		synchronized(moraleLock) {
			if(code) {
				unitMorale += value;
				return;
			}
			enemyMorale += value;
		}
	}
	
	void consumeCost(int consumeValue) {
		changeCost(- consumeValue);
	}
	
	void addCost(int addValue) {
		changeCost(addValue);
	}
	
	void changeCost(int value) {
		synchronized(costLock) {
			cost += value;
			battle.setCostText();
		}
	}
	
	int getMoraleDifference() {
		return enemyMorale - unitMorale;
	}
	
	int getCost() {
		return cost;
	}
}