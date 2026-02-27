package defaultdata.atackpattern;

import java.util.List;

import defendthecastle.battle.Battle;
import defendthecastle.battle.BattleData;

public abstract class AtackPatternData {
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
	 * @return {@link #candidate}を元に、条件に合うターゲットを返却する。{@link atackPatternData}下部に、条件を探す際によく使うメソッドがある。
	 */
	public abstract List<BattleData> getTarget();
	
	//ここから下はfilterやsortedの条件
	/**
	 * ターゲット候補がアクティブ状態であるか確認する。
	 * @param data - ターゲット候補。
	 * @return アクティブ状態であればtrueを返却する。
	 */
	boolean activeCheck(BattleData data){
		return data.canActivate();
	}
	
	/**
	 * ターゲット候補が自身の射程内にいるか確認する。
	 * @param data - ターゲット候補。
	 * @return 自身の射程内にいればtrueを返却する。
	 */
	boolean rangeCheck(BattleData data) {
		return distanceCalculate(data) <= myself.getRange() + Battle.SIZE / 2;
	}
	
	/**
	 * ターゲット候補と自身の距離を計算する。
	 * @param data - ターゲット候補。
	 * @return ターゲット候補と自身の距離を返却する。
	 */
	double distanceCalculate(BattleData data) {
		return Math.sqrt(Math.pow(myself.getPositionX() - data.getPositionX(), 2) + Math.pow(myself.getPositionY() - data.getPositionY(), 2));
	}
	
	/**
	 * ターゲット候補の残存HP割合を計算する。
	 * @param data - ターゲット候補。
	 * @return ターゲット候補の残存HP割合を返却する。
	 */
	double ratioHP(BattleData data) {
		return (double) data.getNowHP() / data.getMaxHP();
	}
}