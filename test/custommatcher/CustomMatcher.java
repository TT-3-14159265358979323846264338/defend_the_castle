package custommatcher;

import java.util.List;

import javax.swing.JComponent;

import org.hamcrest.Matcher;

public class CustomMatcher {
	/**
	 * JButton, JLabelのテキストが全て表示されるだけのサイズがあるか検査するMatcher。<br>
	 * 事前にsetText, setFont, setBoundsなどで表示情報を設定しておくこと。
	 * @return JButton, JLabelに対するMatcherを返却する。
	 * 			テキストが全て表示可能であればテストは成功する。
	 * 			テキストが事前に設定されていなければテストは失敗する。
	 * 			setBorder()による余白設定を考慮する。
	 */
	public static Matcher<JComponent> displayAllText() {
        return new MatcherOfDisplayAllText();
    }
	
	/**
	 * List内に格納された値が一定周期ごとにループしているか検査するMatcher。<br>
	 * テスト成功例) Arrays.asList(0, 2, 4, 6, 0, 2, 4, 6, 0, 2, 4, 6)
	 * @param period - 指定する周期。値が2以上でなければテストは失敗する。
	 * @return List(Integer)に対するMatcherを返却する。
	 * 			指定した周期ごとに値がループしていればテストは成功する。
	 */
	public static Matcher<List<Integer>> periodicChange(int period){
		return new MatcherOfPeriodicChange(period);
	}
	
	/**
	 * List内に格納された値が一定周期ごとに同じ値が並んでいるか検査するMatcher。<br>
	 * テスト成功例) Arrays.asList(0, 0, 0, 2, 2, 2, 4, 4, 4, 6, 6, 6)
	 * @param period - 指定する周期。値が2以上でなければテストは失敗する。
	 * @return List(Integer)に対するMatcherを返却する。
	 * 			指定した周期ごとに同じ値が並んでいるならテストは成功する。
	 */
	public static Matcher<List<Integer>> repeatingPattern(int period){
		return new MacherOfRepeatingPattern(period);
	}
	
	/**
	 * 与えられた配列の全ての要素が、元の配列内に含まれているか検査するMatcher。
	 * @param target - 与えられた配列。
	 * @return Object[]に対するMatcherを返却する。
	 * 			与えられた配列の全ての要素が、元の配列内の要素に含まれていればテストは成功する。
	 * 			要素の順番や個数は加味しない。
	 */
	public static Matcher<Object[]> hasAllItemInArray(Object[] target){
		return new MatcherOfHavingAllItemInArray(target);
	}
}