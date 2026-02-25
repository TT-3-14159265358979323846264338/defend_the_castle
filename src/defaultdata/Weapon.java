package defaultdata;

import defaultdata.weapon.*;

public enum Weapon implements Id{
	SWORD(0, new No0000JapaneseSword()),
	BOW(1, new No0001Bow()),
	SMALL_SHIELD(2, new No0002SmallShield()),
	FIRST_AID_KIT(3, new No0003FirstAidKit()),
	FLAME_ROD(4, new No0004FlameRod()),
	WIND_CUTTER(5, new No0005WindCutter());
	
	public static final int NO_WEAPON = -1;
	private final int id;
	private final WeaponData WeaponData;
	
	Weapon(int id, WeaponData WeaponData) {
		this.id = id;
		this.WeaponData = WeaponData;
	}

	public int getId() {
		return id;
	}

	public WeaponData getWeaponData() {
		return WeaponData;
	}
	
	public static WeaponData getWeaponData(int id) {
		for(Weapon i: values()) {
			if(i.getId() == id) {
				return i.getWeaponData();
			}
		}
		return null;
	}
}