package defaultdata;

import java.util.HashMap;
import java.util.Map;

import defaultdata.enemy.*;

//敵兵データ
public class DefaultEnemy {
	//データコード変換
	public static final Map<Integer, String> WEAPON_MAP = new HashMap<>();
	static {
		WEAPON_MAP.put(0, "攻撃");
		WEAPON_MAP.put(1, "射程");
		WEAPON_MAP.put(2, "攻撃速度");
		WEAPON_MAP.put(3, "攻撃対象");
	}
	public static final Map<Integer, String> UNIT_MAP = new HashMap<>();
	static {
		UNIT_MAP.put(0, "最大HP");
		UNIT_MAP.put(1, "HP");
		UNIT_MAP.put(2, "防御");
		UNIT_MAP.put(3, "回復");
		UNIT_MAP.put(4, "移動速度");
		UNIT_MAP.put(5, "撃破コスト");
	}
	
	public static final int GROUND = 0;
	public static final int FLIGHT = 1;
	public static final int ON_WATER = 2;
	public static final int NO_MOVE = 3;
	
	public static final Map<Integer, String> MOVE_MAP = new HashMap<>();
	static {
		MOVE_MAP.put(GROUND, "地上");
		MOVE_MAP.put(FLIGHT, "飛行");
		MOVE_MAP.put(ON_WATER, "水上");
		MOVE_MAP.put(NO_MOVE, "移動不可");
	}
	
	public static final int NORMAL = 0;
	public static final int FACILITY = 1;
	public static final int BOSS = 2;
	
	public static final Map<Integer, String> TYPE_MAP = new HashMap<>();
	static {
		TYPE_MAP.put(NORMAL, "一般");
		TYPE_MAP.put(FACILITY, "設備");
		TYPE_MAP.put(BOSS, "ボス");
	}
	
	public static final int SLASH = 0;
	public static final int PIERCE = 1;
	public static final int STRIKE = 2;
	public static final int IMPACT = 3;
	public static final int FLAME = 4;
	public static final int WATER = 5;
	public static final int WIND = 6;
	public static final int SOIL = 7;
	public static final int THUNDER = 8;
	public static final int HOLY = 9;
	public static final int DARK = 10;
	public static final int SUPPORT = 11;
	
	public static final Map<Integer, String> ELEMENT_MAP = new HashMap<>();
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
	public static final int BLUE_SLIME = 0;
	public static final int RED_SLIME = 1;
	public static final int GREEN_SLIME = 2;
	public static final int YELLOW_SLIME = 3;
	public static final int HIGH_SLIME = 4;
	public static final int CASTLE = 5;
	public static final int FRONT_GATE = 6;
	public static final int SIDE_GATE = 7;
	
	public static final Map<Integer, EnemyData> DATA_MAP = new HashMap<>();
	static {
		DATA_MAP.put(BLUE_SLIME, new No0000BlueSlime());
		DATA_MAP.put(RED_SLIME, new No0001RedSlime());
		DATA_MAP.put(GREEN_SLIME, new No0002GreenSlime());
		DATA_MAP.put(YELLOW_SLIME, new No0003YellowSlime());
		DATA_MAP.put(HIGH_SLIME, new No0004HighSlime());
		DATA_MAP.put(CASTLE, new No0005Castle());
		DATA_MAP.put(FRONT_GATE, new No0006FrontGate());
		DATA_MAP.put(SIDE_GATE, new No0007SideGate());
	}
}