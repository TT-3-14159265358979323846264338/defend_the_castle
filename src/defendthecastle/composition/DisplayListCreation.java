package defendthecastle.composition;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import screendisplay.DisplaySort;

//表示リスト作成
class DisplayListCreation{
	private DisplaySort coreDisplaySort = new DisplaySort();
	private DisplaySort weaponDisplaySort = new DisplaySort();
	
	DisplayListCreation(SaveData SaveData) {
		coreDisplaySort.core(getDisplayList(SaveData.getCoreNumberList()));
		weaponDisplaySort.weapon(getDisplayList(SaveData.getWeaponNumberList()));
	}
	
	List<Integer> getDisplayList(List<Integer> list){
		return IntStream.range(0, list.size()).mapToObj(i -> (list.get(i) == 0)? -1: i).filter(i -> i != -1).collect(Collectors.toList());
	}
	
	List<Integer> getCoreDisplayList() {
		return coreDisplaySort.getDisplayList();
	}
	
	List<Integer> getWeaponDisplayList() {
		return weaponDisplaySort.getDisplayList();
	}

	void setCoreDisplaySort(DisplaySort coreDisplaySort) {
		this.coreDisplaySort = coreDisplaySort;
	}

	void setWeaponDisplaySort(DisplaySort weaponDisplaySort) {
		this.weaponDisplaySort = weaponDisplaySort;
	}
}