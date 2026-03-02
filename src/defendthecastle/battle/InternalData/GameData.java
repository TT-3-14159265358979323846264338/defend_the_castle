package defendthecastle.battle.InternalData;

import defaultdata.stage.StageData;
import defendthecastle.battle.Battle;

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
	
	public GameData(Battle battle, StageData stageData) {
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
	
	public void consumeCost(int consumeValue) {
		changeCost(- consumeValue);
	}
	
	public void addCost(int addValue) {
		changeCost(addValue);
	}
	
	void changeCost(int value) {
		synchronized(costLock) {
			cost += value;
			battle.setCostText();
		}
	}
	
	public int getMoraleDifference() {
		return enemyMorale - unitMorale;
	}
	
	public int getCost() {
		return cost;
	}
}