package custommatcher;

import java.util.List;

import javax.swing.JButton;

import org.hamcrest.Matcher;

public class CustomMatcher {
	/**
	 * JButtonのテキストが全て表示されるだけのサイズがあるか検査するMatcher。<br>
	 * 事前にsetText, setFont, setBoundsなどで表示情報を設定しておくこと。
	 * @return JButtonに対するMatcherを返却する。テキストが全て表示可能であればテストは成功する。
	 */
	public static Matcher<JButton> displayAllText() {
        return new MatcherOfDisplayAllText();
    }
	
	/**
	 * List内に格納された値が一定周期ごとにループしているか検査するMatcher。
	 * @param period - 指定する周期。
	 * @return List(Integer)に対するMatcherを返却する。指定した周期ごとに値がループしていればテストは成功する。
	 */
	public static Matcher<List<Integer>> periodicChange(int period){
		return new MatcherOfPeriodicChange(period);
	}
}