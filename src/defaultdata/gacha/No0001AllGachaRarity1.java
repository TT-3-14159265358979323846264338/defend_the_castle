package defaultdata.gacha;

import savedata.SaveGameProgress;

public class No0001AllGachaRarity1 extends GachaData{
	@Override
	public String getName() {
		return "闇鍋ガチャ Lv1";
	}

	@Override
	public boolean canActivate(SaveGameProgress saveGameProgress) {
		return true;
	}

	@Override
	protected void createLineup() {
		addCore(CORE_SET_1(), 50);
		addWeapon(WEAPON_SET_1(), 50);
	}
}