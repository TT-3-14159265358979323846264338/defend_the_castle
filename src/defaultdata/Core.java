package defaultdata;

import defaultdata.core.*;

public enum Core implements DefaultEnum<CoreData>{
	NORMAL_CORE(0, new No0000NormalCore()),
	ATACK_CORE(1, new No0001NormalAtackCore()),
	DEFENCE_CORE(2, new No0002NormalDefenseCore()),
	RANGE_CORE(3, new No0003NormalRangeCore()),
	HEAL_CORE(4, new No0004NormalHealCore()),
	SPEED_CORE(5, new No0005NormalSpeedCore());
	
	private final int id;
	private final CoreData label;
	
	Core(int id, CoreData label) {
		this.id = id;
		this.label = label;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public CoreData getLabel() {
		return label;
	}
	
	public static CoreData getLabel(int id) {
		return DefaultEnum.getLabel(Core.values(), id);
	}
}