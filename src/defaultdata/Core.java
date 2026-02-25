package defaultdata;

import defaultdata.core.*;

public enum Core implements Id{
	NORMAL_CORE(0, new No0000NormalCore()),
	ATACK_CORE(1, new No0001NormalAtackCore()),
	DEFENCE_CORE(2, new No0002NormalDefenseCore()),
	RANGE_CORE(3, new No0003NormalRangeCore()),
	HEAL_CORE(4, new No0004NormalHealCore()),
	SPEED_CORE(5, new No0005NormalSpeedCore());
	
	private final int id;
	private final CoreData coreData;
	
	Core(int id, CoreData coreData) {
		this.id = id;
		this.coreData = coreData;
	}

	public int getId() {
		return id;
	}

	public CoreData getCoreData() {
		return coreData;
	}
	
	public static CoreData getCoreData(int id) {
		for(Core i: values()) {
			if(i.getId() == id) {
				return i.getCoreData();
			}
		}
		return null;
	}
}