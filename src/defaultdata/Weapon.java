package defaultdata;

import defaultdata.weapon.*;

public enum Weapon implements DefaultEnum<WeaponData>{
	SWORD(0, new No0000JapaneseSword()),
	BOW(1, new No0001Bow()),
	SMALL_SHIELD(2, new No0002SmallShield()),
	FIRST_AID_KIT(3, new No0003FirstAidKit()),
	FLAME_ROD(4, new No0004FlameRod()),
	WIND_CUTTER(5, new No0005WindCutter());
	
	public static final int NO_WEAPON = -1;
	private final int id;
	private final WeaponData label;
	
	Weapon(int id, WeaponData label) {
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public WeaponData getLabel() {
		return label;
	}
	
	public static WeaponData getLabel(int id) {
		return DefaultEnum.getLabel(Weapon.values(), id);
	}
}