package savedata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class OneCompositionData extends SQLOperation{
	/**
	 * テーブルの番号
	 */
	private int id;
	
	/**
	 * テーブル名
	 */
	private String compositionName;
	
	/**
	 * 各ユニットのカスタマイズ情報
	 */
	private List<OneUnitData> unitData = new ArrayList<>();
	
	OneCompositionData(int id, String compositionName, List<Integer> unitList) {
		this.id = id;
		this.compositionName = compositionName;
		unitData = IntStream.range(0, SaveComposition.UNIT_NUMBER).mapToObj(i -> new OneUnitData(unitList.get(i * 3), unitList.get(i * 3 + 1), unitList.get(i * 3 + 2))).toList();
	}
	
	int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
	
	String getComposionName() {
		return compositionName;
	}

	void setComposionName(String newComposionName) {
		this.compositionName = newComposionName;
	}

	public List<OneUnitData> getOneUnitDataList() {
		return unitData;
	}
	
	public OneUnitData getOneUnitData(int index) {
		return unitData.get(index);
	}
}