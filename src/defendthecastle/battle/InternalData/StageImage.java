package defendthecastle.battle.InternalData;

import java.awt.image.BufferedImage;
import java.util.List;

import defaultdata.Placement;
import defaultdata.stage.StageData;

public class StageImage {
	private final StageData stageData;
	private final BattleEnemy[] enemyData;
	private final GameTimer gameTimer;
	private final BufferedImage stageImage;
	private final List<BufferedImage> placementImage;
	private final List<List<List<Double>>> placementList;
	private List<List<Boolean>> usePlacementList;
	private final int STAGE_RATIO = 2;
	private final int PLACEMENT_RATIO = 4;
	
	public StageImage(StageData stageData, BattleEnemy[] enemyData, GameTimer gameTimer){
		this.stageData = stageData;
		this.enemyData = enemyData;
		this.gameTimer = gameTimer;
		stageImage = stageData.getImage(STAGE_RATIO);
		placementImage = createPlacementImage();
		placementList = stageData.getPlacementPoint();
		updatePlacement();
	}
	
	List<BufferedImage> createPlacementImage(){
		return new Placement().getPlacementImage(PLACEMENT_RATIO);
	}
	
	void updatePlacement(){
		usePlacementList = stageData.canUsePlacement(gameTimer, enemyData);
	}

	public List<Boolean> getUsePlacementList(int code) {
		return usePlacementList.get(code);
	}

	public BufferedImage getStageImage() {
		return stageImage;
	}

	public BufferedImage getPlacementImage(int code) {
		return placementImage.get(code);
	}

	public int getPlacementSize() {
		return placementList.size();
	}
	
	public List<List<Double>> getPlacementList(int code) {
		return placementList.get(code);
	}
}