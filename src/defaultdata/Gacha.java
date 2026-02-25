package defaultdata;

import defaultdata.gacha.*;

public enum Gacha {
	ALL_RARITY1(new No0001AllGachaRarity1()),
	CORE_RARITY1(new No0002CoreGachaRarity1()),
	WEAPON_RARITY1(new No0003WeaponGachaRarity1());
	
	private final GachaData gachaData;
	
	Gacha(GachaData gachaData) {
		this.gachaData = gachaData;
	}

	public GachaData getGachaData() {
		return gachaData;
	}
}