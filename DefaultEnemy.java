package defaultdata;

import java.util.HashMap;
import java.util.Map;

import defaultdata.enemy.EnemyData;
import defaultdata.enemy.No0000BlueSlime;
import defaultdata.enemy.No0001RedSlime;

//敵兵データ
public class DefaultEnemy {
	//データコード変換
	public final static Map<Integer, String> WEAPON_MAP = new HashMap<>();
	static {
		WEAPON_MAP.put(0, "攻撃");
		WEAPON_MAP.put(1, "射程");
		WEAPON_MAP.put(2, "攻撃速度");
		WEAPON_MAP.put(3, "攻撃対象");
	}
	public final static Map<Integer, String> UNIT_MAP = new HashMap<>();
	static {
		UNIT_MAP.put(0, "最大HP");
		UNIT_MAP.put(1, "HP");
		UNIT_MAP.put(2, "防御");
		UNIT_MAP.put(3, "回復");
		UNIT_MAP.put(4, "移動速度");
		UNIT_MAP.put(5, "撃破コスト");
	}
	
	public final static int GROUND = 0;
	public final static int FLIGHT = 1;
	public final static int ON_WATER = 2;
	
	public final static Map<Integer, String> MOVE_MAP = new HashMap<>();
	static {
		MOVE_MAP.put(GROUND, "地上");
		MOVE_MAP.put(FLIGHT, "飛行");
		MOVE_MAP.put(ON_WATER, "水上");
	}
	
	public final static int NORMAL = 0;
	public final static int BOSS = 1;
	
	public final static Map<Integer, String> TYPE_MAP = new HashMap<>();
	static {
		TYPE_MAP.put(NORMAL, "一般");
		TYPE_MAP.put(BOSS, "ボス");
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
	
	//敵コード変換
	public final static int BLUE_SLIME = 0;
	public final static int RED_SLIME = 1;
	
	public final static Map<Integer, EnemyData> DATA_MAP = new HashMap<>();
	static {
		DATA_MAP.put(BLUE_SLIME, new No0000BlueSlime());
		DATA_MAP.put(RED_SLIME, new No0001RedSlime());
	}
}