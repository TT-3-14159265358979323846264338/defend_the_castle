package defaultdata;

import java.util.HashMap;
import java.util.Map;

import defaultdata.core.CoreData;
import defaultdata.core.No0000NormalCore;
import defaultdata.core.No0001NormalAtackCore;
import defaultdata.core.No0002NormalDefenseCore;
import defaultdata.core.No0003NormalRangeCore;
import defaultdata.core.No0004NormalHealCore;
import defaultdata.core.No0005NormalSpeedCore;
import defaultdata.weapon.No0000JapaneseSword;
import defaultdata.weapon.No0001Bow;
import defaultdata.weapon.WeaponData;

//ユニットデータ
public class DefaultUnit {
	//データコード変換
	public final static Map<Integer, String> CORE_WEAPON_MAP = new HashMap<>();
	static {
		CORE_WEAPON_MAP.put(0, "攻撃倍率");
		CORE_WEAPON_MAP.put(1, "射程倍率");
		CORE_WEAPON_MAP.put(2, "攻撃速度倍率");
		CORE_WEAPON_MAP.put(3, "攻撃対象倍率");
	}
	public final static Map<Integer, String> CORE_UNIT_MAP = new HashMap<>();
	static {
		CORE_UNIT_MAP.put(0, "最大HP倍率");
		CORE_UNIT_MAP.put(1, "HP倍率");
		CORE_UNIT_MAP.put(2, "防御倍率");
		CORE_UNIT_MAP.put(3, "回復倍率");
		CORE_UNIT_MAP.put(4, "足止め数倍率");
		CORE_UNIT_MAP.put(5, "配置コスト倍率");
	}
	public final static Map<Integer, String> WEAPON_WEAPON_MAP = new HashMap<>();
	static {
		WEAPON_WEAPON_MAP.put(0, "攻撃");
		WEAPON_WEAPON_MAP.put(1, "射程");
		WEAPON_WEAPON_MAP.put(2, "攻撃速度");
		WEAPON_WEAPON_MAP.put(3, "攻撃対象");
	}
	public final static Map<Integer, String> WEAPON_UNIT_MAP = new HashMap<>();
	static {
		WEAPON_UNIT_MAP.put(0, "最大HP");
		WEAPON_UNIT_MAP.put(1, "HP");
		WEAPON_UNIT_MAP.put(2, "防御");
		WEAPON_UNIT_MAP.put(3, "回復");
		WEAPON_UNIT_MAP.put(4, "足止め数");
		WEAPON_UNIT_MAP.put(5, "配置コスト");
	}
	
	public final static int NEAR = 0;
	public final static int FAR = 1;
	public final static int ALL = 2;
	
	public final static Map<Integer, String> DISTANCE_MAP = new HashMap<>();
	static {
		DISTANCE_MAP.put(NEAR,"近接");
		DISTANCE_MAP.put(FAR,"遠隔");
		DISTANCE_MAP.put(ALL,"遠近");
	}
	
	public final static int ONE = 0;
	public final static int BOTH = 1;
	
	public final static Map<Integer, String> HANDLE_MAP = new HashMap<>();
	static {
		HANDLE_MAP.put(ONE,"片手");
		HANDLE_MAP.put(BOTH,"両手");
	}
	
	public final static int SLASH = 0;
	public final static int PIERCE = 1;
	public final static int STRIKE = 2;
	public final static int IMPACT = 3;
	public final static int FLAME = 4;
	public final static int WATER = 5;
	public final static int WIND = 6;
	public final static int SOIL = 7;
	public final static int THUNDER = 8;
	public final static int HOLY = 9;
	public final static int DARK = 10;
	public final static int SUPPORT = 11;
	
	public final static Map<Integer, String> ELEMENT_MAP = new HashMap<>();
	static {
		ELEMENT_MAP.put(SLASH,"斬撃");
		ELEMENT_MAP.put(PIERCE,"刺突");
		ELEMENT_MAP.put(STRIKE,"殴打");
		ELEMENT_MAP.put(IMPACT,"衝撃");
		ELEMENT_MAP.put(FLAME,"炎");
		ELEMENT_MAP.put(WATER,"水");
		ELEMENT_MAP.put(WIND,"風");
		ELEMENT_MAP.put(SOIL,"土");
		ELEMENT_MAP.put(THUNDER,"雷");
		ELEMENT_MAP.put(HOLY,"聖");
		ELEMENT_MAP.put(DARK,"闇");
		ELEMENT_MAP.put(SUPPORT,"支援");
	}
	
	//コアコード変換
	public final static int NORMAL_CORE = 0;
	public final static int ATACK_CORE = 1;
	public final static int DEFENCE_CORE = 2;
	public final static int RANGE_CORE = 3;
	public final static int HEAL_CORE = 4;
	public final static int SPEED_CORE = 5;
	
	public final static Map<Integer, CoreData> CORE_DATA_MAP = new HashMap<>();
	static {
		CORE_DATA_MAP.put(NORMAL_CORE, new No0000NormalCore());
		CORE_DATA_MAP.put(ATACK_CORE, new No0001NormalAtackCore());
		CORE_DATA_MAP.put(DEFENCE_CORE, new No0002NormalDefenseCore());
		CORE_DATA_MAP.put(RANGE_CORE, new No0003NormalRangeCore());
		CORE_DATA_MAP.put(HEAL_CORE, new No0004NormalHealCore());
		CORE_DATA_MAP.put(SPEED_CORE, new No0005NormalSpeedCore());
	}
	
	//武器コード変換
	public final static int SWORD = 0;
	public final static int BOW = 1;
	
	public final static Map<Integer, WeaponData> WEAPON_DATA_MAP = new HashMap<>();
	static {
		WEAPON_DATA_MAP.put(SWORD, new No0000JapaneseSword());
		WEAPON_DATA_MAP.put(BOW, new No0001Bow());
	}
}