package defendthecastle.itemget;

import savedata.SaveGameProgress;

//保有メダル
class HoldMedal{
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	private GachaInformation GachaInformation;;
	private int medal;
	private final static int USE = 100;
	
	protected HoldMedal() {
		SaveGameProgress.load();
		medal = SaveGameProgress.getMedal();
	}
	
	protected void install(GachaInformation GachaInformation) {
		this.GachaInformation = GachaInformation;
	}
	
	protected void save() {
		SaveGameProgress.save(SaveGameProgress.getClearStatus(), SaveGameProgress.getMeritStatus(), medal, SaveGameProgress.getSelectStage());
	}
	
	protected SaveGameProgress getSaveData() {
		return SaveGameProgress;
	}
	
	protected int getMedal() {
		return medal;
	}
	
	protected void recountMedal() {
		medal -= useMedal();
	}
	
	protected boolean canPossessMedal() {
		return useMedal() <= medal;
	}
	
	protected int useMedal() {
		return GachaInformation.getRepeatNumber() * USE;
	}
}