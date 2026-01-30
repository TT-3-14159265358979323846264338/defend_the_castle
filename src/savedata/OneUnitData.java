package savedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import defaultdata.DefaultUnit;

public class OneUnitData {
	/**
	 * 右武器・コア・左武器の順に格納したList
	 */
	private List<Integer> unitData = new ArrayList<>();
	
	OneUnitData(){
		reset();
	}
	
	OneUnitData(int rightNumber, int centerNUmber, int leftNumber){
		unitData.add(rightNumber);
		unitData.add(centerNUmber);
		unitData.add(leftNumber);
	}
	
	public void reset() {
		unitData.clear();
		unitData = new ArrayList<>(Arrays.asList(DefaultUnit.NO_WEAPON, DefaultUnit.NORMAL_CORE, DefaultUnit.NO_WEAPON));
	}
	
	public List<Integer> getUnitDataList(){
		return unitData;
	}
	
	public int getUnit(int index) {
		return unitData.get(index);
	}
	
	public void setUnitData(int index, int value){
		unitData.set(index, value);
	}
}