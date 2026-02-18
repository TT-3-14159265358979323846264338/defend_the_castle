package defendthecastle.composition;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import defendthecastle.commoninheritance.DisplayList;

//表示リスト作成
class DisplayListCreation extends DisplayList{
	private final SaveData saveData;
	
	DisplayListCreation(ScheduledExecutorService scheduler, SaveData saveData){
		this.saveData = saveData;
		super(scheduler);
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