package screendisplay;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import defaultdata.DefaultUnit;
import defaultdata.core.CoreData;
import defaultdata.weapon.WeaponData;

//ステータス計算
public class StatusCalculation{
	int rightType;
	List<Integer> rightElement;
	int rightAtackPattern;
	List<Integer> rightWeaponStatus;
	List<Integer> rightUnitStatus;
	int leftType;
	List<Integer> leftElement;
	int leftAtackPattern;
	List<Integer> leftWeaponStatus;
	List<Integer> leftUnitStatus;
	List<Double> coreWeaponStatus;
	List<Double> coreUnitStatus;
	
	List<Integer> rightWeaponCutList;
	List<Integer> leftWeaponCutList;
	List<Integer> coreCutList;
	
	public StatusCalculation(List<Integer> unitData) {
		try {
			WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(unitData.get(0));
			rightType = WeaponData.getDistance();
			rightElement = WeaponData.getElement();
			rightAtackPattern = WeaponData.getAtackPattern();
			rightWeaponStatus = WeaponData.getWeaponStatus();
			rightUnitStatus = WeaponData.getUnitStatus();
			rightWeaponCutList = WeaponData.getCutStatus();
		}catch(Exception noWeapon) {
			rightType = defaultType();
			rightElement = defaultElement();
			rightAtackPattern = defaultType();
			rightWeaponStatus = defaultWeaponStatus();
			rightUnitStatus = defaultUnitStatus();
			rightWeaponCutList = defaultCutList();
		}
		try {
			WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(unitData.get(2));
			leftType = WeaponData.getDistance();
			leftElement = WeaponData.getElement();
			leftAtackPattern = WeaponData.getAtackPattern();
			leftWeaponStatus = WeaponData.getWeaponStatus();
			leftUnitStatus = WeaponData.getUnitStatus();
			leftWeaponCutList = WeaponData.getCutStatus();
		}catch(Exception noWeapon) {
			leftType = defaultType();
			leftElement = defaultElement();
			leftAtackPattern = defaultType();
			leftWeaponStatus = defaultWeaponStatus();
			leftUnitStatus = defaultUnitStatus();
			leftWeaponCutList = defaultCutList();
		}
		CoreData CoreData = DefaultUnit.CORE_DATA_MAP.get(unitData.get(1));
		coreWeaponStatus = CoreData.getWeaponStatus();
		coreUnitStatus = CoreData.getUnitStatus();
		coreCutList = CoreData.getCutStatus();
	}
	
	private int defaultType() {
		return -1;
	}
	
	private List<Integer> defaultElement(){
		return Arrays.asList(-1);
	}
	
	private List<Integer> defaultWeaponStatus(){
		return Arrays.asList(0, 0, 0, 0);
	}
	
	private List<Integer> defaultUnitStatus(){
		return Arrays.asList(1000, 1000, 0, 0, 0, 0);
	}
	
	private List<Integer> defaultCutList(){
		return Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}
	
	public int getType() {
		if(rightType == -1 && leftType == -1) {
			return 2;
		}else if(rightType == leftType) {
			return leftType;
		}else if(0 <= rightType && 0 <= leftType){
			return 2;
		}else {
			return (rightType <= leftType)? leftType: rightType;
		}
	}
	
	public List<Integer> getRightWeaponStatus(){
		return getStatus(rightWeaponStatus);
	}
	
	public List<Integer> getLeftWeaponStatus(){
		return getStatus(leftWeaponStatus);
	}
	
	private List<Integer> getStatus(List<Integer> statusList){
		return IntStream.range(0, statusList.size()).mapToObj(i -> (int) (statusList.get(i) * coreWeaponStatus.get(i))).toList();
	}
	
	public List<Integer> getRightElement(){
		return rightElement;
	}
	
	public List<Integer> getLeftElement(){
		return leftElement;
	}
	
	public int getRightAtackPattern() {
		return rightAtackPattern;
	}
	
	public int getLeftAtackPattern() {
		return leftAtackPattern;
	}
	
	public List<Integer> getUnitStatus(){
		return IntStream.range(0, coreUnitStatus.size()).mapToObj(i -> (int) ((rightUnitStatus.get(i) + leftUnitStatus.get(i)) * coreUnitStatus.get(i))).toList();
	}
	
	public List<Integer> getCutStatus(){
		return IntStream.range(0, coreCutList.size()).mapToObj(i -> leftWeaponCutList.get(i) + coreCutList.get(i) + rightWeaponCutList.get(i)).toList();
	}
}