package battle;

import defaultdata.stage.StageData;

//ゲームデータ管理
public class GameData{
	final static boolean UNIT = true;
	final static boolean ENEMY = false;
	int unitMorale;
	int enemyMorale;
	int cost;
	
	protected GameData(StageData StageData) {
		unitMorale = StageData.getMorale().get(0);
		enemyMorale = StageData.getMorale().get(1);
		cost = StageData.getCost();
	}
	
	protected void moraleBoost(boolean code, int boost) {
		if(code) {
			unitMorale += boost;
			return;
		}
		enemyMorale += boost;
	}
	
	protected void lowMorale(boolean code, int decline) {
		if(code) {
			unitMorale -= decline;
			return;
		}
		enemyMorale -= decline;
	}
	
	protected void consumeCost(int consumeValue) {
		cost -= consumeValue;
	}
	
	protected void addCost(int addValue) {
		cost += addValue;
	}
	
	protected int getMoraleDifference() {
		return enemyMorale - unitMorale;
	}
	
	protected int getCost() {
		return cost;
	}
}