package custommatcher;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

class MatcherOfPeriodicChange extends BaseMatcher<List<Integer>>{
	private int period;
	
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