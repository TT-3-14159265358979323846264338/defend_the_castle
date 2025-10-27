package defaultdata;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import defaultdata.facility.*;
import defaultdata.stage.*;

//ステージデータ
public class DefaultStage {
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
		UNIT_MAP.put(4, "足止め数");
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
	
	//ステージリスト
	public static final List<StageData> STAGE_DATA = Arrays.asList(
			new No0000Stage1(), 
			new No0001Stage2(),
			new No0002Stage3(),
			new No0003Stage4()
			);
	
	//設備コード変換
	public static final int CASTLE = 0;
	public static final int GATE = 1;
	public static final int STRONGHOLD = 2;
		
	public static final Map<Integer, FacilityData> FACILITY_DATA_MAP = new HashMap<>();
	static {
		FACILITY_DATA_MAP.put(CASTLE, new No0000Castle());
		FACILITY_DATA_MAP.put(GATE, new No0001Gate());
		FACILITY_DATA_MAP.put(STRONGHOLD, new No0002Stronghold());
	}
	
	//配置マス画像ファイル
	public static final List<String> PLACEMENT_NAME_LIST = Arrays.asList(
			"image/field/near placement.png",
			"image/field/far placement.png",
			"image/field/all placement.png"
			);
	
	public List<BufferedImage> getPlacementImage(double ratio){
		return EditImage.input(PLACEMENT_NAME_LIST, ratio);
	}
}