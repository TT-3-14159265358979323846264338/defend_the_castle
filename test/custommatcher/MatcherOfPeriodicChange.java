package custommatcher;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class MatcherOfPeriodicChange extends BaseMatcher<List<Integer>>{
	private int period;
	
	/**
	 * List内に格納された値が一定周期ごとにループしているか検査するMatcher。
	 * @param period - 指定する周期。
	 * @return List(Integer)に対するMatcherを返却する。指定した周期ごとに値がループしていればテストは成功する。
	 */
	public static Matcher<List<Integer>> periodicChange(int period){
		return new MatcherOfPeriodicChange(period);
	}
	
	public MatcherOfPeriodicChange(int period) {
		this.period = period;
	}
	
	@Override
	public boolean matches(Object obj) {
		if(!instanceCheck(obj)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		List<Integer> list = (List<Integer>) obj;
		for(int i = 0; i < period; i++) {
			if(canLoopEveryCycle(i, list, period)) {
				continue;
			}
			return false;
		}
		return true;
	}
	
	boolean instanceCheck(Object obj) {
		if(!(obj instanceof List<?>)) {
			return false;
		}
		List<?> list = (List<?>) obj;
		for(Object element: list) {
			if(!(element instanceof Integer)) {
				return false;
			}
		}
		return true;
	}
	
	boolean canLoopEveryCycle(int number, List<Integer> position, int cycle) {
		int index = number + cycle;
		do{
			if(position.get(number) == position.get(index).intValue()) {
				index += cycle;
				continue;
			}
			return false;
		}while(index < position.size());
		return true;
	}

	@Override
	public void describeTo(Description desc) {
		desc.appendText("与えられたList<Integer>は、指定した周期ごとに値がループしていません。");
	}
}