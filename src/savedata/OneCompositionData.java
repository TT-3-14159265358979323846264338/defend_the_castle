package savedata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OneCompositionData {
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
		String compositionLoad = "SELECT * FROM " + compositionName;
		try(PreparedStatement compositionPrepared = mysql.prepareStatement(compositionLoad);
				ResultSet compositionTable = compositionPrepared.executeQuery()) {
			while (compositionTable.next()) {
				unitData.add(new OneUnitData(compositionTable.getInt(RIGHT_COLUMN), compositionTable.getInt(CENTER_COLUMN), compositionTable.getInt(LEFT_COLUMN)));
			}
		}
	}
	
	void save(Connection mysql) throws Exception{
		String compositionSave = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ? WHERE %s = ?", compositionName, RIGHT_COLUMN, CENTER_COLUMN, LEFT_COLUMN, ID_COLUMN);
		try(PreparedStatement compositionPrepared = mysql.prepareStatement(compositionSave)) {
			for(int i = 0; i < unitData.size(); i++) {
				for(int j = 0; j < unitData.get(i).getUnitDataList().size(); j++) {
					compositionPrepared.setInt(j + 1, unitData.get(i).getUnitDataList().get(j));
				}
				compositionPrepared.setInt(4, i + 1);
				compositionPrepared.addBatch();
			}
			compositionPrepared.executeBatch();
		}
	}
	
	void canCreateComposition(Connection mysql, String name) throws Exception {
		try(Statement newTableStatement = mysql.createStatement()) {
			newTableStatement.executeUpdate(createTableCode(name));
		}catch (Exception e) {
			throw e;
		}
		String addDefault = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)", name, RIGHT_COLUMN, CENTER_COLUMN, LEFT_COLUMN);
		try(PreparedStatement newPrepared = mysql.prepareStatement(addDefault)) {
			for(int i = 0; i < 8; i++) {
				OneUnitData newUnitData = new OneUnitData();
				unitData.add(newUnitData);
				for(int j = 0; j < newUnitData.getUnitDataList().size(); j++) {
					newPrepared.setInt(j + 1, newUnitData.getUnitDataList().get(j));
				}
				newPrepared.addBatch();
			}
			newPrepared.executeBatch();
		}catch (Exception e) {
			throw e;
		}
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