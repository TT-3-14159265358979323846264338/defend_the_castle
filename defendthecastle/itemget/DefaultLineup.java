package defendthecastle.itemget;

import static javax.swing.JOptionPane.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import defaultdata.DefaultUnit;

/*ガチャのラインナップ
 * LineupSetには、そのセットで排出されるユニット番号をリスト化
 * その後、各ガチャのmodeでどのLineupSetと排出確率を使用するか指定する(addCore(), addWeapon())
 * 排出確率は、1つのLineupSet全体の排出確率を指定する
 * 各ガチャ全体の排出確率を100に必ずすること
 */
class DefaultLineup{
	private final List<Integer> coreLineupSet1 = Arrays.asList(DefaultUnit.ATACK_CORE, DefaultUnit.DEFENCE_CORE, DefaultUnit.RANGE_CORE, DefaultUnit.HEAL_CORE, DefaultUnit.SPEED_CORE);
	//private final List<Integer> coreLineupSet2 = Arrays.asList();
	//private final List<Integer> coreLineupSet3 = Arrays.asList();
	private final List<Integer> weaponLineupSet1 = Arrays.asList(DefaultUnit.SWORD, DefaultUnit.BOW, DefaultUnit.SMALL_SHIELD);
	//private final List<Integer> weaponLineupSet2 = Arrays.asList();
	//private final List<Integer> weaponLineupSet3 = Arrays.asList();
	
	private int repeatCode = 0;
	private Map<Integer, Integer> repeatMap = new HashMap<>();{
		repeatMap.put(0, 1);
		repeatMap.put(1, 5);
		repeatMap.put(2, 10);
	}
	private int gachaModeCode = 0;
	private String[] gachaName = {
			"通常闇鍋ガチャ",
			"通常コアガチャ",
			"通常武器ガチャ"
	};
	private List<Integer> coreLineup = new ArrayList<>();
	private List<Double> coreRatio = new ArrayList<>();
	private List<Integer> weaponLineup = new ArrayList<>();
	private List<Double> weaponRatio = new ArrayList<>();
	
	protected String[] getGachaName() {
		return gachaName;
	}
	
	protected int getRepeatNumber() {
		return repeatMap.get(repeatCode);
	}
	
	protected void changeRepeatNumber() {
		repeatCode = (repeatCode < repeatMap.size() - 1)? repeatCode + 1: 0;
	}
	
	protected void changeGachaMode(int mode) {
		gachaModeCode = mode;
	}
	
	protected void setLineup() {
		coreLineup.clear();
		coreRatio.clear();
		weaponLineup.clear();
		weaponRatio.clear();
		switch(gachaModeCode) {
		case 0:
			addCore(coreLineupSet1, 50);
			addWeapon(weaponLineupSet1, 50);
			break;
		case 1:
			addCore(coreLineupSet1, 100);
			break;
		case 2:
			addWeapon(weaponLineupSet1, 100);
			break;
		default:
			break;
		}
	}
	
	private void addCore(List<Integer> lineupSet, double totalRatio) {
		coreLineup.addAll(lineupSet);
		coreRatio.addAll(getRatioList(lineupSet.size(), totalRatio));
	}
	
	private void addWeapon(List<Integer> lineupSet, double totalRatio) {
		weaponLineup.addAll(lineupSet);
		weaponRatio.addAll(getRatioList(lineupSet.size(), totalRatio));
	}
	
	private List<Double> getRatioList(int size, double totalRatio){
		return IntStream.range(0, size).mapToObj(i -> (double) (totalRatio / size)).toList();
	}
	
	protected boolean aptitudeTest() {
		double sum = coreRatio.stream().mapToDouble(Double::doubleValue).sum() + weaponRatio.stream().mapToDouble(Double::doubleValue).sum();
		if(Math.round(sum) != 100) {
			showMessageDialog(null, "このガチャモードは使用できません");
			return false;
		}
		return true;
	}
	
	protected List<Integer> getCoreLineup(){
		return coreLineup;
	}
	
	protected List<Double> getCoreRatio(){
		return coreRatio;
	}
	
	protected List<Integer> getWeaponLineup(){
		return weaponLineup;
	}
	
	protected List<Double> getWeaponRatio(){
		return weaponRatio;
	}
}