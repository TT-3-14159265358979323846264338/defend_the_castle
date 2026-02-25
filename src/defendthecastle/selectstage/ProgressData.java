package defendthecastle.selectstage;

import java.util.List;
import java.util.stream.Stream;

import defaultdata.Stage;
import savedata.SaveGameProgress;
import savedata.SaveSelect;

//クリアデータ取込み
class ProgressData{
	private final SaveGameProgress saveGameProgress;
	private final SaveSelect saveSelect;
	private final List<Stage> activeStageList;
	
	ProgressData() {
		saveGameProgress = createSaveGameProgress();
		saveSelect = createSaveSelect();
		saveGameProgress.load();
		saveSelect.load();
		activeStageList = activeStageIndex();
	}
	
	SaveGameProgress createSaveGameProgress() {
		return new SaveGameProgress();
	}
	
	SaveSelect createSaveSelect() {
		return new SaveSelect();
	}
	
	List<Stage> activeStageIndex(){
		return Stream.of(Stage.values())
				.filter(i -> i.getStageData().canActivate(saveGameProgress))
				.toList();
	}
	
	void save(int select) {
		saveSelect.setStageSelectNumber(activeStageList.get(select).getId());
		saveSelect.save();
	}
	
	List<Stage> getActivateStage(){
		return activeStageList;
	}
	
	List<Boolean> getClearStatus(){
		return activeStageList.stream().map(i -> saveGameProgress.getStageStatus().get(i.getId())).toList();
	}
	
	List<List<Boolean>> getMeritStatus(){
		return activeStageList.stream().map(i -> saveGameProgress.getMeritData(i.getId()).getMeritClearList()).toList();
	}
	
	int getSelectStage() {
		return activeStageList.indexOf(Stage.getStage(saveSelect.getStageSelectNumber()));
	}
	
	List<String> getStageName(){
		return activeStageList.stream().map(i -> i.getStageData().getName()).toList();
	}
	
	boolean canAllActivate() {
		return activeStageList.size() == Stage.values().length;
	}
}