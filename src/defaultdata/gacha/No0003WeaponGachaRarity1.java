package defaultdata.gacha;

import savedata.SaveGameProgress;

public class No0003WeaponGachaRarity1 extends GachaData{
	@Override
	public String getName() {
		return "武器ガチャ Lv1";
	}

	@Override
	public boolean canActivate(SaveGameProgress saveGameProgress) {
		return hasClearedMerit(saveGameProgress, 0, 5, -1);
	}

	@Override
	protected void createLineup() {
		addWeapon(WEAPON_SET_1(), 100);
	}
}