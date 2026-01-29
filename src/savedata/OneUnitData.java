package savedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultUnit;

public class OneUnitData {
	/**
	 * 新規に作成された編成のデフォルト設定。
	 */
	public static final List<Integer> DEFAULT = Arrays.asList(DefaultUnit.NO_WEAPON, DefaultUnit.NORMAL_CORE, DefaultUnit.NO_WEAPON);
	
	/**
	 * 右武器・コア・左武器の順に格納したList
	 */
	private List<Integer> unitData = new ArrayList<>();
	
	OneUnitData(){
		DEFAULT.stream().forEach(i -> unitData.add(i));
	}
	
	OneUnitData(int rightNumber, int centerNUmber, int leftNumber){
		unitData.add(rightNumber);
		unitData.add(centerNUmber);
		unitData.add(leftNumber);
	}
	
	List<Integer> getUnitData(){
		return unitData;
	}
	
	void setUnitData(int number, int value){
		unitData.set(number, value);
	}
}