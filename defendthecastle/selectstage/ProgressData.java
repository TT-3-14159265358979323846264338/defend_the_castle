package defendthecastle.selectstage;

import java.util.List;
import java.util.stream.IntStream;

import defaultdata.DefaultStage;
import savedata.SaveGameProgress;

//クリアデータ取込み
class ProgressData{
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	private List<Integer> stageNumberList;
	
	protected ProgressData() {
		SaveGameProgress.load();
		stageNumberList = IntStream.range(0, DefaultStage.STAGE_DATA.size())
				.filter(i -> DefaultStage.STAGE_DATA.get(i).canActivate(SaveGameProgress))
				.boxed()
				.toList();
	}
	
	protected void save(int select) {
		SaveGameProgress.save(SaveGameProgress.getClearStatus(), SaveGameProgress.getMeritStatus(), SaveGameProgress.getMedal(), stageNumberList.get(select));
	}
	
	protected List<Integer> getActivateStage(){
		return stageNumberList;
	}
	
	protected List<Boolean> getClearStatus(){
		return stageNumberList.stream().map(i -> SaveGameProgress.getClearStatus().get(i)).toList();
	}
	
	protected List<List<Boolean>> getMeritStatus(){
		return stageNumberList.stream().map(i -> SaveGameProgress.getMeritStatus().get(i)).toList();
	}
	
	protected int getSelectStage() {
		return stageNumberList.indexOf(SaveGameProgress.getSelectStage());
	}
	
	protected List<String> getStageName(){
		return stageNumberList.stream().map(i -> DefaultStage.STAGE_DATA.get(i).getName()).toList();
	}
	
	protected boolean canAllActivate() {
		return stageNumberList.size() == DefaultStage.STAGE_DATA.size();
	}
}