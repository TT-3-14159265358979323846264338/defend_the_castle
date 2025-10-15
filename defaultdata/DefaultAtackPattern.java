package defaultdata;

import defaultdata.atackpattern.*;

public class DefaultAtackPattern {
	//パターンの種類
	public static final int PATTERN_SPECIES = 5;
	
	//コード名
	public static final int NO_ATACK = 0;
	public static final int NEAR = 2;
	public static final int FAR = 1;
	public static final int LOW_HP = 3;
	public static final int HIGH_HP = 4;
	
	//コードの振り分け(戦闘時、各キャラに独自のAtackPatternクラスを搭載するため、毎回新インスタンスを生成する必要がある)
	/**
	 * アタックパターンの取得。<br>
	 * 新たなデータを追加したらPATTERN_SPECIESにも加算すること。
	 * @param code - アタックパターンコード。コードはDefaultAtackPatternのクラス変数を使用すること。
	 * @return codeに該当するAtackPatternを返却する。戦闘時、各キャラに独自のAtackPatternクラスを搭載するため、このメソッドで毎回新インスタンスを生成する必要がある。
	 */
	public AtackPattern getAtackPattern(int code) {
		switch(code) {
		case NO_ATACK:
			return new No00NoAtack();
		case NEAR:
			return new No01Near();
		case FAR:
			return new No02Far();
		case LOW_HP:
			return new No03LowHP();
		case HIGH_HP:
			return new No04HighHP();
		default:
			return null;
		}
	}
}