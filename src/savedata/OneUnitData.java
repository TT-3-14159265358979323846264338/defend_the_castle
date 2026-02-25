package savedata;

import java.util.ArrayList;
import java.util.List;

public class OneUnitData {
	//編成の順番コード
	public static final int RIGHT_WEAPON = 0;
	public static final int CORE = 1;
	public static final int LEFT_WEAPON = 2;
	
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
		unitData = new ArrayList<>(FileCheck.INITIAL_UNIT);
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