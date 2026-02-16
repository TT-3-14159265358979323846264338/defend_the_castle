package defendthecastle.itemget;

import savedata.SaveGameProgress;
import savedata.SaveItem;

//保有メダル
class HoldMedal{
	private final SaveGameProgress saveGameProgress;
	private final SaveItem saveItem;
	private GachaInformation gachaInformation;
	private final static int USE = 100;
	
	HoldMedal() {
		saveGameProgress = createSaveGameProgress();
		saveItem = createSaveItem();
		saveGameProgress.load();
		saveItem.load();
	}
	
	SaveGameProgress createSaveGameProgress() {
		return new SaveGameProgress();
	}
	
	SaveItem createSaveItem() {
		return new SaveItem();
	}
	
	void install(GachaInformation gachaInformation) {
		this.gachaInformation = gachaInformation;
	}
	
	void save() {
		saveItem.save();
	}
	
	SaveGameProgress getSaveData() {
		return saveGameProgress;
	}
	
	int getMedal() {
		return saveItem.getMedalNumber();
	}
	
	void recountMedal() {
		saveItem.reduceMedal(useMedal());
	}
	
	boolean canPossessMedal() {
		return useMedal() <= getMedal();
	}
	
	int useMedal() {
		return gachaInformation.getRepeatNumber() * USE;
	}
}