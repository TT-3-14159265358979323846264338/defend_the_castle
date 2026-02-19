package defendthecastle.composition;

import java.util.List;

import commoninheritance.DisplayList;

//表示リスト作成
class DisplayListCreation extends DisplayList{
	private final SaveData saveData;
	
	DisplayListCreation(SaveData saveData){
		this.saveData = saveData;
		super();
	}
	
	@Override
	protected List<Integer> initialCoreDisplayList() {
		return notZeroIndexList(saveData.getCoreNumberList());
	}
	
	@Override
	protected List<Integer> initialWeaponDisplayList() {
		return notZeroIndexList(saveData.getWeaponNumberList());
	}
}