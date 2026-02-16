package defaultdata;

import static defaultdata.DefaultUnit.*;

import java.util.Arrays;
import java.util.List;

import defaultdata.gacha.*;

public class DefaultGacha {
	/*
	 * 
	 * ガチャで使用するコアラインナップ
	 * 
	 */
	public static final List<Integer> CORE_SET_1 = Arrays.asList(ATACK_CORE, DEFENCE_CORE, RANGE_CORE, HEAL_CORE, SPEED_CORE);
	
	/*
	 * 
	 * ガチャで使用する武器ラインナップ
	 * 
	 */
	public static final List<Integer> WEAPON_SET_1 = Arrays.asList(SWORD, BOW, SMALL_SHIELD, FIRST_AID_KIT, FLAME_ROD, WIND_CUTTER);
	
	//ガチャリスト
	public static final List<GachaData> GACHA_DATA = Arrays.asList(
			new No0001AllGachaRarity1(),
			new No0002CoreGachaRarity1(),
			new No0003WeaponGachaRarity1()
			);
}