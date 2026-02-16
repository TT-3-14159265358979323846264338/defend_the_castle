package defaultdata.gacha;

import defaultdata.DefaultGacha;
import savedata.SaveGameProgress;

public class No0002CoreGachaRarity1 extends GachaData{
	@Override
	public String getName() {
		return "コアガチャ Lv1";
	}

	@Override
	public boolean canActivate(SaveGameProgress SaveGameProgress) {
		return hasClearedMerit(SaveGameProgress, 0, 5, -1);
	}

	@Override
	protected void createLineup() {
		addCore(DefaultGacha.CORE_SET_1, 100);
	}
}