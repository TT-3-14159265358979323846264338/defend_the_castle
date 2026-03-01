package defaultdata;

import defaultdata.stage.*;

public enum Stage implements DefaultEnum<StageData>{
	STAGE1(0, new No0001Stage1()), 
	STAGE2(1, new No0002Stage2()),
	STAGE3(2, new No0003Stage3()),
	STAGE4(3, new No0004Stage4()),
	STAGE5(4, new No0005Stage5());
	
	private final int id;
	private final StageData label;
	
	Stage(int id, StageData label) {
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public StageData getLabel() {
		return label;
	}
	
	public static StageData getLabel(int id) {
		return DefaultEnum.getLabel(Stage.values(), id);
	}
	
	public static Stage getStage(int id) {
		return DefaultEnum.getEnum(Stage.values(), id);
	}
}