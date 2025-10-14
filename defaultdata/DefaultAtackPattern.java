package defaultdata;

import defaultdata.atackpattern.*;

public class DefaultAtackPattern {
	//パターンの種類
	public static final int PATTERN_SPECIES = 2;
	
	//コード名
	public static final int NEAR = 0;
	public static final int FAR = 1;
	public static final int NO_ATACK = 2;
	
	//コードの振り分け(戦闘時、各キャラに独自のAtackPatternクラスを搭載するため、毎回新インスタンスを生成する必要がある)
	/**
	 * アタックパターンの取得。<br>
	 * 新たなデータを追加したらPATTERN_SPECIESにも加算すること。
	 * @param code - アタックパターンコード。コードはDefaultAtackPatternのクラス変数を使用すること。
	 * @return codeに該当するAtackPatternを返却する。戦闘時、各キャラに独自のAtackPatternクラスを搭載するため、このメソッドで毎回新インスタンスを生成する必要がある。
	 */
	public AtackPattern getAtackPattern(int code) {
		switch(code) {
		case NEAR:
			return new No00Near();
		case FAR:
			return new No01Far();
		case NO_ATACK:
			return new No02NoAtack();
		default:
			return null;
		}
	}
}