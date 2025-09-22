package defaultdata.atackpattern;

import java.util.List;

import battle.Battle;
import battle.BattleData;

public abstract class AtackPattern {
	protected BattleData myself;
	/**
	 * ターゲット候補となるBattleData。{@link #install}を呼び出すことで初期化される。
	 */
	protected List<BattleData> candidate;
	
	/**
	 * インスタンス変数を初期化する。
	 * @param myself - 自分自身のBattleData。
	 * @param candidate - ターゲット候補となるBattleDataのList。
	 */
	public void install(BattleData myself, List<BattleData> candidate) {
		this.myself = myself;
		this.candidate = candidate;
	}
	
	/**
	 * 攻撃パターン説明。
	 * @return 攻撃パターン説明を返却する。表示させるときの都合上、単語・短文が望ましい。(表示時はフォントサイズが自動調整される。)
	 */
	public abstract String getExplanation();
	
	/**
	 * targetとなる相手のBattleDataを算出する。
	 * @return {@link #candidate}を元に、条件に合うターゲットを返却する。同クラス下部に、条件を探す際によく使うメソッドがある。
	 */
	public abstract List<BattleData> getTarget();
	
	//ここから下はfilterやsortedの条件
	protected boolean activeCheck(BattleData data){
		return data.canActivate();
	}
	
	protected boolean rangeCheck(BattleData data) {
		return distanceCalculate(data) <= myself.getRange() + Battle.SIZE / 2;
	}
	
	protected double distanceCalculate(BattleData data) {
		return Math.sqrt(Math.pow(myself.getPositionX() - data.getPositionX(), 2) + Math.pow(myself.getPositionY() - data.getPositionY(), 2));
	}
}