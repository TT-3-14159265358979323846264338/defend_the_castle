package defaultdata;

import defaultdata.atackpattern.AtackPattern;
import defaultdata.atackpattern.No00Near;
import defaultdata.atackpattern.No01Far;

public class DefaultAtackPattern {
	//パターンの種類
	public static final int PATTERN_SPECIES = 2;
	
	//コード名
	public static final int NEAR = 0;
	public static final int FAR = 1;
	
	//コードの振り分け(戦闘時、各キャラに独自のAtackPatternクラスを搭載するため、毎回新インスタンスを生成する必要がある)
	public AtackPattern getAtackPattern(int code) {
		switch(code) {
		case 0:
			return new No00Near();
		case 1:
			return new No01Far();
		default:
			return null;
		}
	}
	
	/*
	新たなデータを追加したらPATTERN_SPECIESにも加算すること
	 */
}