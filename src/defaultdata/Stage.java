package defaultdata;

import defaultdata.stage.*;

public enum Stage {
	STAGE1(0, new No0001Stage1()), 
	STAGE2(1, new No0002Stage2()),
	STAGE3(2, new No0003Stage3()),
	STAGE4(3, new No0004Stage4()),
	STAGE5(4, new No0005Stage5());
	
	private final int id;
	private final StageData stageData;
	
	Stage(int id, StageData stageData) {
		this.id = id;
		this.stageData = stageData;
	}

	public int getId() {
		return id;
	}

	public StageData getStageData() {
		return stageData;
	}
	
	public static StageData getStageData(int id) {
		Stage stage = getStage(id);
		if(stage != null) {
			return stage.getStageData();
		}
		return null;
	}
	
	public static Stage getStage(int id) {
		for(Stage i: values()) {
			if(i.getId() == id) {
				return i;
			}
		}
		return null;
	}
}