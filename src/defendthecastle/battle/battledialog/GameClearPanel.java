package defendthecastle.battle.battledialog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JLabel;

import defaultdata.Difficulty;
import defaultdata.Stage;
import defendthecastle.battle.BattleEnemy;
import defendthecastle.battle.BattleFacility;
import defendthecastle.battle.BattleUnit;
import defendthecastle.battle.GameData;

class GameClearPanel extends ClearMerit{
	private final List<Boolean> thisClearList;
	
	GameClearPanel(Stage stage, BattleUnit[] unitMainData, BattleUnit[] unitLeftData, BattleFacility[] facilityData, BattleEnemy[] enemyData, GameData gameData, Difficulty difficulty) {
		thisClearList = stage.getLabel().canClearMerit(unitMainData, unitLeftData, facilityData, enemyData, gameData, difficulty);
		beforeSet(stage);
		updateClearData(stage);
		clearLabel = IntStream.range(0, meritLabel.length).mapToObj(i -> new JLabel(clearComment(i))).toArray(JLabel[]::new);
		afterSet();
	}
	
	void updateClearData(Stage stage) {
		List<Boolean> newClearList = newClearMerit();
		updateClearStage();
		saveGameProgress.save();
		stage.getLabel().giveClearReward(newClearList);
	}
	
	List<Boolean> newClearMerit(){
		List<Boolean> newClearList = new ArrayList<>();
		for(int i = 0; i < thisClearList.size(); i++){
			if(thisClearList.get(i) && !hasCleared(i)){
				getMeritData().setMeritClear(i, true);
				newClearList.add(true);
				continue;
			}
			newClearList.add(false);
		}
		return newClearList;
	}
	
	void updateClearStage() {
		if(getMeritData().getMeritClearList().stream().allMatch(i -> i)) {
			saveGameProgress.setStage(stageNumber, true);
		}
	}
	
	String clearComment(int number) {
		return thisClearList.get(number)? "clear": "";
	}
}