package defendthecastle.itemdispose;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import defaultdata.DefaultUnit;
import defendthecastle.commoninheritance.DisplayList;

//表示リスト作成
class CreateDisplayList extends DisplayList{
	private final OperateData operateData;
	
	CreateDisplayList(ScheduledExecutorService scheduler, OperateData operateData){
		this.operateData = operateData;
		super(scheduler);
	}

	@Override
	protected List<Integer> initialCoreDisplayList() {
		List<Integer> displayList = notZeroIndexList(operateData.getCoreNumberList());
		//初期コアはリサイクル禁止
		displayList.remove(DefaultUnit.NORMAL_CORE);
		return displayList;
	}

	@Override
	protected List<Integer> initialWeaponDisplayList() {
		return notZeroIndexList(operateData.getWeaponNumberList());
	}
}