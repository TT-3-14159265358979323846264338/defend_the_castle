package defendthecastle.selectstage;

import java.util.List;

import savedata.SaveGameProgress;

//クリアデータ取込み
class ProgressData{
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	
	protected ProgressData() {
		SaveGameProgress.load();
	}
	
	protected void save(int select) {
		SaveGameProgress.save(SaveGameProgress.getClearStatus(), SaveGameProgress.getMeritStatus(), SaveGameProgress.getMedal(), select);
	}
	
	protected List<Boolean> getClearStatus(){
		return SaveGameProgress.getClearStatus();
	}
	
	protected List<List<Boolean>> getMeritStatus(){
		return SaveGameProgress.getMeritStatus();
	}
	
	protected int getSelectStage() {
		return SaveGameProgress.getSelectStage();
	}
}