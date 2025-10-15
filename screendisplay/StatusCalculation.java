package screendisplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import defaultdata.DefaultUnit;
import defaultdata.core.CoreData;
import defaultdata.weapon.WeaponData;

//ステータス計算
public class StatusCalculation{
	private final int NO_DATA = -1;
	
	private int rightType;
	private List<Integer> rightElement;
	private int rightAtackPattern;
	private List<Integer> rightWeaponStatus;
	private List<Integer> rightUnitStatus;
	private List<Integer> rightWeaponCutList;
	private List<List<Double>> rightBuffList = new ArrayList<>();
	
	private int leftType;
	private List<Integer> leftElement;
	private int leftAtackPattern;
	private List<Integer> leftWeaponStatus;
	private List<Integer> leftUnitStatus;
	private List<Integer> leftWeaponCutList;
	private List<List<Double>> leftBuffList = new ArrayList<>();
	
	private List<Double> coreWeaponStatus;
	private List<Double> coreUnitStatus;
	private List<Integer> coreCutList;
	
	public StatusCalculation(List<Integer> unitData) {
		rightWeaponInstall(unitData.get(DefaultUnit.RIGHT_WEAPON));
		coreInstall(unitData.get(DefaultUnit.CORE));
		leftWeaponInstall(unitData.get(DefaultUnit.LEFT_WEAPON));
	}
	
	private void rightWeaponInstall(int number) {
		try {
			WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(number);
			rightType = WeaponData.getDistance();
			rightElement = WeaponData.getElement();
			rightAtackPattern = WeaponData.getAtackPattern();
			rightWeaponStatus = WeaponData.getWeaponStatus();
			rightUnitStatus = WeaponData.getUnitStatus();
			rightWeaponCutList = WeaponData.getCutStatus();
			if(!WeaponData.getBuff().isEmpty()) {
				rightBuffList.addAll(WeaponData.getBuff());
			}
		}catch(Exception noWeapon) {
			rightType = defaultType();
			rightElement = defaultElement();
			rightAtackPattern = defaultType();
			rightWeaponStatus = defaultWeaponStatus();
			rightUnitStatus = defaultUnitStatus();
			rightWeaponCutList = defaultCutList();
		}
	}
	
	private void coreInstall(int number) {
		CoreData CoreData = DefaultUnit.CORE_DATA_MAP.get(number);
		coreWeaponStatus = CoreData.getWeaponStatus();
		coreUnitStatus = CoreData.getUnitStatus();
		coreCutList = CoreData.getCutStatus();
		if(!CoreData.getBuff().isEmpty()) {
			rightBuffList.addAll(CoreData.getBuff());
		}
	}
	
	private void leftWeaponInstall(int number) {
		try {
			WeaponData WeaponData = DefaultUnit.WEAPON_DATA_MAP.get(number);
			leftType = WeaponData.getDistance();
			leftElement = WeaponData.getElement();
			leftAtackPattern = WeaponData.getAtackPattern();
			leftWeaponStatus = WeaponData.getWeaponStatus();
			leftUnitStatus = WeaponData.getUnitStatus();
			leftWeaponCutList = WeaponData.getCutStatus();
			if(!WeaponData.getBuff().isEmpty()) {
				leftBuffList.addAll(WeaponData.getBuff());
			}
		}catch(Exception noWeapon) {
			leftType = defaultType();
			leftElement = defaultElement();
			leftAtackPattern = defaultType();
			leftWeaponStatus = defaultWeaponStatus();
			leftUnitStatus = defaultUnitStatus();
			leftWeaponCutList = defaultCutList();
		}
	}
	
	private int defaultType() {
		return NO_DATA;
	}
	
	private List<Integer> defaultElement(){
		return Arrays.asList();
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
		if(rightType == NO_DATA && leftType == NO_DATA) {
			return DefaultUnit.ALL;
		}else if(rightType == leftType) {
			return leftType;
		}else if(NO_DATA < rightType && NO_DATA < leftType){
			return DefaultUnit.ALL;
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
	
	public List<List<Double>> getRightBuffList(){
		return rightBuffList;
	}
	
	public List<List<Double>> getLeftBuffList(){
		return leftBuffList;
	}
}