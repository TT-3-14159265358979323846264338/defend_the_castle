package defaultdata.atackpattern;

import java.util.List;

import battle.Battle;
import battle.BattleData;

public abstract class AtackPattern {
	protected BattleData myself;
	protected List<BattleData> candidate;
	
	public void install(BattleData myself, List<BattleData> candidate) {
		this.myself = myself;
		this.candidate = candidate;
	}
	
	//攻撃パターン説明
	public abstract String getExplanation();
	
	//targetになる相手のBattleDataを返す
	public abstract List<BattleData> getTarget();
	
	//ここから下はfilterやsortedの条件
	protected boolean activeCheck(BattleData data){
		return data.getActivate();
	}
	
	protected boolean rangeCheck(BattleData data) {
		return distanceCalculate(data) <= myself.getRange() + Battle.SIZE / 2;
	}
	
	protected double distanceCalculate(BattleData data) {
		return Math.sqrt(Math.pow(myself.getPositionX() - data.getPositionX(), 2) + Math.pow(myself.getPositionY() - data.getPositionY(), 2));
	}
}