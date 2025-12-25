package custommatcher;

import java.util.List;
import java.util.stream.IntStream;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

class MacherOfRepeatingPattern extends BaseMatcher<List<Integer>>{
	private int period;
	
	MacherOfRepeatingPattern(int period) {
		this.period = period;
	}
	
	@Override
	public boolean matches(Object obj) {
		if(period < 2) {
			return false;
		}
		if(!instanceCheck(obj)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		List<Integer> list = (List<Integer>) obj;
		List<List<Integer>> repeatList = IntStream.range(0, (int) Math.ceil((double) list.size() / period))
												.mapToObj(i -> list.stream().skip(i * period).limit(period).toList())
												.toList();
		for(List<Integer> i: repeatList) {
			if(i.stream().allMatch(j -> i.get(0).equals(j))) {
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
	
	@Override
	public void describeTo(Description desc) {
		if(period < 2) {
			desc.appendText("指定する周期は2以上にしてください");
			return;
		}
		desc.appendText("与えられたList<Integer>は、指定した周期ごとに同じ値が並んでいません。");
	}
}