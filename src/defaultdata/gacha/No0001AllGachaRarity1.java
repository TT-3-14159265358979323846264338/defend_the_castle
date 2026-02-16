package defaultdata.gacha;

import defaultdata.DefaultGacha;
import savedata.SaveGameProgress;

public class No0001AllGachaRarity1 extends GachaData{
	@Override
	public String getName() {
		return "闇鍋ガチャ Lv1";
	}

	@Override
	public boolean canActivate(SaveGameProgress SaveGameProgress) {
		return true;
	}

	@Override
	protected void createLineup() {
		addCore(DefaultGacha.CORE_SET_1, 50);
		addWeapon(DefaultGacha.WEAPON_SET_1, 50);
	}
}