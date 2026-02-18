package defendthecastle.commoninheritance;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import defendthecastle.screendisplay.CoreDisplaySort;
import defendthecastle.screendisplay.WeaponDisplaySort;

public abstract class DisplayList {
	private final CoreDisplaySort coreDisplaySort;
	private final WeaponDisplaySort weaponDisplaySort;
	
	/**
	 * コアと武器の表示する順番を指定する。
	 */
	protected DisplayList() {
		coreDisplaySort = createCoreDisplaySort();
		weaponDisplaySort = createWeaponDisplaySort();
	}
	
	CoreDisplaySort createCoreDisplaySort() {
		return new CoreDisplaySort(initialCoreDisplayList());
	}
	
	WeaponDisplaySort createWeaponDisplaySort() {
		return new WeaponDisplaySort(initialWeaponDisplayList());
	}
	
	/**
	 * 初期状態でコアを表示する順番を格納したリストを作成する。
	 * @return - コア表示順リスト。
	 */
	protected abstract List<Integer> initialCoreDisplayList();
	
	/**
	 * 初期状態で武器を表示する順番を格納したリストを作成する。
	 * @return - 武器表示順リスト。
	 */
	protected abstract List<Integer> initialWeaponDisplayList();
	
	/**
	 * 所持数が0ではないインデックスリストを作成する。
	 * @param numberList - 所持数を格納したリスト。
	 * @return 所持数が0ではないアイテムのインデックスを格納したリスト。
	 */
	protected List<Integer> notZeroIndexList(List<Integer> numberList){
		return IntStream.range(0, numberList.size()).filter(i -> numberList.get(i) != 0).	boxed().collect(Collectors.toList());
	}
	
	/**
	 * 新たなコアの表示リストを作成する。
	 * @return コアの表示する順番を格納したリスト。
	 */
	public List<Integer> getCoreDisplayList() {
		return coreDisplaySort.getDisplayList();
	}
	
	/**
	 * 新たな武器の表示リストを作成する。
	 * @return 武器の表示する順番を格納したリスト。
	 */
	public List<Integer> getWeaponDisplayList() {
		return weaponDisplaySort.getDisplayList();
	}
}