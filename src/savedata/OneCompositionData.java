package savedata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class OneCompositionData {
	/**
	 * MySQLへの接続
	 */
	private Connection mysql;
	
	/**
	 * 元々の編成を格納したテーブル名を格納したカラム名
	 */
	private String oldComposionName;
	
	/**
	 * 変更後の編成を格納したテーブル名を格納したカラム名
	 */
	private String newComposionName;
	
	/**
	 * composionNameのテーブルの要素<br>
	 * 編成番号を格納したカラム名<br>
	 * PRIMARY KEY
	 */
	private final String NUMBER_COLUMN = "number";
	
	/**
	 * composionNameのテーブルの要素<br>
	 * 右武器番号を格納したカラム名
	 */
	private final String RIGHT_COLUMN = "right";
	
	/**
	 * composionNameのテーブルの要素<br>
	 * コア番号を格納したカラム名
	 */
	private final String CENTER_COLUMN = "center";
	
	/**
	 * composionNameのテーブルの要素<br>
	 * 左武器番号を格納したカラム名
	 */
	private final String LEFT_COLUMN = "left";
	
	/**
	 * 各ユニットのカスタマイズ情報
	 */
	private List<OneUnitData> unitData = new ArrayList<>();
	
	OneCompositionData(Connection mysql, String oldComposionName) {
		this.mysql = mysql;
		this.oldComposionName = oldComposionName;
	}
	
	void load() {
		newComposionName = oldComposionName;
		unitData.clear();
		try {
			String compositionLoad = "SELECT * FROM " + oldComposionName;
			ResultSet compositionTable = mysql.prepareStatement(compositionLoad).executeQuery();
			while (compositionTable.next()) {
				unitData.add(new OneUnitData(compositionTable.getInt(RIGHT_COLUMN), compositionTable.getInt(CENTER_COLUMN), compositionTable.getInt(LEFT_COLUMN)));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void save() {
		try {
			String renameSave = String.format("RENAME TABLE %s TO %s", oldComposionName, newComposionName);
			mysql.createStatement().executeUpdate(renameSave);
			oldComposionName = newComposionName;
			String compositionSave = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ? WHERE %s = ?", newComposionName, RIGHT_COLUMN, CENTER_COLUMN, LEFT_COLUMN, NUMBER_COLUMN);
			PreparedStatement compositionPrepared = mysql.prepareStatement(compositionSave);
			IntStream.range(0, unitData.size()).forEach(i -> {
				IntStream.range(0, unitData.get(i).getUnitData().size()).forEach(j -> {
					try {
						compositionPrepared.setInt(j + 1, unitData.get(i).getUnitData().get(j));
					}catch (Exception e) {
						e.printStackTrace();
					}
				});
				try {
					compositionPrepared.setInt(4, i + 1);
					compositionPrepared.addBatch();
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
			compositionPrepared.executeBatch();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	boolean canCreateComposition(String name) {
		newComposionName = oldComposionName;
		try {
			mysql.createStatement().executeUpdate(createTableCode(name));
			String addDefault = String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)", name, RIGHT_COLUMN, CENTER_COLUMN, LEFT_COLUMN);
			PreparedStatement newPrepared = mysql.prepareStatement(addDefault);
			IntStream.range(0, 8).forEach(i -> {
				try {
					OneUnitData newUnitData = new OneUnitData();
					unitData.add(newUnitData);
					IntStream.range(0, newUnitData.getUnitData().size()).forEach(j -> {
						
						
						
						
						
						
						
					});
					newPrepared.addBatch();
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
			newPrepared.executeBatch();
		}catch (Exception e) {
			return false;
		}
		return true;
	}
	
	String createTableCode(String name) {
		return String.format("CREATE TABLE %s ("
				+ "%s TYNYINT AUTO_INCREMENT NOT NULL PRIMARY KEY,"
				+ "%s TYNYINT NOT NULL"
				+ "%s TYNYINT NOT NULL"
				+ "%s TYNYINT NOT NULL"
				+ ")",
				name,
				NUMBER_COLUMN,
				RIGHT_COLUMN,
				CENTER_COLUMN,
				LEFT_COLUMN);
	}
	
	String getNewComposionName() {
		return newComposionName;
	}

	void setNewComposionName(String newComposionName) {
		this.newComposionName = newComposionName;
	}
}