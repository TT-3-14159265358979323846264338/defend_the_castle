package defendthecastle.itemget;

import savedata.SaveGameProgress;
import savedata.SaveItem;

//保有メダル
class HoldMedal{
	private SaveGameProgress SaveGameProgress = new SaveGameProgress();
	private SaveItem SaveItem = new SaveItem();
	private GachaInformation GachaInformation;
	private final static int USE = 100;
	
	protected HoldMedal() {
		SaveGameProgress.load();
		SaveItem.load();
	}
	
	protected void install(GachaInformation GachaInformation) {
		this.GachaInformation = GachaInformation;
	}
	
	protected void save() {
		SaveItem.save();
	}
	
	protected SaveGameProgress getSaveData() {
		return SaveGameProgress;
	}
	
	protected int getMedal() {
		return SaveItem.getMedalNumber();
	}
	
	protected void recountMedal() {
		SaveItem.reduceMedal(useMedal());
	}
	
	protected boolean canPossessMedal() {
		return useMedal() <= getMedal();
	}
	
	protected int useMedal() {
		return GachaInformation.getRepeatNumber() * USE;
	}
}