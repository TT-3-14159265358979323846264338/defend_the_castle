package defendthecastle.itemdispose;

import java.util.List;

import commonclass.DisplayList;
import defaultdata.Core;

//表示リスト作成
class CreateDisplayList extends DisplayList{
	private final OperateData operateData;
	
	CreateDisplayList(OperateData operateData){
		this.operateData = operateData;
		super();
	}

	@Override
	protected List<Integer> initialCoreDisplayList() {
		List<Integer> displayList = notZeroIndexList(operateData.getCoreNumberList());
		//初期コアはリサイクル禁止
		displayList.remove(Core.NORMAL_CORE.getId());
		return displayList;
	}

	@Override
	protected List<Integer> initialWeaponDisplayList() {
		return notZeroIndexList(operateData.getWeaponNumberList());
	}
}