package defaultdata.gacha;

import savedata.SaveGameProgress;

public class No0002CoreGachaRarity1 extends GachaData{
	@Override
	public String getName() {
		return "コアガチャ Lv1";
	}

	@Override
	public boolean canActivate(SaveGameProgress saveGameProgress) {
		return hasClearedMerit(saveGameProgress, 0, 5, -1);
	}

	@Override
	protected void createLineup() {
		addCore(CORE_SET_1(), 100);
	}
}