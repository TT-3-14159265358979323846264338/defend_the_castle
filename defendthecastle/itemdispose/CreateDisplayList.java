package defendthecastle.itemdispose;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import defaultdata.DefaultUnit;
import screendisplay.DisplaySort;

//表示リスト作成
class CreateDisplayList{
	private DisplaySort coreDisplaySort = new DisplaySort();
	private DisplaySort weaponDisplaySort = new DisplaySort();
	private OperateData OperateData;
	
	protected CreateDisplayList(OperateData OperateData) {
		this.OperateData = OperateData;
		coreDisplaySort.core(getInitialCoreDisplayList());
		weaponDisplaySort.weapon(getInitialWeaponDisplayList());
	}
	
	protected List<Integer> getInitialCoreDisplayList(){
		List<Integer> displayList = getDisplayList(OperateData.getCoreNumberList());
		//初期コアはリサイクル禁止
		displayList.remove(DefaultUnit.NORMAL_CORE);
		return displayList;
	}
	
	protected List<Integer> getInitialWeaponDisplayList(){
		return getDisplayList(OperateData.getWeaponNumberList());
	}
	
	private List<Integer> getDisplayList(List<Integer> list){
		return IntStream.range(0, list.size()).mapToObj(i -> (list.get(i) == 0)? -1: i).filter(i -> i != -1).collect(Collectors.toList());
	}
	
	protected List<Integer> getCoreDisplayList() {
		return coreDisplaySort.getDisplayList();
	}
	
	protected List<Integer> getWeaponDisplayList() {
		return weaponDisplaySort.getDisplayList();
	}
}