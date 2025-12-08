package defendthecastle.composition;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import screendisplay.DisplaySort;

//表示リスト作成
class DisplayListCreation{
	private DisplaySort coreDisplaySort = new DisplaySort();
	private DisplaySort weaponDisplaySort = new DisplaySort();
	
	protected DisplayListCreation(SaveData SaveData) {
		coreDisplaySort.core(getDisplayList(SaveData.getCoreNumberList()));
		weaponDisplaySort.weapon(getDisplayList(SaveData.getWeaponNumberList()));
	}
	
	protected List<Integer> getDisplayList(List<Integer> list){
		return IntStream.range(0, list.size()).mapToObj(i -> (list.get(i) == 0)? -1: i).filter(i -> i != -1).collect(Collectors.toList());
	}
	
	protected List<Integer> getCoreDisplayList() {
		return coreDisplaySort.getDisplayList();
	}
	
	protected List<Integer> getWeaponDisplayList() {
		return weaponDisplaySort.getDisplayList();
	}
}