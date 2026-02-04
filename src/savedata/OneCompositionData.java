package savedata;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class OneCompositionData extends SQLOperation{
	/**
	 * composionNameのテーブルの要素<br>
	 * 編成番号を格納したカラム名<br>
	 * PRIMARY KEY
	 */
	private final String ID_COLUMN = "id";
	
	/**
	 * composionNameのテーブルの要素<br>
	 * 右武器番号を格納したカラム名
	 */
	private final String RIGHT_COLUMN = "right_weapon";
	
	/**
	 * composionNameのテーブルの要素<br>
	 * コア番号を格納したカラム名
	 */
	private final String CENTER_COLUMN = "center_core";
	
	/**
	 * composionNameのテーブルの要素<br>
	 * 左武器番号を格納したカラム名
	 */
	private final String LEFT_COLUMN = "left_weapon";
	
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
	
	OneCompositionData(int id, String compositionName) {
		this.id = id;
		this.compositionName = compositionName;
	}
	
	void load(Connection mysql) throws Exception{
		unitData.clear();
		operateResultSet(mysql, compositionName, result -> {
			while (result.next()) {
				unitData.add(new OneUnitData(result.getInt(RIGHT_COLUMN), result.getInt(CENTER_COLUMN), result.getInt(LEFT_COLUMN)));
			}
		});
	}
	
	void save(Connection mysql) throws Exception{
		String compositionSave = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ? WHERE %s = ?", compositionName, RIGHT_COLUMN, CENTER_COLUMN, LEFT_COLUMN, ID_COLUMN);
		operatePrepared(mysql, compositionSave, prepared -> {
			for(int i = 0; i < unitData.size(); i++) {
				for(int j = 0; j < unitData.get(i).getUnitDataList().size(); j++) {
					prepared.setInt(j + 1, unitData.get(i).getUnitDataList().get(j));
				}
				prepared.setInt(4, i + 1);
				prepared.addBatch();
			}
			prepared.executeBatch();
		});
	}
	
	void canCreateComposition(Connection mysql, String name) throws Exception {
		operateStatement(mysql, createTableCode(name));
		String addDefault = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)", name, RIGHT_COLUMN, CENTER_COLUMN, LEFT_COLUMN);
		operatePrepared(mysql, addDefault, prepared -> {
			for(int i = 0; i < 8; i++) {
				OneUnitData newUnitData = new OneUnitData();
				unitData.add(newUnitData);
				for(int j = 0; j < newUnitData.getUnitDataList().size(); j++) {
					prepared.setInt(j + 1, newUnitData.getUnitDataList().get(j));
				}
				prepared.addBatch();
			}
			prepared.executeBatch();
		});
	}
	
	String createTableCode(String name) {
		return String.format("CREATE TABLE %s ("
				+ "%s TINYINT AUTO_INCREMENT NOT NULL PRIMARY KEY,"
				+ "%s TINYINT NOT NULL,"
				+ "%s TINYINT UNSIGNED NOT NULL,"
				+ "%s TINYINT NOT NULL"
				+ ")",
				name,
				ID_COLUMN,
				RIGHT_COLUMN,
				CENTER_COLUMN,
				LEFT_COLUMN);
	}
	
	int getID() {
		return id;
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