package defendthecastle.itemget;

import savedata.SaveGameProgress;

//保有メダル
class HoldMedal{
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	private DefaultLineup DefaultLineup;;
	private int medal;
	private final static int USE = 100;
	
	protected HoldMedal(DefaultLineup DefaultLineup) {
		SaveGameProgress.load();
		this.DefaultLineup = DefaultLineup;
		medal = SaveGameProgress.getMedal();
	}
	
	protected void save() {
		SaveGameProgress.save(SaveGameProgress.getClearStatus(), SaveGameProgress.getMeritStatus(), medal, SaveGameProgress.getSelectStage());
	}
	
	protected int getMedal() {
		return medal;
	}
	
	protected void recountMedal() {
		medal -= useMedal();
	}
	
	protected boolean checkMedal() {
		return useMedal() <= medal;
	}
	
	protected int useMedal() {
		return DefaultLineup.getRepeatNumber() * USE;
	}
}