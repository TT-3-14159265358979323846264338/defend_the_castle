package custommatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

class MatcherOfHavingAllItemInArray extends BaseMatcher<Object[]>{
	private Object[] target;
	private List<Object> notHasObject = new ArrayList<>();
	
	MatcherOfHavingAllItemInArray(Object[] target){
		this.target = target;
	}

	@Override
	public boolean matches(Object obj) {
		if(!(obj instanceof Object[])) {
			return false;
		}
		Object[] origin = (Object[]) obj;
		notHasObject = Stream.of(target).filter(i -> Stream.of(origin).noneMatch(j -> j.equals(i))).toList();
		if(notHasObject.size() == 0) {
			return true;
		}
		return false;
	}

	@Override
	public void describeTo(Description desc) {
		desc.appendValue(notHasObject);
		desc.appendText("が元の配列に含まれていません。");
	}
}