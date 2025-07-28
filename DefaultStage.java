package defaultdata;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import defaultdata.facility.FacilityData;
import defaultdata.facility.No0000Castle;
import defaultdata.facility.No0001Gate;
import defaultdata.stage.No0000Stage1;
import defaultdata.stage.No0001Stage2;
import defaultdata.stage.StageData;

//ステージデータ
public class DefaultStage {
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
		UNIT_MAP.put(4, "足止め数");
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
	
	//ステージコード変換
	public final static int STAGE1 = 0;
	public final static int STAGE2 = 1;
		
	public final static Map<Integer, StageData> STAGE_DATA_MAP = new HashMap<>();
	static {
		STAGE_DATA_MAP.put(STAGE1, new No0000Stage1());
		STAGE_DATA_MAP.put(STAGE2, new No0001Stage2());
	}
	
	//設備コード変換
	public final static int CASTLE = 0;
	public final static int GATE = 1;
		
	public final static Map<Integer, FacilityData> FACILITY_DATA_MAP = new HashMap<>();
	static {
		FACILITY_DATA_MAP.put(CASTLE, new No0000Castle());
		FACILITY_DATA_MAP.put(GATE, new No0001Gate());
	}
	
	//配置マス画像ファイル
		public final static List<String> PLACEMENT_NAME_LIST = Arrays.asList(
				"image/field/near placement.png",
				"image/field/far placement.png",
				"image/field/all placement.png"
				);
		
	public List<BufferedImage> getPlacementImage(double ratio){
		return new EditImage().input(PLACEMENT_NAME_LIST, ratio);
	}
}