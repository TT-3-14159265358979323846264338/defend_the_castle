package defendthecastle.selectstage;

import java.util.List;
import java.util.stream.IntStream;

import defaultdata.DefaultStage;
import savedata.SaveGameProgress;
import savedata.SaveSelect;

//クリアデータ取込み
class ProgressData{
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	private SaveSelect SaveSelect = new SaveSelect();
	private List<Integer> stageNumberList;
	
	protected ProgressData() {
		SaveGameProgress.load();
		SaveSelect.load();
		stageNumberList = IntStream.range(0, DefaultStage.STAGE_DATA.size())
				.filter(i -> DefaultStage.STAGE_DATA.get(i).canActivate(SaveGameProgress))
				.boxed()
				.toList();
	}
	
	protected void save(int select) {
		SaveGameProgress.save();
	}
	
	protected List<Integer> getActivateStage(){
		return stageNumberList;
	}
	
	protected List<Boolean> getClearStatus(){
		return stageNumberList.stream().map(i -> SaveGameProgress.getStageStatus().get(i)).toList();
	}
	
	protected List<List<Boolean>> getMeritStatus(){
		return stageNumberList.stream().map(i -> SaveGameProgress.getMeritData(i).getMeritClearList()).toList();
	}
	
	protected int getSelectStage() {
		return stageNumberList.indexOf(SaveSelect.getStageSelectNumber());
	}
	
	protected List<String> getStageName(){
		return stageNumberList.stream().map(i -> DefaultStage.STAGE_DATA.get(i).getName()).toList();
	}
	
	protected boolean canAllActivate() {
		return stageNumberList.size() == DefaultStage.STAGE_DATA.size();
	}
}