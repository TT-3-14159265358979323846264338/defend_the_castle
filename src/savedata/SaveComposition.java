package savedata;

import static javax.swing.JOptionPane.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//現在の編成状況の保存用
public class SaveComposition extends SQLOperation{
	/**
	 * データベース上で全編成を格納したテーブル名
	 */
	public static final String COMPOSITION_NAME = "all_composition";
	
	/**
	 * COMPOSITION_NAMEのテーブルの要素<br>
	 * 編成番号を格納したカラム名<br>
	 * PRIMARY KEY
	 */
	private final String ID_COLUMN = "id";
	
	/**
	 * COMPOSITION_NAMEのテーブルの要素<br>
	 * 編成名とその編成を格納したテーブル名を表す文字列を格納したカラム名
	 */
	private final String NAME_COLUMN = "name";
	
	/**
	 * 編成名の最大文字数
	 */
	public static final int MAX_WORD = 20;
	
	/**
	 * COMPOSITION_NAMEのテーブルの要素<br>
	 * 右武器番号を格納したカラム名<br>
	 * この後ろにUNIT_NUMBER番号を振る
	 */
	private final String RIGHT_COLUMN = "right_weapon_";
	
	/**
	 * COMPOSITION_NAMEのテーブルの要素<br>
	 * コア番号を格納したカラム名<br>
	 * この後ろにUNIT_NUMBER番号を振る
	 */
	private final String CENTER_COLUMN = "center_core_";
	
	/**
	 * COMPOSITION_NAMEのテーブルの要素<br>
	 * 左武器番号を格納したカラム名<br>
	 * この後ろにUNIT_NUMBER番号を振る
	 */
	private final String LEFT_COLUMN = "left_weapon_";
	
	/**
	 * 編成されたユニット最大数を表しており、0からこの数字までのカラムを作成する(0～7)
	 */
	public static final int UNIT_NUMBER = 8;
	
	/**
	 * 全ての編成情報を保存。
	 * */
	private List<OneCompositionData> allCompositionList = new ArrayList<>();
	
	public List<String> getColumnList(){
		List<String> columnList = new ArrayList<>();
		columnList.add(ID_COLUMN);
		columnList.add(NAME_COLUMN);
		columnList.addAll(getUnitColumnList());
		return columnList;
	}
	
	List<String> getUnitColumnList(){
		List<String> unitColumnList = new ArrayList<>();
		for(int i = 0; i < UNIT_NUMBER; i++) {
			unitColumnList.add(String.format("%s%d", RIGHT_COLUMN, i));
			unitColumnList.add(String.format("%s%d", CENTER_COLUMN, i));
			unitColumnList.add(String.format("%s%d", LEFT_COLUMN, i));
		}
		return unitColumnList;
	}
	
	public void load() {
		operateSQL(mysql -> {
			operateResultSet(mysql, COMPOSITION_NAME, result -> {
				allCompositionList.clear();
				List<String> unitColumnList = getUnitColumnList();
				while (result.next()) {
					List<Integer> unitList = new ArrayList<>();
					for(String i: unitColumnList) {
						unitList.add(result.getInt(i));
					}
					addComposition(result.getInt(ID_COLUMN), result.getString(NAME_COLUMN), unitList);
				}
			});
		});
	}
	
	public void addComposition(int id, String compositionName, List<Integer> unitList) {
		OneCompositionData newCompositionData = new OneCompositionData(id, compositionName, unitList);
		allCompositionList.add(newCompositionData);
	}
	
	public void save() {
		operateSQL(mysql -> {
			operatePrepared(mysql, createUpdataCode(), prepared -> {
				for(OneCompositionData i: allCompositionList) {
					int count = 1;
					prepared.setString(count, i.getComposionName());
					count++;
					for(int j = 0; j < UNIT_NUMBER; j++) {
						List<Integer> unitData = i.getOneUnitData(j).getUnitDataList();
						for(int k = 0; k < unitData.size(); k++) {
							prepared.setInt(count, unitData.get(k));
							count++;
						}
						prepared.setInt(count, i.getID());
					}
					prepared.addBatch();
				}
				prepared.executeBatch();
			});
		});
	}
	
	String createUpdataCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("UPDATE %s SET %s = ?,", COMPOSITION_NAME, NAME_COLUMN));
		List<String> unitColumnList =  getUnitColumnList();
		for(int i = 0; i < unitColumnList.size(); i++) {
			builder.append(String.format("%s = ?,", unitColumnList.get(i)));
		}
		builder.delete(builder.length() - 1, builder.length());
		builder.append(String.format(" WHERE %s = ?", ID_COLUMN));
		return builder.toString();
	}
	
	public boolean rename(int index, String name) {
		if(name == null) {
			return false;
		}
		if(MAX_WORD < name.	length()) {
			showMessageDialog(null, String.format("編成名で使用できる最大文字数は%dです。", MAX_WORD));
			return false;
		}
		getOneCompositionData(index).setComposionName(name);
		return true;
	}
	
	public void swap(int upperIndex, int lowerIndex) {
		int upperId = getID(upperIndex);
		int lowerId = getID(lowerIndex);
		setID(upperIndex, lowerId);
		setID(lowerIndex, upperId);
		Collections.swap(allCompositionList, upperIndex, lowerIndex);
	}
	
	int getID(int index) {
		return getOneCompositionData(index).getID();
	}
	
	void setID(int index, int id) {
		getOneCompositionData(index).setID(id);
	}
	
	public List<String> getCompositionNameList(){
		return allCompositionList.stream().map(i -> i.getComposionName()).toList();
	}

	public List<OneCompositionData> getAllCompositionList() {
		return allCompositionList;
	}
	
	public OneCompositionData getOneCompositionData(int index) {
		return allCompositionList.get(index);
	}
}