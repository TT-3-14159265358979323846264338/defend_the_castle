package defaultdata.gacha;

import defaultdata.DefaultGacha;
import savedata.SaveGameProgress;

public class No0003WeaponGachaRarity1 extends GachaData{
	@Override
	public String getName() {
		return "武器ガチャ Lv1";
	}

	@Override
	public boolean canActivate(SaveGameProgress SaveGameProgress) {
		return hasClearedMerit(SaveGameProgress, 0, 5, -1);
	}

	@Override
	protected void createLineup() {
		addWeapon(DefaultGacha.WEAPON_SET_1, 100);
	}
}