package defendthecastle.selectstage;

import java.util.List;
import java.util.stream.IntStream;

import defaultdata.DefaultStage;
import savedata.SaveGameProgress;
import savedata.SaveSelect;

//クリアデータ取込み
class ProgressData{
	private final SaveGameProgress saveGameProgress;
	private final SaveSelect saveSelect;
	private final List<Integer> activeStageList;
	
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
	
	List<Integer> activeStageIndex(){
		return IntStream.range(0, DefaultStage.STAGE_DATA.size())
				.filter(i -> DefaultStage.STAGE_DATA.get(i).canActivate(saveGameProgress))
				.boxed()
				.toList();
	}
	
	void save(int select) {
		saveSelect.setStageSelectNumber(select);
		saveSelect.save();
	}
	
	List<Integer> getActivateStage(){
		return activeStageList;
	}
	
	List<Boolean> getClearStatus(){
		return activeStageList.stream().map(i -> saveGameProgress.getStageStatus().get(i)).toList();
	}
	
	List<List<Boolean>> getMeritStatus(){
		return activeStageList.stream().map(i -> saveGameProgress.getMeritData(i).getMeritClearList()).toList();
	}
	
	int getSelectStage() {
		return activeStageList.indexOf(saveSelect.getStageSelectNumber());
	}
	
	List<String> getStageName(){
		return activeStageList.stream().map(i -> DefaultStage.STAGE_DATA.get(i).getName()).toList();
	}
	
	boolean canAllActivate() {
		return activeStageList.size() == DefaultStage.STAGE_DATA.size();
	}
}