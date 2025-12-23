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
	 * List内に格納された値が一定周期ごとにループしているか検査するMatcher。
	 * @param period - 指定する周期。
	 * @return List(Integer)に対するMatcherを返却する。
	 * 			指定した周期ごとに値がループしていればテストは成功する。
	 */
	public static Matcher<List<Integer>> periodicChange(int period){
		return new MatcherOfPeriodicChange(period);
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